package com.autumn.core.autopage;

import com.autumn.core.cookie.Client;
import com.autumn.core.cookie.CookieJar;
import com.autumn.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * <p>Title: 等待输入图形验证码</p>
 *
 * <p>Description: 维护一个队列（lists）依次等待人工输入</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class VerifyCodeEnter {

    private static final HashMap<Integer, VerifyCodeEnter> lists = new HashMap<Integer, VerifyCodeEnter>();

    /**
     * 添加一个需要输入验证的VerifyCodeEnter
     *
     * @param v
     */
    public static void addEnter(VerifyCodeEnter v) throws Exception {
        synchronized (lists) {
            if(lists.size()>5000){
                throw new Exception("队列长度超过5000");
            }
            lists.put(v.hashCode(), v);
        }
    }

    /**
     * 获取一个未输入验证的VerifyCodeEnter
     *
     * @return VerifyCodeEnter
     */
    public static VerifyCodeEnter getEnterUser() {
        synchronized (lists) {
            for (VerifyCodeEnter vce : lists.values()) {
                if (vce.getCode() == null) {
                    return vce;
                }
            }
        }
        return null;
    }

    public static VerifyCodeEnter getEnter(Integer hashcode) {
        synchronized (lists) {
            return lists.get(hashcode);
        }
    }

    public static void removeEnter(Integer hashcode) {
        synchronized (lists) {
            lists.remove(hashcode);
        }
    }

    public static void removeEnter(VerifyCodeEnter vce) {
        synchronized (lists) {
            lists.remove(vce.hashCode());
        }
    }
    private String comment = null;//注释
    private String imgUrl = null;//获取图片的地址
    private CookieJar cj = null;//cookie
    private byte[] imgContent = null;//获取到的图片内容
    private String code = null;//识别的认证码
    private final Object lockObject = new Object();//setCode,与getImage互斥

    public VerifyCodeEnter(String comment, String imgUrl, CookieJar cj) {
        this.comment = comment;
        this.imgUrl = imgUrl;
        this.cj = cj;
    }

    /**
     * 获取图片
     *
     * @throws IOException
     */
    public byte[] getImgContent() {
        synchronized (lockObject) {
            if (imgContent != null) {
                return imgContent;
            }
            Client client = new Client();
            HttpURLConnection connection = null;
            try {
                URL Url = new URL(imgUrl);
                connection = (HttpURLConnection) Url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0; SLCC1; .NET CLR 2.0.50727; InfoPath.2; MS-RTC LM 8)");
                if (cj != null) {
                    client.setCookies(connection, cj);//读出cookie，加入到http请求头的cookie字段里面
                }
                InputStream is = connection.getInputStream();
                imgContent = IOUtils.read(is);
                cj = client.getCookies(connection);//获取到cookie，保存以备下次提交
                return imgContent;
            } catch (IOException e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    /**
     * 等待输入验证码
     *
     * @throws InterruptedException
     */
    public void waitout() throws InterruptedException {
        synchronized (lockObject) {
            lockObject.wait(60 * 1000);
        }
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the imgUrl
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * @param imgUrl the imgUrl to set
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * @return the cj
     */
    public CookieJar getCj() {
        return cj;
    }

    /**
     * @param cj the cj to set
     */
    public void setCj(CookieJar cj) {
        this.cj = cj;
    }

    /**
     * 清除imgContent
     */
    public void clearImgContent() {
        synchronized (lockObject) {
            imgContent = null;
        }
    }

    /**
     * @return the code
     */
    public String getCode() {
        synchronized (lockObject) {
            return code;
        }
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        synchronized (lockObject) {
            this.code = code;
            lockObject.notify();
        }
        removeEnter(this.hashCode());
    }
}
