package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.util.Function;
import com.autumn.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 输出公共资源</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SourceServlet {

    private static final ConcurrentHashMap<String, byte[]> map = new ConcurrentHashMap<String, byte[]>();//资源
    private static final ConcurrentHashMap<String, String> typemap = new ConcurrentHashMap<String, String>();//资源类型

    static synchronized void init() {
        ZipInputStream in = null;
        try {
            URL url = SourceServlet.class.getResource("/com/autumn/jsp/res.zip");
            if (url != null) {
                in = new ZipInputStream(url.openStream());
                ZipEntry z = in.getNextEntry();
                while (z != null) {
                    if ((!z.isDirectory()) && (!z.getName().endsWith("/"))) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        int b;
                        while ((b = in.read()) != -1) {
                            os.write(b);
                        }
                        byte[] bs = os.toByteArray();
                        String type = Function.getContentType(bs);
                        map.put(z.getName(), bs);
                        typemap.put(z.getName(), type);
                    }
                    z = in.getNextEntry(); //读取下一个ZipEntry
                }
                LogFactory.warn("解压缩[{0}]完毕！", new Object[]{"/com/autumn/jsp/res.zip"}, SourceServlet.class);
            }
        } catch (IOException ex) {
            LogFactory.error("解压缩[{0}]错误:[{1}]", new Object[]{"/com/autumn/jsp/res.zip", ex}, SourceServlet.class);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 资源输出
     *
     * @param httpRequest
     * @param httpResponse
     * @param uriStr
     * @return boolean
     */
    public static boolean write(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String uriStr) {
        try {
            byte[] bs = map.get(uriStr);
            String type = typemap.get(uriStr);
            if (bs == null) {
                URL url = SourceServlet.class.getResource("/com/autumn/jsp/" + uriStr);
                if (url == null) {
                    bs = new byte[0];
                } else {
                    bs = IOUtils.read(url.openStream());
                }
                type = Function.getContentType(bs);
                if (type != null) {
                    typemap.put(uriStr, type);
                }
                map.put(uriStr, bs);
                LogFactory.info("读取资源：{0},Content-Type：{1}", new Object[]{uriStr, type}, SourceServlet.class);
            }
            if (bs.length == 0) {
                return false;
            } else {
                if (type != null) {
                    httpResponse.setHeader("Content-Type", type);
                }
                OutputStream os = httpResponse.getOutputStream();
                os.write(bs);
                os.flush();
                os.close();
                return true;
            }
        } catch (IOException e) {
            LogFactory.error("读取资源：" + uriStr + "错误：" + e.getMessage(), SourceServlet.class);
        }
        return false;
    }
}
