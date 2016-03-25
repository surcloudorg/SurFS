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
