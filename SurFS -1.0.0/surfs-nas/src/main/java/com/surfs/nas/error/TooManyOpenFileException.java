package com.surfs.nas.error;

import java.io.IOException;


public class TooManyOpenFileException extends IOException {

    public TooManyOpenFileException() {
        super();
    }

    public TooManyOpenFileException(String cause) {
        super(cause);
    }

    public TooManyOpenFileException(Throwable cause) {
        super(cause);
    }
}
