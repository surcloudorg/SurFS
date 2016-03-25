package com.surfs.nas.error;

import java.io.IOException;


public class VolumeStateException extends IOException {

    public VolumeStateException() {
        super("volume state error.");
    }

    public VolumeStateException(String cause) {
        super(cause);
    }

    public VolumeStateException(Throwable cause) {
        super(cause);
    }
}
