/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

// The logic of pool selection when creating a volume.
// 1. If user specify the pool name when creating volume, then we use
//    specified pool
// 2. Else if user do not specify hostip, then choose a pool with max free space
// 3. Else we choose a server which is closest to hostip.
//     a. if hostip has affiliated servers, then choose a affiliated server
//     b. else choose a "closed" server to hostip

import (
	"errors"
	"fmt"
	"io"
	"net/url"
	"sort"
	"sync"
)

// Volume information
type VolumeInfo struct {
	Name   string   `json:"name"`
	Server fsServer `json:"-"`
	Pool   string   `json:"pool"`
	Size   int64    `json:"size"`
	CTime  int      `json:"-"`
}

// SursenFS create
//    -V <size>
//    [- P host_ip]
//    <poolname/volumename | volumename>
func (c *Client) CreateVolume(pool string, volname string,
	size int64, hostip string) (*VolumeInfo, error) {
	// Check whether there is already a volume with same name
	_, err := c.locateZVol(volname)
	if err == nil {
		return nil, ErrVolumeAlreadyExists
	} else {
		if err != ErrVolumeNotFound {
			return nil, err
		}
	}

	// User give us a specific pool to create volume
	if len(pool) > 0 {
		for _, s := range c.servers {
			if containString(s.Pools, pool) {
				return c.tryCreateVolume(s, pool, volname, size)
			}
		}
		return nil, ErrPoolNotFound
	}

	size = zfsBlockAlignedSize(size)
	pools, err := c.getCandidatePools(pool, size, hostip)
	if err != nil {
		return nil, err
	}

	// pools.dump(os.Stdout)
	if pools.explictPool != nil {
		return c.tryCreateVolume(pools.explictPool.s,
			pools.explictPool.pool.Name, volname, size)
	}

	if pools.hostipPool != nil {
		v, err := c.tryCreateVolume(pools.hostipPool.s,
			pools.hostipPool.pool.Name, volname, size)
		if err == nil {
			return v, nil
		}
	}

	v, err := c.tryCreateVolumeOnPools(pools.affiliation, volname, size)
	if err == nil {
		return v, nil
	}

	// Get latency
	if len(hostip) > 0 {
		c.getPoolsLatency(pools.others, hostip)
	}

	// sort by latency and free space
	sort.Sort(pools.others)
	return c.tryCreateVolumeOnPools(pools.others, volname, size)
}

type VolLocation struct {
	server *ServerInfo
	zpool  string
	zvol   *zvolInfo
}

// Find the server which the volume locates in.
func (c *Client) LocateVolume(volname string) (*ServerInfo, *VolumeInfo, error) {
	loc, err := c.locateZVol(volname)
	if err != nil {
		return nil, nil, err
	}

	v := new(VolumeInfo)
	zpool, zvolname, err := parseZVolName(loc.zvol.Name)
	if err != nil {
		c.logger.Print(err)
		return nil, nil, errors.New("invalid volume name")
	}

	if containString(loc.server.Server.Pools, zpool) == false {
		return nil, nil, ErrVolumeNotFound
	}

	v.Name = zvolname
	v.Size = loc.zvol.Cap
	v.Pool = zpool
	return loc.server, v, nil
}

// Get all volumes of a pool.
func (c *Client) GetVolumeListOfPool(poolname string) ([]*VolumeInfo, error) {
	pool, err := c.GetPoolInfo(poolname)
	if err != nil {
		return nil, err
	}

	zvols, err := c.getZVolList(pool.Server)
	if err != nil {
		return nil, err
	}
	vols := make([]*VolumeInfo, 0, len(zvols.Volumes))
	for _, zv := range zvols.Volumes {
		v := new(VolumeInfo)
		// v.Server = zvols.Server
		zpool, zvolname, err := parseZVolName(zv.Name)
		if err != nil {
			c.logger.Print(err)
			continue
		}
		if zpool != poolname {
			continue
		}
		v.Name = zvolname
		v.Size = zv.Cap
		v.Pool = zpool
		v.Server = zvols.Server
		vols = append(vols, v)
	}
	return vols, nil

}

// Delete a volume
func (c *Client) DeleteVolume(volname string) (*ServerInfo, error) {
	loc, err := c.locateZVol(volname)
	if err != nil {
		return nil, err
	}

	form := url.Values{
		"poolvolume": {loc.zpool + "/" + volname},
	}

	err = c.doPostRequest(loc.server.Server,
		"service/block/volume/delete", form, nil)
	if err != nil {
		return nil, err
	}

	return loc.server, nil
}

