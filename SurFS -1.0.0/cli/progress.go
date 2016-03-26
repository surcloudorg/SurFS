/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

// Asynchrounous operation
// Copy volume operation will take very long time. To avoid hang caller
// for a long time, this operation is implemented in asynchronous
// manner. Caller start a copy command, and server will return a
// response indicating the operation is started. Then, caller can query
// the progress at any time.

import (
	"errors"
	"net/url"
)

type copyVolProgressResp struct {
	Progress string `json:"progress"`
}

// Progress of asynchronous operation.
type ProgressType int

const (
	PrgFailed ProgressType = iota
	PrgDone
	PrgRunning
	PrgNoExisted
	PrgError
)

func prgTypeFound(t ProgressType) bool {
	return t == PrgFailed || t == PrgDone || t == PrgRunning
}

func ProgressToString(t ProgressType) string {
	switch t {
	case PrgFailed:
		return "failed"
	case PrgDone:
		return "completed"
	case PrgRunning:
		return "running"
	case PrgNoExisted:
		return "notExist"
	case PrgError:
		return "unknownError"
	}
	return "unknownError"
}

func parsePrgStatusString(s string) ProgressType {
	switch s {
	case "running":
		return PrgRunning
	case "failure":
		return PrgFailed
	case "complete":
		return PrgDone
	case "not find":
		return PrgNoExisted
	default:
		return PrgError
	}
}

func (c *Client) queryCopyVolumeProgressInSingleServer(s fsServer,
	destvol string) (ProgressType, error) {

	resp := &copyVolProgressResp{}
	api := "service/block/volume/copystatus/" + url.QueryEscape(destvol)
	err := c.doGetRequest(s, api, resp)
	if err != nil {
		return PrgError, err
	}

	return parsePrgStatusString(resp.Progress), nil
}

// Get progress of copy volume operation. The copy operation is
// identified by the name of destination volume.
func (c *Client) GetCopyVolumeProgress(destvol string) (ProgressType, error) {
	ch := make(chan *singleResult, len(c.servers))
	for _, s := range c.servers {
		go func(server fsServer) {
			prg, err := c.queryCopyVolumeProgressInSingleServer(server, destvol)
			ch <- &singleResult{server, prg, err}
		}(s)
	}

	allNotExist := true
	results := collectResult(ch, len(c.servers))
	prg := PrgError
	for _, r := range results {
		if r.err != nil {
			allNotExist = false
			continue
		}

		prg1 := r.data.(ProgressType)
		if prg1 != PrgNoExisted {
			allNotExist = false
		}

		if prgTypeFound(prg1) {
			prg = prg1
			break
		}
	}

	if allNotExist {
		return PrgNoExisted, nil
	}

	if !prgTypeFound(prg) {
		return PrgError, errors.New("failed to get progress")
	}

	return prg, nil
}
