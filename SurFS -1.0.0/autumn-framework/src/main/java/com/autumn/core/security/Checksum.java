/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

public interface Checksum {

    public void update(byte[] buf, int index, int len);

    public void reset();

    public long getValue();

    public byte[] toBytes();
}
