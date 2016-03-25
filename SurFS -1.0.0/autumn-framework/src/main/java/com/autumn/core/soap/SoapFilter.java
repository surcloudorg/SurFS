package com.autumn.core.soap;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 过滤器接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public interface SoapFilter {

    public static final long MAX_CONTENT_SIZE = 1024 * 1024 * 8;

    /**
     * 更改请求/回应
     *
     * @param content byte[]
     * @return byte[]
     */
    public byte[] doFilter(byte[] content);
}
