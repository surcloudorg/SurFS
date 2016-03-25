package com.autumn.core.soap;

import java.io.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 压缩回应</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SoapResponseWrapper extends HttpServletResponseWrapper {

    public static final int OUTPUT_NONE = 0;//Indicate that getOutputStream() or getWriter() is not called yet.
    public static final int OUTPUT_WRITER = 1;//Indicate that getWriter() is already called.
    public static final int OUTPUT_STREAM = 2;//Indicate that getOutputStream() is already called.
    private int outputType = OUTPUT_NONE;
    private int status = SC_OK;
    private ServletOutputStream output = null;
    private PrintWriter writer = null;
    private SoapFilter outSoapFilter = null;
    private HttpServletResponse response = null;

    public SoapResponseWrapper(SoapFilter outsf, HttpServletResponse resp) throws IOException {
        super(resp);
        this.response = resp;
        this.outSoapFilter = outsf;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        super.setStatus(status);
        this.status = status;
    }

    @Override
    public void setStatus(int status, String string) {
        super.setStatus(status, string);
        this.status = status;
    }

    @Override
    public void sendError(int status, String string) throws IOException {
        super.sendError(status, string);
        this.status = status;
    }

    @Override
    public void sendError(int status) throws IOException {
        super.sendError(status);
        this.status = status;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(location);
        this.status = SC_MOVED_TEMPORARILY;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputType == OUTPUT_STREAM) {
            throw new IllegalStateException();
        } else if (outputType == OUTPUT_WRITER) {
            return writer;
        } else {
            if (output == null) {
                getOutputStream();
            }
            outputType = OUTPUT_WRITER;
            writer = new PrintWriter(new OutputStreamWriter(output, getCharacterEncoding()));
            return writer;
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputType == OUTPUT_WRITER) {
            throw new IllegalStateException();
        } else if (outputType == OUTPUT_STREAM) {
            return output;
        } else {
            outputType = OUTPUT_STREAM;
            output = new BufferOutputStream();
            return output;
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if (outputType == OUTPUT_WRITER) {
            writer.flush();
            writer.close();
        }
        if (outputType == OUTPUT_STREAM) {
            output.flush();
            output.close();
        }
        if (output instanceof BufferOutputStream) {
            byte[] bs = outSoapFilter.doFilter(((BufferOutputStream) output).getBytes());
            OutputStream os = response.getOutputStream();
            os.write(bs);
            os.close();
        }
    }

    @Override
    public void reset() {
        outputType = OUTPUT_NONE;
    }

    private class BufferOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private long count = 0;

        @Override
        public void write(int b) throws IOException {
            count++;
            if (count > SoapFilter.MAX_CONTENT_SIZE) {
                throw new IOException("返回数据包大小超过".concat(String.valueOf(SoapFilter.MAX_CONTENT_SIZE)));
            }
            bos.write(b);
        }

        @Override
        public void close() throws IOException {
            bos.close();
        }

        @Override
        public void flush() throws IOException {
            bos.flush();
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            count = count + len;
            if (count > SoapFilter.MAX_CONTENT_SIZE) {
                throw new IOException("返回数据包大小超过".concat(String.valueOf(SoapFilter.MAX_CONTENT_SIZE)));
            }
            bos.write(b, off, len);
        }

        public byte[] getBytes() {
            return bos.toByteArray();
        }
    }
}
