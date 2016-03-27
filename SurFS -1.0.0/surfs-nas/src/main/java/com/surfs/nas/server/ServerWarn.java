/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.autumn.core.log.Logger;
import com.autumn.core.log.WarnCommand;
import com.autumn.core.log.WarnImpl;
import java.util.Properties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;


public class ServerWarn implements WarnImpl {

    @Override
    public void execute(WarnCommand command) {
        Properties p = command.getLog().getProperties().getProperties();
        if (p == null) {
            return;
        }
        String url = p.getProperty("url", "http://sms.surdoc.com:80/SMSSend/servlet/SendMessage.do");
        int sendMax = 10;
        try {
            sendMax = Integer.parseInt(p.getProperty("sendmax", "10"));
        } catch (Exception r) {
        }
        String phones = p.getProperty("phones", "");
        String head = p.getProperty("head", "US-PC7:");
        String msg = head + command.getMessage();
        if (msg.length() > 70) {
            msg = msg.substring(0, 70);
        }
        sendMessage(command.getLog(), url, phones, msg, sendMax);
    }

    /**
     *
     * @param log
     * @param url
     * @param phones
     * @param content
     * @param sendMax
     */
    public void sendMessage(Logger log, String url, String phones, String content, int sendMax) {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);
        post.getParams().setParameter("http.protocol.content-charset", "utf-8");
        post.addParameter("phones", phones);
        post.addParameter("content", content);
        post.addParameter("sendMax", String.valueOf(sendMax));
        post.addParameter("delayCount", String.valueOf(0));
        post.addParameter("remove", String.valueOf(false));
        try {
            int status = client.executeMethod(post);            
        } catch (Exception e) {
        }
    }
}
