package com.autumn.core.cookie;

import java.net.URL;

/**
 * <p>Title: Cookie工具</p>
 *
 * <p>Description: 接口定义-解析/生成HttpHeader中的cookie字段</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public interface CookieParser {

    /**
     * cookie对象转换为字符串
     *
     * @param cj
     * @return Header
     */
    public Header getCookieHeaders(CookieJar cj);

    /**
     * 检测是否允许保存cookie
     *
     * @param c
     * @param url
     * @return boolean
     */
    public boolean allowedCookie(Cookie c, URL url);

    /**
     * 字符串转换为cookie对象
     *
     * @param h
     * @param url
     * @return CookieJar
     * @throws MalformedCookieException
     */
    public CookieJar parseCookies(Header h, URL url) throws MalformedCookieException;

    /**
     * 检测是否能为url设置cookie
     *
     * @param c
     * @param url
     * @param bRespectExpires
     * @return boolean
     */
    public boolean sendCookieWithURL(Cookie c, URL url, boolean bRespectExpires);
}
