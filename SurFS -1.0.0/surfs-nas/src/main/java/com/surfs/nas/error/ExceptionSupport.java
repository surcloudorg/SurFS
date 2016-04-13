/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.error;

import com.surfs.nas.transport.ErrorResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
 

public class ExceptionSupport {

    /**
     *
     * @return
     */
    public static int getErrorCode() {
        return getErrorCode(null);
    }

    /**
     *
     * @param e
     * @return
     */
    public static int getErrorCode(IOException e) {
        if (e == null) {
            return 50099;
        }
        if (e instanceof NosqlException) {
            return 50080;
        } else if (e instanceof FileNotFoundException) {
            return 50001;
        } else if (e instanceof ArgumentException) {
            return 50002;
        } else if (e instanceof DiskFullException) {
            return 50003;
        } else if (e instanceof VolumeStateException) {
            return 50006;
        } else if (e instanceof SessionTimeoutException) {
            return 50007;
        } else if (e instanceof FileExistException) {
            return 50008;
        } else if (e instanceof SystemBusyException) {
            return 50011;
        } else if (e instanceof VolumeFullException) {
            return 50012;
        } else if (e instanceof VolumeBusyException) {
            return 50013;
        } else if (e instanceof VolumeNotFoundException) {
            return 50014;
        } else if (e instanceof TooManyOpenFileException) {
            return 50016;
        } else if (e instanceof PoolNotFoundException) {
            return 50017;
        } else {
            if (e.getMessage() != null && e.getMessage().contains("No space left on device")) {
                return 50012;//VolumeFullException
            } else {
                return 50000;
            }
        }
    }

    public static IOException getException(ErrorResponse tcpResponse) {
        switch (tcpResponse.getErrorCode()) {
            case 50099:
                return new IOException(tcpResponse.getErrorMessage());
            case 50000:
                return new IOException(tcpResponse.getErrorMessage());
            case 50080:
                return new NosqlException(tcpResponse.getErrorMessage());
            case 50001:
                return new FileNotFoundException(tcpResponse.getErrorMessage());
            case 50002:
                return new ArgumentException(tcpResponse.getErrorMessage());
            case 50003:
                return new DiskFullException(tcpResponse.getErrorMessage());
            case 50006:
                return new VolumeStateException(tcpResponse.getErrorMessage());
            case 50007:
                return new SessionTimeoutException(tcpResponse.getErrorMessage());
            case 50008:
                return new FileExistException(tcpResponse.getErrorMessage());
            case 50011:
                return new SystemBusyException(tcpResponse.getErrorMessage());
            case 50012:
                return new VolumeFullException(tcpResponse.getErrorMessage());
            case 50013:
                return new VolumeBusyException(tcpResponse.getErrorMessage());
            case 50014:
                return new VolumeNotFoundException(tcpResponse.getErrorMessage());
            case 50016:
                return new TooManyOpenFileException(tcpResponse.getErrorMessage());
            case 50017:
                return new PoolNotFoundException(tcpResponse.getErrorMessage());
            default:
                return new ConnectException(tcpResponse.getErrorMessage());
        }
    }
}
