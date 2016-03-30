/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.log;

public class LogInitializer {

    public static void initLogger() {
        String path = System.getProperty("logger_path");
        LogFactory.setLogPath(path);
        LogFactory.setLog4jLogger();
        LogFactory.setSystemErr();
        LogFactory.configJDKLog();
        LogFactory.configLog4j();
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
