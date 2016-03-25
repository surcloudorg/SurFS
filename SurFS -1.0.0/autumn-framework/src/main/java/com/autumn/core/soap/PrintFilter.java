package com.autumn.core.soap;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 输出数据包内容</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class PrintFilter implements SoapFilter {

    private final Logger log = LogFactory.getLogger(getClass());

    @Override
    public byte[] doFilter(byte[] content) {
        try {
            String res = new String(content, "UTF-8");
            log.info(res);
        } catch (Exception e) {
        }
        return content;
    }
}
