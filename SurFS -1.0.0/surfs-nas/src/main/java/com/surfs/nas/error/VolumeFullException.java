package com.surfs.nas.error;

import java.io.IOException;

public class VolumeFullException extends IOException {

    public VolumeFullException() {
        super("volume space full.");
    }

    public VolumeFullException(String cause) {
        super(cause);
    }

    public VolumeFullException(Throwable cause) {
        super(cause);
    }
}
