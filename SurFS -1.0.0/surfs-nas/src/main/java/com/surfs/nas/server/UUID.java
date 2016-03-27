/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

public class UUID {

    public static String makeUUID() {
        java.util.UUID id = java.util.UUID.randomUUID();
        return Long.toHexString(id.getMostSignificantBits()) + Long.toHexString(id.getLeastSignificantBits());
    }
}
