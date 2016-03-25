package com.surfs.nas.error;


public class RecordNotFoundException extends NosqlException {

    public RecordNotFoundException(String s) {
        super(s);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }
}
