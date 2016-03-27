/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import com.autumn.core.log.Level;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.LogProperties;

public class LogInitializer {

    public static void initLogger() {
        String path=System.getProperty("logger_path");      
        LogFactory.setLogPath(path);
        try {
            LogProperties lp = new LogProperties("system");
            lp.setLevel(Level.INFO.intValue());
            lp.setOutConsole(true);
            lp.setAddLevel(false);
            LogFactory.addLogger(lp);
            lp = new LogProperties("error");
            lp.setLevel(Level.INFO.intValue());
            lp.setOutConsole(true);
            lp.setAddLevel(false);
            LogFactory.addLogger(lp);
        } catch (Exception ex) {
        }
    }
}
