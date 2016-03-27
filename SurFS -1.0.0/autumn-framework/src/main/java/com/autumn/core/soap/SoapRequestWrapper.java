/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.soap;

import java.io.*;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 解压请求</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SoapRequestWrapper extends HttpServletRequestWrapper {

    private SoapFilter inSoapFilter = null;
    private ServletInputStream input = null;
    private HttpServletRequest request = null;
    private BufferedReader reader = null;

    /**
     * 读入字结束不能超过SoapFilter.MAX_CONTENT_SIZE
     *
     * @param is
     * @return ByteArrayOutputStream
     * @throws IOException
     */
    private ByteArrayOutputStream readInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] c = new byte[8192];
        int readChars;
        long count = 0;
        while ((readChars = bis.read(c)) != -1) {
            bos.write(c, 0, readChars);
            count = count + readChars;
            if (count > SoapFilter.MAX_CONTENT_SIZE) {
                throw new IOException("请求数据包大小超过".concat(String.valueOf(SoapFilter.MAX_CONTENT_SIZE)));
            }
        }
        return bos;
    }

    public SoapRequestWrapper(SoapFilter insf, HttpServletRequest req) throws IOException {
        super(req);
        this.request = req;
        this.inSoapFilter = insf;
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
            ByteArrayOutputStream bos = readInputStream(request.getInputStream());
            byte[] bs = inSoapFilter.doFilter(bos.toByteArray());
            input = new WrappedInputStream(new ByteArrayInputStream(bs));
        }
        return input;
    }

    private class WrappedInputStream extends ServletInputStream {

        private ByteArrayInputStream buffer = null;

        private WrappedInputStream(ByteArrayInputStream buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }
    }
}
