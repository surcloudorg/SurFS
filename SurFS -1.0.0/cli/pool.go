/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

import (
	"errors"
	"fmt"
)

// Relationship between zpool and pool
// In SurFS, every zpool is mapped to a pool seen by others.

// Connect pools. This function will connect to every pool in the list,
// and return error if _ANY_ is failed.
// If pools is empty, then it will connects to all pools in SurFS
// cluster.
func (c *Client) Connect(pools []string) ([]*PoolInfo, error) {
	// check whether pool name is valid
	for _, p := range pools {
		if !c.poolExists(p) {
			return nil, fmt.Errorf("invalid pool name: %s", p)
		}
	}

	if len(pools) == 0 {
		for _, p := range c.servers {
			pools = append(pools, p.Pools...)
		}
	}
	c.poolStates.markPoolsState(pools, true)

	// fmt.Println(c.pools)
	poolsInfo, err := c.GetPoolList()
	if err == nil {
		err = c.savePoolState()
		if err != nil {
			return nil, err
		}
	}
	return poolsInfo, nil
}

// Disconnect specified pool.
func (c *Client) Disconnect(pool string) ([]*PoolInfo, error) {
	// check whether pool name is valid
	if !c.poolExists(pool) {
		return nil, fmt.Errorf("invalid pool name: %s", pool)
	}

	c.poolStates.markPoolsState([]string{pool}, false)

	// fmt.Println(c.pools)
	poolsInfo, err := c.GetPoolList()
	if err == nil {
		err = c.savePoolState()
		if err != nil {
			return nil, err
		}
	}
	return poolsInfo, nil
}

// Pool information
type PoolInfo struct {
	Success     bool     `json:"success"`
	Message     string   `json:"message,omitempty"`
	Server      fsServer `json:"-"`
	IP          string   `json:"ip"`
	Name        string   `json:"pool"`
	VolumeCount int      `json:"-"`
	Total       int64    `json:"total"`
	Used        int64    `json:"used"`
	Free        int64    `json:"free"`
}

func newErrorPoolInfo(poolname string, err error) *PoolInfo {
	return &PoolInfo{
		Name:    poolname,
		Success: false,
		Message: err.Error(),
	}
}

func newPoolInfo(server fsServer, serverInfo *ServerInfo, zpool *zpoolInfo) *PoolInfo {
	p := &PoolInfo{
		Success:     true,
		Server:      server,
		IP:          serverInfo.IP,
		Name:        zpool.ZPool,
		VolumeCount: 0,
		Total:       zpool.Total,
		Free:        zpool.Free,
		Used:        (zpool.Total - zpool.Free),
	}
	return p
}

// SursenFS disk_cab_status <PoolName>
// Get information of a pool.
func (c *Client) GetPoolInfo(poolname string) (*PoolInfo, error) {
	found := false
	var server fsServer
	for _, s := range c.servers {
		if containString(s.Pools, poolname) {
			found = true
			server = s
			break
		}
	}

	if !found {
		return nil, ErrPoolNotFound
	}

	if c.poolStates.poolConnected(poolname) == false {
		return nil, ErrPoolDisconnected
	}

	pools, err := c.getPoolListOnServers([]fsServer{server})
	if err != nil {
		return nil, err
	}

	for _, p := range pools {
		if p.Name == poolname {
			if p.Success == true {
				return p, nil
			} else {
				return nil, errors.New(p.Message)
			}
		}
	}
	return nil, ErrPoolNotFound
}

// SursenFS disk_cab_list
// Get all pools' information.
func (c *Client) GetPoolList() ([]*PoolInfo, error) {
	return c.getPoolListOnServers(c.servers)
}

func (c *Client) getPoolListOnServers(servers []fsServer) ([]*PoolInfo, error) {
	ch := make(chan *singleResult, len(servers))

	for _, s := range servers {
		go func(server fsServer) {
			zpoolList, err := c.getZPoolList(server)
			ch <- &singleResult{server, zpoolList, err}
		}(s)
	}

	pools := make([]*PoolInfo, 0, len(servers))
	results := collectResult(ch, len(servers))
	for _, r := range results {
		// All pools with same error
		if r.err != nil {
			for _, zp := range r.server.Pools {
				p := &PoolInfo{Server: r.server,
					Name:    zp,
					Success: false,
					Message: r.err.Error(),
				}
				pools = append(pools, p)
			}
			continue
		}

		zpools, ok := r.data.(*serverZPoolList)
		if !ok {
			c.logger.Fatalf("invalid pool info")
			continue
		}

		for _, sp := range r.server.Pools {
			if !c.poolStates.poolConnected(sp) {
				continue
			}
			found := false
			for _, zp := range zpools.ZPools {
				if zp.ZPool != sp {
					continue
				}
				found = true
				p := newPoolInfo(r.server, &zpools.ServerInfo, zp)
				pools = append(pools, p)
				break
			}

			if !found {
				pools = append(pools, newErrorPoolInfo(sp, ErrPoolFailure))
			}
		}
	}

	return pools, nil
}
