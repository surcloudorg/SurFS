package com.surfs.nas.error;

import java.io.IOException;


public class ArgumentException extends IOException {

    public ArgumentException(String s) {
        super(s);
    }

    public ArgumentException(Throwable cause) {
        super(cause);
    }

}
