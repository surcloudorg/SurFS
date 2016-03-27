/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.log;

/**
 * <p>Title: 执行报警</p>
 *
 * <p>Description: 报警执行线程，当需要报警时，从线程池中启动一个线程执行报警</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class WarnExecuter implements Runnable {

    private WarnCommand cmd = null;

    public WarnExecuter(WarnCommand cmd) {
        this.cmd = cmd;
    }

    @Override
    public void run() {
        WarnImpl executer = cmd.getLog().getProperties().getWarnObject();
        try {
            if (executer != null) {
                executer.execute(cmd);
            }
        } catch (Throwable e) {
            LogFactory.trace("执行[" + executer.getClass().getName() + "]告警发生未知错误!", e, WarnExecuter.class);
        }
    }
}
