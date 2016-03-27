/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-压缩/解压缩过滤器</p>
 *
 * <p>Description: response压缩/request解压缩过滤器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class CompressionFilter {

    static final String NO_ENCODING = "identity";
    static final String GZIP_ENCODING = "gzip";
    static final String X_GZIP_ENCODING = "x-gzip";
    static final String DEFLATE_ENCODING = "deflate";
    static final String COMPRESS_ENCODING = "compress";
    static final String X_COMPRESS_ENCODING = "x-compress";
    private static List<String> compressableMimeType = null;//支持的回应类型
    private static List<String> supportEncodings = new ArrayList<String>();

    /**
     * 在过滤器参数中配置支持压缩的ContentType
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        supportEncodings.add(GZIP_ENCODING);
        supportEncodings.add(X_GZIP_ENCODING);
        supportEncodings.add(DEFLATE_ENCODING);
        supportEncodings.add(COMPRESS_ENCODING);
        supportEncodings.add(X_COMPRESS_ENCODING);
        List<String> list = new ArrayList<String>();
        String ct = Initializer.servletContext.getInitParameter("compressableMimeType");
        if (ct != null) {
            StringTokenizer st = new StringTokenizer(ct, ",;|");
            while (st.hasMoreTokens()) {
                String pp = st.nextToken().trim();
                if (pp.isEmpty()) {
                    continue;
                }
                list.add(pp.toLowerCase());
            }
        }
        if (list.size() > 0) {
            compressableMimeType = list;
        }
    }

    /**
     * 检查请求的Content-Encoding,是否需要解压缩
     *
     * @param contentEncoding
     * @return boolean
     */
    private boolean isSupportedRequestContentEncoding(String contentEncoding) {
        if (contentEncoding == null) {
            return false;
        }
        contentEncoding = contentEncoding.trim().toLowerCase();
        if (contentEncoding.isEmpty() || contentEncoding.equalsIgnoreCase(NO_ENCODING)) {
            return false;
        }
        return supportEncodings.contains(contentEncoding);
    }

    /**
     * 获取包装HttpServletRequest
     *
     * @param request
     * @return HttpServletRequest
     * @throws IOException
     */
    HttpServletRequest getHttpServletRequest(ServletRequest request) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (compressableMimeType == null) {
            return req;
        }
        String contentEncoding = req.getHeader("Content-Encoding");//判断是否需要解压
        if (isSupportedRequestContentEncoding(contentEncoding)) {
            req = new CompressionRequest(req, contentEncoding);
        }
        return req;
    }

    /**
     * 获取包装HttpServletResponse
     *
     * @param request
     * @param response
     * @return HttpServletResponse
     * @throws IOException
     */
    HttpServletResponse getHttpServletResponse(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse res = (HttpServletResponse) response;
        if (compressableMimeType == null) {
            return res;
        }
        String acceptEncoding = getSupportedRequestContentEncoding(request);
        if (acceptEncoding != null) {
            res = new CompressionResponse(res, acceptEncoding);
        }
        return res;
    }

    /**
     * 根据配置判断response的ContentType是否需要压缩
     *
     * @param response
     * @return boolean
     */
    static boolean isSupportedResponseContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        contentType = contentType.toLowerCase();
        String contentTypeOnly = contentType;
        if (contentType != null) {
            int semicolonIndex = contentType.indexOf(';');
            if (semicolonIndex >= 0) {
                contentTypeOnly = contentType.substring(0, semicolonIndex);
            }
        }
        return compressableMimeType.contains(contentTypeOnly);
    }

    /**
     * 获取浏览器支持的压缩编码
     *
     * @param request HttpServletRequest
     * @return String
     */
    private String getSupportedRequestContentEncoding(ServletRequest request) {
        String acceptEncoding = ((HttpServletRequest) request).getHeader("Accept-Encoding");
        if (acceptEncoding == null) {
            return null;
        }
        acceptEncoding = acceptEncoding.toLowerCase();
        if (acceptEncoding.contains(GZIP_ENCODING)) {
            return GZIP_ENCODING;
        }
        if (acceptEncoding.contains(COMPRESS_ENCODING)) {
            return COMPRESS_ENCODING;
        }
        if (acceptEncoding.contains(DEFLATE_ENCODING)) {
            return DEFLATE_ENCODING;
        }
        return null;
    }
}
