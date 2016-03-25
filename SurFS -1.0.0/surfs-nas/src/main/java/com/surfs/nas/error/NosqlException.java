package com.surfs.nas.error;

import java.io.IOException;

public class NosqlException extends IOException {

    public NosqlException(String s) {
        super(s);
    }

    public NosqlException(Throwable cause) {
        super(cause);
    }

}
