/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.log.WarnCommand;
import com.autumn.core.log.WarnImpl;
import com.autumn.util.Function;
import com.autumn.util.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

/**
 * <p>Title: 告警实现例子</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WarnTest implements WarnImpl {

    private Logger log = null;

    @Override
    public void execute(WarnCommand command) {
        log = command.getLog().getLogger(WarnTest.class);

        //获取私网IP
        String[] ips = Function.getLocalHostIP();
        String ip = "未知";
        if (!(ips == null || ips.length == 0)) {
            ip = ips[0];
        }

        Properties p = command.getLog().getProperties().getProperties();
        if (p == null) {
            p = LogFactory.getLogger().getProperties().getProperties();
            if (p == null) {
                log.error("没有配置发送短信参数，目录：" + command.getLog().getProperties().getLogName());
                return;
            }
        }


        //组装短信message
        String msg = p.getProperty("msgformat");
        try {
            msg = msg.replace("{ip}", ip);//设置ip
            msg = msg.replace("{sdate}", TextUtils.Date2String(new Date(), "MM-dd-HH:mm"));//设置发送时间
            msg = msg.replace("{logname}", command.getLog().getProperties().getLogName());//设置日志目录
            msg = msg.replace("{wdate}", TextUtils.Date2String(command.getWarnTime(), "HH:mm:ss"));//设置fatal日志实际输出时间
        } catch (Exception e) {
            log.error("msgformat字段配置错误：" + e.getMessage());
        }

        //发送报警
        String url = p.getProperty("url");
        url = url + msg;
        sendMessage(url);
    }

    /**
     * 发送短信
     *
     * @param url
     */
    private void sendMessage(String url) {
        try {
            URL Url = new URL(URLEncoder.encode(url, "UTF-8"));
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int rbyte = -1;
            while ((rbyte = is.read()) > 0) {
                os.write(rbyte);
            }
            String page = new String(os.toByteArray(), "UTF-8");
            connection.disconnect();
            if (page.contains("操作成功")) {
                log.info("发送短信成功：" + url);
            } else {
                log.error("发送短信失败：" + page);
            }
        } catch (IOException e) {
            e.getStackTrace();
            log.error("发送短信失败：" + url);
        }
    }
}
