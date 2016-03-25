package com.surfs.nas.error;


public class DuplicateKeyException extends NosqlException {

    public DuplicateKeyException(String s) {
        super(s);
    }

    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
