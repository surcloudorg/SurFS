package com.surfs.nas.error;

import java.io.IOException;


@com.autumn.core.ReloadForbidden
public class FileExistException extends IOException {

    public FileExistException(String cause) {
        super(cause);
    }

    public FileExistException(Throwable cause) {
        super(cause);
    }

}
