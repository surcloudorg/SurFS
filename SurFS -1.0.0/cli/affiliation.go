/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

// Affiliation
//	User can specify affiliation between servers and hostip, that is: a
//	hostip perfers some servers, if we want to create a volume used by
//	hostip, then we should prefer to create volumes on these affiliated
//	servers.

import (
	"encoding/json"
)

type affiliationMgr struct {
	entries map[string]*affiliationEntry
}

func newAffiliationMgr() *affiliationMgr {
	return &affiliationMgr{
		entries: make(map[string]*affiliationEntry),
	}
}

type affiliationEntry struct {
	HostIP        string   `json:"hostip"`
	AffiliatedIps []string `json:"afflicatedIps"`
}

func (a *affiliationMgr) Unmarshal(b []byte) error {
	entries := make([]*affiliationEntry, 0)
	err := json.Unmarshal(b, &entries)
	if err != nil {
		return err
	}

	for _, e := range entries {
		a.entries[e.HostIP] = e
	}

	return nil
}

func (a *affiliationMgr) Marshal(b []byte) ([]byte, error) {
	entries := make([]*affiliationEntry, 0)
	for _, ae := range a.entries {
		entries = append(entries, ae)
	}
	return json.Marshal(entries)
}

func (a *affiliationMgr) IsHostIPAffiliated(hostip, ip string) bool {
	if l, found := a.entries[hostip]; found {
		return containString(l.AffiliatedIps, ip)
	}
	return false
}
