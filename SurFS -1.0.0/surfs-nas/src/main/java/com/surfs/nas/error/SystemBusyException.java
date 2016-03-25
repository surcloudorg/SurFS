package com.surfs.nas.error;

import java.io.IOException;

public class SystemBusyException extends IOException {

    public SystemBusyException() {
        super("File system busy.");
    }

    public SystemBusyException(String cause) {
        super(cause);
    }

    public SystemBusyException(Throwable cause) {
        super(cause);
    }
}
