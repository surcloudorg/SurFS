package surfs

import (
	"errors"
	"fmt"
	"net/url"
)

// Snapshot information
type SnapshotInfo struct {
	IP       string `json:"ip"`
	Pool     string `json:"pool"`
	Volume   string `json:"volume"`
	Snapshot string `json:"snapshot"`
	Size     int64  `json:"size"`
	SrcSize  int64  `json:"srcSize"`
	CTime    int    `json:"ctime"`
}

// Create a snapshot.
func (c *Client) CreateSnapshot(volname, snapname string) (*ServerInfo,
	*SnapshotInfo, error) {
	loc, err := c.locateZVol(volname)
	if err != nil {
		return nil, nil, err
	}

	form := url.Values{
		"poolvolume": {loc.zpool + "/" + volname},
		"snapshot":   {snapname},
	}

	err = c.doPostRequest(loc.server.Server,
		"service/block/snapshot/create", form, nil)

	if err != nil {
		return nil, nil, err
	}

	// Get snap information
	zsnaps, err := c.getZVolSnapshotList(loc.server.Server)
	if err != nil {
		return nil, nil, errors.New("snapshot created but failed to get its info")
	}
	zsnapInfo := c.findSnapFromList(snapname, zsnaps)
	if zsnapInfo == nil {
		return nil, nil, errors.New("snapshot created but failed to get its info")
	}

	snapInfo, err := zsnapToSnapInfo(loc.server, zsnapInfo.zsnap)
	if err != nil {
		return nil, nil, errors.New("snapshot created but failed to get its info")
	}

	return loc.server, snapInfo, nil
}

// List snapshots in a pool.
func (c *Client) ListSnapshot(poolname, snapname string) ([]*SnapshotInfo, error) {
	poolinfo, err := c.GetPoolInfo(poolname)
	if err != nil {
		return nil, err
	}

	zsnaps, err := c.getZVolSnapshotList(poolinfo.Server)
	if err != nil {
		return nil, err
	}

	snaps := make([]*SnapshotInfo, 0)
	for _, zs := range zsnaps.ZSnaps {
		s, err := zsnapToSnapInfo(&zsnaps.ServerInfo, zs)
		if err != nil {
			c.logger.Printf("invalid snap entry: %v", err)
		}
		snaps = append(snaps, s)
	}

	return snaps, nil
}

// Delete a snapshot
func (c *Client) DeleteSnapshot(snapname string) (*ServerInfo, error) {
	loc, err := c.locateZSnap(snapname)
	if err != nil {
		return nil, err
	}

	form := url.Values{
		"poolvolsnapshot": {loc.zsnap.Name},
	}

	err = c.doPostRequest(loc.server.Server,
		"service/block/snapshot/delete", form, nil)
	if err != nil {
		return nil, err
	}

	return loc.server, nil
}

type snapLocation struct {
	server *ServerInfo
	zpool  string
	zvol   string
	zsnap  *zsnapInfo
}

func (c *Client) findSnapFromList(snapname string, snaps *serverZSnapList) *snapLocation {
	for _, s := range snaps.ZSnaps {
		zpool, zvol, snap, err := parseZSnapName(s.Name)
		if err != nil {
			continue
		}
		if !c.poolStates.poolConnected(zpool) {
			continue
		}

		if snap == snapname {
			return &snapLocation{&snaps.ServerInfo, zpool, zvol, s}
		}
	}

	return nil
}

func (c *Client) locateZSnap(snapname string) (*snapLocation, error) {
	ch := make(chan *singleResult, len(c.servers))
	for _, s := range c.servers {
		go func(server fsServer) {
			snaps, err := c.getZVolSnapshotList(server)
			ch <- &singleResult{server, snaps, err}
			if err == nil && c.findSnapFromList(snapname, snaps) != nil {
				// We found it, don't wait for others
				ch <- nil
			}
		}(s)
	}

	results := collectResult(ch, len(c.servers))
	for _, r := range results {
		if r.err != nil {
			continue
		}
		if snaps, ok := r.data.(*serverZSnapList); ok {
			if loc := c.findSnapFromList(snapname, snaps); loc != nil {
				return loc, nil
			}
		}
	}

	return nil, ErrSnapshotNotFound
}

func (c *Client) SnapshotToVolume(snapname, volname string,
	size int64) (*ServerInfo, error) {
	loc, err := c.locateZSnap(snapname)
	if err != nil {
		return nil, err
	}

	zvol, err := c.locateZVol(loc.zvol)
	if err != nil {
		c.logger.Printf("failed to get volume of snapshot: %v", err)
		return nil, err
	}

	if size < zvol.zvol.Cap {
		return nil, errors.New("volume size is smaller than snapshot size")
	}

	size = zfsBlockAlignedSize(size)
	bestzpool, err := c.getBestPoolToCopyVolume(loc.server.Server,
		size, loc.zpool)
	if err != nil {
		return nil, err
	}

	form := url.Values{
		"poolvolumesnapshot": {loc.zsnap.Name},
		"poolvolume":         {bestzpool.zpool.ZPool + "/" + volname},
		"size":               {fmt.Sprintf("%d", size)},
	}

	err = c.doPostRequest(loc.server.Server,
		"service/block/snapshot/generate", form, nil)
	if err != nil {
		return nil, err
	}

	return loc.server, nil
}

func zsnapToSnapInfo(server *ServerInfo, zsnap *zsnapInfo) (*SnapshotInfo, error) {
	zpool, zvol, snap, err := parseZSnapName(zsnap.Name)
	if err != nil {
		return nil, err
	}
	s := &SnapshotInfo{
		IP:       server.IP,
		Pool:     zpool,
		Volume:   zvol,
		Snapshot: snap,
		Size:     zsnap.Size,
		SrcSize:  zsnap.SrcSize,
		CTime:    zsnap.CTime,
	}
	return s, nil
}
