package com.autumn.core.cookie;

/**
 * <p>Title: Cookie工具</p>
 *
 * <p>Description: 检查cookie接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public interface CookieMatcher {

    /**
     * 检查给定的cookie是否满足自定义标准
     * @param cookie the Cookie to be checked
     * @return 是否满足
     */
    public boolean doMatch(Cookie cookie);
}
