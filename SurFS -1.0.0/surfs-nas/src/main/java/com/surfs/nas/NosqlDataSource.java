/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.IOException;

public interface NosqlDataSource {

    /**
     *
     * @return MetaAccessor
     * @throws IOException
     */
    public NasMetaAccessor getNasMetaAccessor() throws IOException;

    /**
     *
     * @return ConfigDataAccessor
     * @throws IOException
     */
    public ResourcesAccessor getResourcesAccessor() throws IOException;

    /**
     * close
     */
    public void shutdown();
}