// Copy a volume to destination.
// In current implementation, we can not copy a volume to a pool which
// locates in different fs server.
func (c *Client) CopyVolume(src, dest string, overwrite bool) (*ServerInfo, error) {
	srcLoc, err := c.locateZVol(src)
	if err != nil {
		return nil, errors.New("failed to find src volume")
	}

	destLoc, err := c.locateZVol(dest)
	if err == nil && overwrite == false {
		return nil, errors.New("target volume already exists")
	}

	var targetZpool string
	if destLoc != nil {
		if destLoc.server.Server.HttpAddr != srcLoc.server.Server.HttpAddr {
			return nil,
				errors.New("copy volume to different server is not supported")
		}
		targetZpool = destLoc.zpool
	} else {
		best, err := c.getBestPoolToCopyVolume(srcLoc.server.Server,
			srcLoc.zvol.Cap, "")
		if err != nil {
			return nil, err
		}

		targetZpool = best.zpool.ZPool
	}

	form := url.Values{
		"sourcepoolvolume": {srcLoc.zpool + "/" + src},
		"destpoolvolume":   {targetZpool + "/" + dest},
		"cover":            {boolString(overwrite)},
	}

	type copyVolResult struct {
		Status   int    `json:"status"`
		Progress string `json:"progress"`
	}

	resp := &copyVolResult{}
	err = c.doPostRequest(srcLoc.server.Server,
		"service/block/volume/copy", form, resp)

	if err != nil {
		return nil, err
	}

	return srcLoc.server, nil
}

type zvolCreateResp struct {
	IP string `json:"ip"`
}

type candiate struct {
	server *ServerInfo
	zpool  *zpoolInfo
}

type candidateList []*candiate

func (z candidateList) Len() int {
	return len(z)
}

func (z candidateList) Swap(i, j int) {
	z[i], z[j] = z[j], z[i]
}

func (z candidateList) Less(i, j int) bool {
	return z[i].zpool.Free < z[j].zpool.Free
}

func (c *Client) createZVolumeOnServer(server fsServer, zpool, volname string,
	size int64) (*VolumeInfo, error) {
	r := &zvolCreateResp{}
	form := url.Values{
		"pool":   {zpool},
		"volume": {volname},
		"size":   {fmt.Sprintf("%d", size)},
	}

	err := c.doPostRequest(server, "service/block/volume/create", form, &r)
	/*
		api := fmt.Sprintf("service/block/volume/create/%s/%s/%d",
			zpool, volname, size)
		err := c.doUglyPost(server, api, &r)
	*/

	if err != nil {
		return nil, err
	}

	v := new(VolumeInfo)
	v.Name = volname
	v.Server = server
	v.Size = size

	return v, nil
}

type candidatePool struct {
	s       fsServer
	latency float64
	pool    *PoolInfo
}

type candidatePoolList []*candidatePool

func (c candidatePoolList) Len() int      { return len(c) }
func (c candidatePoolList) Swap(i, j int) { c[i], c[j] = c[j], c[i] }
func (c candidatePoolList) Less(i, j int) bool {
	if c[i].latency != c[j].latency {
		return c[i].latency < c[j].latency
	} else {
		return c[i].pool.Free > c[j].pool.Free
	}
}

type candidateServersByType struct {
	explictPool *candidatePool
	hostipPool  *candidatePool
	affiliation candidatePoolList
	others      candidatePoolList
}

func (c *candidateServersByType) dump(w io.Writer) {
	if c.explictPool != nil {
		fmt.Fprintf(w, "Explict pool: %s\n", c.explictPool.pool.Name)
	}
	if c.hostipPool != nil {
		fmt.Fprintf(w, "HostIP pool: %s\n", c.hostipPool.pool.Name)
	}
	if len(c.affiliation) != 0 {
		fmt.Fprintf(w, "Affiliation pools: %d\n", len(c.affiliation))
	}
	if len(c.others) != 0 {
		fmt.Fprintf(w, "Other pools: %d\n", len(c.affiliation))
	}
}

