package com.surfs.nas.error;

import java.io.IOException;


@com.autumn.core.ReloadForbidden
public class PoolNotFoundException extends IOException {

    public PoolNotFoundException(String cause) {
        super(cause);
    }

    public PoolNotFoundException(Throwable cause) {
        super(cause);
    }

}
