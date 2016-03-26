/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

import (
	"net/url"
)

// SursenFS export
//    <iqn_name>
//    <initiator_name>
//    <chap_info>
//    <volume_name>
//    <host_ip>
type ChapInfo struct {
	User     string
	Password string
}

// All information needed to export a volume
type VolExportReq struct {
	// Exported iSCSI IQN name
	IqnName string

	// Initiator name
	InitiatorName string

	// For authentication
	Chap ChapInfo

	// Pool name, may be empty
	PoolName string

	// Volume name, _MUST_ be non-empty
	VolName string

	// HostIP: used to determine a fastest server to export this volume
	HostIp string
}

type volExportResp struct {
	IP string `json:"response"`
}

type checkExportResult struct {
	Repaired []string `json:"repair"`
}

// Export a volume, returns an IP pointing to the server who export 
// the volume through iSCSI.
func (c *Client) ExportVolume(req *VolExportReq) (*ServerInfo, error) {
	loc, err := c.locateZVol(req.VolName)
	if err != nil {
		return nil, err
	}

	form := url.Values{
		"iqn":       {req.IqnName},
		"initiator": {req.InitiatorName},
		"user":      {req.Chap.User},
		"pw":        {req.Chap.Password},
		"volume":    {loc.zpool + "/" + req.VolName},
	}

	result := &volExportResp{}
	err = c.doPostRequest(loc.server.Server,
		"service/block/export", form, result)
	if err != nil {
		return nil, err
	} else {
		return loc.server, nil
	}
}

// Check the volume export status, and fix the export if it is
// in abnormal status.
func (c *Client) CheckExportVolume(req *VolExportReq) (*ServerInfo,
	*checkExportResult, error) {
	loc, err := c.locateZVol(req.VolName)
	if err != nil {
		return nil, nil, err
	}

	form := url.Values{
		"iqn":       {req.IqnName},
		"initiator": {req.InitiatorName},
		"user":      {req.Chap.User},
		"pw":        {req.Chap.Password},
		"volume":    {loc.zpool + "/" + req.VolName},
	}

	result := &checkExportResult{}
	err = c.doPostRequest(loc.server.Server,
		"service/block/export/check", form, result)
	if err != nil {
		return nil, nil, err
	} else {
		return loc.server, result, nil
	}
}

// Discard volume export.
func (c *Client) UndoExportVolume(volname string) (*ServerInfo, error) {
	loc, err := c.locateZVol(volname)
	if err != nil {
		return nil, err
	}

	result := &volExportResp{}
	form := url.Values{
		"poolvolume": {loc.zpool + "/" + volname},
	}
	err = c.doPostRequest(loc.server.Server,
		"service/block/export/disable", form, result)
	if err != nil {
		return nil, err
	} else {
		return loc.server, nil
	}
}
