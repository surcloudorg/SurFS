/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.SystemAttributes;
import java.io.IOException;
import java.util.Date;

/**
 * <p>Title: 测试</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ThreadTest extends Thread {

    @Override
    public void run() {
        this.setName(this.getClass().getName());
        Object obj = null;
        try {
            obj = SystemAttributes.loadAttribute(SystemAttTest.class.getName());
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        if (obj == null) {
            Demo demo = new Demo("张三", 22, false,"",new Date(),"");
            SystemAttributes.setAttribute(SystemAttTest.class.getName(), demo, 1000 * 60 * 10);
        }
        while (!this.isInterrupted()) {
            try {
                java.util.logging.Logger.getLogger(ThreadTest.class.getName()).info("测试JDKLogger重定向");
                org.apache.log4j.Logger.getLogger(ThreadTest.class.getName()).info("测试Log4J重定向");
                sleep(300000);
            } catch (Exception e) {
                break;
            }
        }
    }
}
