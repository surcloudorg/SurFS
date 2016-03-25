package surfs

// This file conatins ZFS related things. 
// We using following mapping between ZFS and general block storage.
// ZFS POOL     --> POOL
// ZFS VOLUME   --> VOLUME
// ZFS SNAPSHOT --> SNAPSHOT

import (
	"fmt"
	"net/url"
	"strings"
)

// Information of a ZFS pool
type zpoolInfo struct {
	ZPool string `json:"pool"`
	Total int64  `json:"total"`
	Free  int64  `json:"free"`
	CTime int64  `json:"ctime"`
	IP    string `json:"ip"`
}

type zvolInfo struct {
	Name  string `json:"vol"`
	Cap   int64  `json:"cap"`
	CTime int    `json:"ctime"`
}

// Server summary info
type ServerInfo struct {
	Total  int64    `json:"total"`
	Free   int64    `json:"free"`
	Used   int64    `json:"used"`
	CTime  int64    `json:"ctime"`
	IP     string   `json:"ip"`
	Server fsServer `json:"-"`
}

type serverZPoolList struct {
	ServerInfo
	ZPools []*zpoolInfo `json:"pools"`
}

type serverZVolList struct {
	ServerInfo
	Volumes []*zvolInfo `json:"vols"`
}

func (c *Client) getServerInfo(server fsServer) (*ServerInfo, error) {
	r := &ServerInfo{}
	err := c.doGetRequest(server, "service/block/pool/status", r)
	if err != nil {
		return nil, err
	}

	r.Server = server
	return r, nil
}

func (c *Client) getZPoolList(server fsServer) (*serverZPoolList, error) {
	r := &serverZPoolList{}
	err := c.doGetRequest(server, "service/block/pool/list", r)
	if err != nil {
		return nil, err
	}

	r.ServerInfo.Server = server
	return r, nil
}

func (c *Client) getZPoolInfo(server fsServer,
	poolName string) (*zpoolInfo, error) {
	r := &zpoolInfo{}
	err := c.doGetRequest(server,
		"service/block/pool/list/"+url.QueryEscape(poolName), r)
	if err != nil {
		return nil, err
	}

	return r, nil
}

func (c *Client) getZVolList(server fsServer) (*serverZVolList, error) {
	r := &serverZVolList{}
	err := c.doGetRequest(server, "service/block/volume/list", r)
	if err != nil {
		return nil, err
	}

	r.ServerInfo.Server = server
	return r, nil
}

type zsnapInfo struct {
	Name    string `json:"name"`
	Size    int64  `json:"size"`
	SrcSize int64  `json:"srcSize"`
	CTime   int    `json:"ctime"`
}

type serverZSnapList struct {
	ServerInfo
	ZSnaps []*zsnapInfo `json:"snaps"`
}

func (c *Client) getZVolSnapshotList(server fsServer) (*serverZSnapList, error) {
	r := &serverZSnapList{}
	err := c.doGetRequest(server, "service/block/snapshot/list", r)
	if err != nil {
		return nil, err
	}
	r.Server = server

	// We need get source volume size for snaps
	zvols, err := c.getZVolList(server)
	if err != nil {
		return nil, err
	}

	for _, zs := range r.ZSnaps {
		_, zvol, _, err := parseZSnapName(zs.Name)
		if err != nil {
			c.logger.Printf("invalid snap name: %s", zs.Name)
			continue
		}

		matches := findVolFromList(zvols, zvol)
		if len(matches) == 0 {
			c.logger.Printf("no source volume for snap: %s", zs.Name)
			continue
		}

		zs.SrcSize = matches[0].zvol.Cap
	}

	return r, err
}

func parseZVolName(name string) (zpool, zvol string, err error) {
	parts := strings.Split(name, "/")
	if len(parts) != 2 {
		return "", "", fmt.Errorf("invalid zfs snapshot name: %s", name)
	}
	zpool, zvol = parts[0], parts[1]
	return
}

func parseZSnapName(name string) (zpool, zvol, zsnap string, err error) {
	parts := strings.Split(name, "/")
	if len(parts) != 2 {
		return "", "", "", fmt.Errorf("invalid zfs snapshot name: %s", name)
	}
	zpool = parts[0]
	parts2 := strings.Split(parts[1], "@")
	if len(parts) != 2 {
		return "", "", "", fmt.Errorf("invalid zfs snapshot name: %s", name)
	}
	zvol, zsnap = parts2[0], parts2[1]
	return
}

func zfsBlockAlignedSize(size int64) int64 {
	// ZFS only allow create volume with size of multiple of blocksize
	const k128K = 1024 * 128
	return (size/k128K + 1) * k128K
}
