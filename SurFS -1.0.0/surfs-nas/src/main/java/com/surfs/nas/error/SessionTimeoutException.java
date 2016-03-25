package com.surfs.nas.error;

import java.io.IOException;


public class SessionTimeoutException extends IOException {

    public SessionTimeoutException() {
        super("The session timeout.");
    }

    public SessionTimeoutException(String cause) {
        super(cause);
    }

    public SessionTimeoutException(Throwable cause) {
        super(cause);
    }
}
