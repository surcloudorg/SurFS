/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>Title: WEB框架-解压缩Request</p>
 *
 * <p>Description: WEB框架-解压缩Request</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class CompressionRequest extends HttpServletRequestWrapper {

    private ServletInputStream input = null;
    private HttpServletRequest request = null;
    private BufferedReader reader = null;
    private String contentEncoding = null;

    public CompressionRequest(HttpServletRequest req, String contentEncoding) throws IOException {
        super(req);
        this.request = req;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            getInputStream();
            reader = new BufferedReader(new InputStreamReader(input, getCharacterEncoding()));
        }
        return reader;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (input == null) {
            if (contentEncoding.equals(CompressionFilter.GZIP_ENCODING)
                    || contentEncoding.equals(CompressionFilter.X_GZIP_ENCODING)) {
                InputStream gis = new GZIPInputStream(request.getInputStream());
                input = new WrappedInputStream(gis);
            }
            if (contentEncoding.equals(CompressionFilter.COMPRESS_ENCODING)
                    || contentEncoding.equals(CompressionFilter.X_COMPRESS_ENCODING)) {
                InputStream gis = new ZipInputStream(request.getInputStream());
                input = new WrappedInputStream(gis);
            }
            if (contentEncoding.equals(CompressionFilter.DEFLATE_ENCODING)) {
                InputStream gis = new DeflaterInputStream(request.getInputStream());
                input = new WrappedInputStream(gis);
            }
        }
        return input;
    }

    class WrappedInputStream extends ServletInputStream {

        private InputStream inputStream = null;

        WrappedInputStream(InputStream is) {
            inputStream = is;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
