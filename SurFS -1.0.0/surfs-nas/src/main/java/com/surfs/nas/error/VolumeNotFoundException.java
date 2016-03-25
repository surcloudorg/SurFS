package com.surfs.nas.error;

import java.io.IOException;


public class VolumeNotFoundException extends IOException {

    public VolumeNotFoundException() {
        super();
    }

    public VolumeNotFoundException(String cause) {
        super(cause);
    }

    public VolumeNotFoundException(Throwable cause) {
        super(cause);
    }
}
