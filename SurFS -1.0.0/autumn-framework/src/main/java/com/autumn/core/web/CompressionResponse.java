/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>Title: WEB框架-压缩Response</p>
 *
 * <p>Description: WEB框架-压缩Response</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class CompressionResponse extends HttpServletResponseWrapper {

    public static final int OUTPUT_NONE = 0;//Indicate that getOutputStream() or getWriter() is not called yet.
    public static final int OUTPUT_WRITER = 1;//Indicate that getWriter() is already called.
    public static final int OUTPUT_STREAM = 2;//Indicate that getOutputStream() is already called.
    private int outputType = OUTPUT_NONE;
    private int status = SC_OK;
    private ServletOutputStream output = null;
    private PrintWriter writer = null;
    private HttpServletResponse response = null;
    private String acceptEncoding;
    private int savedContentLength = 0;
    private String savedContentEncoding = null;

    public CompressionResponse(HttpServletResponse resp, String acceptEncoding) throws IOException {
        super(resp);
        this.response = resp;
        this.acceptEncoding = acceptEncoding;
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
            boolean contentTypeOK = CompressionFilter.isSupportedResponseContentType(response.getContentType());
            if (contentTypeOK) {
                response.setHeader("Content-Encoding", acceptEncoding);
                response.setHeader("Vary", "Accept-Encoding");
            } else {
                if (savedContentEncoding != null) {
                    response.setHeader("Content-Encoding", savedContentEncoding);
                }
                if (savedContentLength > 0) {
                    response.setIntHeader("Content-Length", savedContentLength);
                }
            }
            if (contentTypeOK) {
                if (acceptEncoding.equals(CompressionFilter.GZIP_ENCODING)
                        || acceptEncoding.equals(CompressionFilter.X_GZIP_ENCODING)) {
                    output = new WrappedOutputStream(new GZIPOutputStream(response.getOutputStream()));
                } else if (acceptEncoding.equals(CompressionFilter.COMPRESS_ENCODING)
                        || acceptEncoding.equals(CompressionFilter.X_COMPRESS_ENCODING)) {
                    output = new WrappedOutputStream(new ZipOutputStream(response.getOutputStream()));
                } else if (acceptEncoding.equals(CompressionFilter.DEFLATE_ENCODING)) {
                    output = new WrappedOutputStream(new DeflaterOutputStream(response.getOutputStream()));
                } else {
                    output = response.getOutputStream();
                }
            } else {
                output = response.getOutputStream();
            }
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
    }

    @Override
    public void addHeader(String name, String value) {
        setHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        setIntHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        if ("Content-Length".equalsIgnoreCase(name)) {
            setContentLength(value);
        }
    }

    @Override
    public void setHeader(String name, String value) {
        if ("Content-Encoding".equalsIgnoreCase(name)) {
            if (output == null) {
                this.savedContentEncoding = value;
            }
        } else if ("Content-Length".equalsIgnoreCase(name)) {
            setContentLength(Integer.parseInt(value));
        } else if ("Content-Type".equalsIgnoreCase(name)) {
            setContentType(value);
        } else {
            response.setHeader(name, value);
        }
    }

    @Override
    public void setContentLength(int contentLength) {
        if (output == null) {
            savedContentLength = contentLength;
        }
    }

    @Override
    public void setContentType(String contentType) {
        if (output == null) {
            this.response.setContentType(contentType);
        }
    }

    @Override
    public void reset() {
        outputType = OUTPUT_NONE;
        output = null;
        response.reset();
        savedContentLength = 0;
        savedContentEncoding = null;
    }

    private class WrappedOutputStream extends ServletOutputStream {

        private OutputStream out = null;

        private WrappedOutputStream(OutputStream out) throws IOException {
            this.out = out;
        }

        @Override
        public void close() throws IOException {
            out.close();
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            out.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }
    }
}
