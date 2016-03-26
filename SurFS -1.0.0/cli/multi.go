/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

type singleResult struct {
	server fsServer
	data   interface{}
	err    error
}

func collectResult(c <-chan *singleResult, n int) []*singleResult {
	out := make([]*singleResult, 0, n)
	for i := 0; i < n; i++ {
		r, ok := <-c
		if !ok || r == nil {
			return out
		}
		out = append(out, r)
	}
	return out
}
