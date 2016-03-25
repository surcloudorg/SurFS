package com.surfs.nas.error;

import java.io.IOException;

public class DiskFullException extends IOException {

    public DiskFullException(String s) {
        super(s);
    }

    public DiskFullException(Throwable cause) {
        super(cause);
    }
}