func (c *Client) getCandidatePools(pool string, size int64,
	hostip string) (*candidateServersByType, error) {
	servers := &candidateServersByType{}
	pools, err := c.GetPoolList()
	if err != nil {
		return nil, err
	}

	for _, p := range pools {
		if len(pool) != 0 && p.Name == pool {
			if !p.Success {
				return nil, ErrPoolFailure
			} else if p.Free < size {
				return nil, ErrNoSpace
			} else {
				servers.explictPool =
					&candidatePool{p.Server, 0, p}
				return servers, nil
			}
		}

		if !p.Success {
			continue
		}

		if p.Free < size {
			continue
		}

		if len(hostip) != 0 && hostip == p.IP {
			servers.hostipPool = &candidatePool{p.Server, 0, p}
		} else if c.affiliation.IsHostIPAffiliated(hostip, p.IP) {
			servers.affiliation = append(servers.affiliation,
				&candidatePool{p.Server, 0, p})
		} else {
			servers.others = append(servers.affiliation,
				&candidatePool{p.Server, 0, p})
		}
	}

	return servers, nil
}

func (c *Client) tryCreateVolume(s fsServer, zpool, volname string, size int64) (*VolumeInfo, error) {
	v, err := c.createZVolumeOnServer(s, zpool, volname, size)
	if err == nil {
		v.Pool = zpool
		return v, err
	} else {
		return nil, err
	}
}

func (c *Client) tryCreateVolumeOnPools(servers candidatePoolList,
	volname string, size int64) (*VolumeInfo, error) {
	for _, s := range servers {
		v, err := c.tryCreateVolume(s.s, s.pool.Name, volname, size)
		if err == nil {
			return v, nil
		}
	}

	return nil, ErrNoSpace
}

func (c *Client) getPoolsLatency(pools candidatePoolList, hostip string) {
	var wg sync.WaitGroup

	servers := make(map[string]float64)
	var lock sync.Mutex
	for _, p := range pools {
		if _, found := servers[p.s.HttpAddr]; found {
			continue
		}
		wg.Add(1)
		go func(server fsServer) {
			c.logger.Printf("get latency of server: %s\n", server.HttpAddr)
			defer wg.Done()
			latency := c.getLatency(server, hostip)
			lock.Lock()
			servers[server.HttpAddr] = latency
			lock.Unlock()
		}(p.s)
	}

	for _, p := range pools {
		p.latency = servers[p.s.HttpAddr]
	}

	wg.Wait()
}

func findVolFromList(vols *serverZVolList, volname string) []*VolLocation {
	locs := make([]*VolLocation, 0)
	for _, v := range vols.Volumes {
		zpool, zvol, err := parseZVolName(v.Name)
		if err == nil && zvol == volname {
			locs = append(locs, &VolLocation{&vols.ServerInfo, zpool, v})
		}
	}
	return locs
}

func (c *Client) locateZVol(volname string) (*VolLocation, error) {
	ch := make(chan *singleResult, len(c.servers))
	for _, s := range c.servers {
		go func(server fsServer) {
			vols, err := c.getZVolList(server)
			ch <- &singleResult{server, vols, err}
			if err == nil {
				if locs := findVolFromList(vols, volname); len(locs) > 0 {
					ch <- nil
				}
			}
		}(s)
	}

	locs := make([]*VolLocation, 0)
	results := collectResult(ch, len(c.servers))
	for _, r := range results {
		if r.err != nil {
			continue
		}

		if vols, ok := r.data.(*serverZVolList); ok {
			locsOfThisServer := findVolFromList(vols, volname)
			locs = append(locs, locsOfThisServer...)
		}
	}

	if len(locs) == 0 {
		return nil, ErrVolumeNotFound
	} else if len(locs) == 1 {
		return locs[0], nil
	} else {
		return nil, ErrDuplicatedVolName
	}
}

func (c *Client) getBestPoolToCopyVolume(server fsServer,
	size int64, zpool string) (*candiate, error) {
	candidates := make([]*candiate, 0)
	zpools, err := c.getZPoolList(server)
	if err != nil {
		return nil, errors.New("failed to get pools from server")
	}

	for _, zp := range zpools.ZPools {
		if zp.Free > size {
			candidates = append(candidates, &candiate{&zpools.ServerInfo, zp})
		}
	}

	if len(candidates) == 0 {
		return nil, errors.New("no free space to copy volume")
	}
	sort.Sort(candidateList(candidates))

	// Prefer same zpool if there is enough free space
	if len(zpool) != 0 {
		for _, d := range candidates {
			if d.zpool.ZPool == zpool {
				return d, nil
			}
		}
	}

	return candidates[0], nil
}
