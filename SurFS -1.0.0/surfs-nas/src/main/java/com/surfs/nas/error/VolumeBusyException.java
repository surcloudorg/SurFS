package com.surfs.nas.error;

import java.io.IOException;


public class VolumeBusyException extends IOException {

    public VolumeBusyException() {
        super();
    }

    public VolumeBusyException(String cause) {
        super(cause);
    }

    public VolumeBusyException(Throwable cause) {
        super(cause);
    }
}
