/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;


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
        }
    }
}
