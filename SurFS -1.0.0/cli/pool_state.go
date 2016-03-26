/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

// Pool State Manager
// Pool connection state is stored at client-side. This is a simple
// manager using a json file to store data.

import (
	"encoding/json"
)

type poolConnectEntry struct {
	Pool      string `json:"pool"`
	Connected bool   `json:"connected"`
}

type poolStateManager struct {
	entries map[string]*poolConnectEntry
}

func newPoolStateManager() *poolStateManager {
	return &poolStateManager{
		entries: make(map[string]*poolConnectEntry),
	}
}

func (c *poolStateManager) Unmarshal(b []byte) error {
	entries := make([]*poolConnectEntry, 0)
	err := json.Unmarshal(b, &entries)
	if err != nil {
		return err
	}

	for _, e := range entries {
		c.entries[e.Pool] = e
	}
	return nil
}

func (c *poolStateManager) Marshal() ([]byte, error) {
	entries := make([]*poolConnectEntry, 0)

	for _, v := range c.entries {
		entries = append(entries, v)
	}
	return json.Marshal(entries)
}

func (c *poolStateManager) poolConnected(pool string) bool {
	if cs, found := c.entries[pool]; found {
		return cs.Connected
	}
	return false
}

func (c *poolStateManager) removeNonExistedPool(pools []string) bool {
	modified := false
	for p, _ := range c.entries {
		if !containString(pools, p) {
			delete(c.entries, p)
			modified = true
		}
	}
	return modified
}

func (c *poolStateManager) markPoolsState(pools []string, connected bool) {
	for _, p := range pools {
		if cs, found := c.entries[p]; found {
			cs.Connected = connected
		} else {
			c.entries[p] = &poolConnectEntry{p, connected}
		}
	}
}
