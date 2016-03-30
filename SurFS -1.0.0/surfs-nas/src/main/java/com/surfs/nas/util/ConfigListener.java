/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.util;

public interface ConfigListener {

    /**
     *
     * @param method Method
     * @return Object
     */
    public Object callMethod(Method method);

    /**
     *
     * @param property Property
     * @return boolean true
     */
    public boolean changeProperty(Property property);
}
