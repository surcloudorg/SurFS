/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import com.surfs.nas.error.ExceptionSupport;
import java.io.IOException;

public final class ErrorResponse extends TcpResponse {

    private int errorCode;
    private String errorMessage;

    public ErrorResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public ErrorResponse(TcpRequest req, Exception err) {
        super(TcpCommandType.ERROR, req.getSequence());
        this.setException(err);
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        errorCode = m_in.readInt();
        errorMessage = m_in.readString();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeInt(errorCode);
        m_out.writeString(errorMessage);
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @param ex the errorCode to set
     */
    public void setException(Exception ex) {
        this.errorCode = init(ex);
        if (ex.getMessage() != null) {
            errorMessage = ex.getMessage();
        }
    }

    /**
     * @return IOException
     */
    public IOException getException() {
        return ExceptionSupport.getException(this);
    }

    /**
     * @param ex
     * @return int
     */
    private static int init(Exception ex) {
        if (ex == null) {
            return ExceptionSupport.getErrorCode();
        }
        if (ex instanceof IOException) {
            return ExceptionSupport.getErrorCode((IOException) ex);
        } else {
            return ExceptionSupport.getErrorCode();
        }
    }

    /**
     * @return the errmsg
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errmsg the errmsg to set
     */
    public void setErrorMessage(String errmsg) {
        this.errorMessage = errmsg;
    }
}
