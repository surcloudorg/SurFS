/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;


public class ConfigParser {

    private Object config = null;
    private String stringConfig = null;

    /**
     *
     * @return String
     */
    public String getString() {
        return stringConfig;
    }


    public void update() throws IOException {
        if (config != null) {
            if (config instanceof Config) {
                Config cfg = (Config) config;
                cfg.update();
                stringConfig = cfg.getDoc();
            } else if (config instanceof Properties) {
                Properties properties = (Properties) config;
                StringWriter sw = new StringWriter();
                properties.store(sw, "");
                stringConfig = sw.toString();
            }
        }
    }

    /**
     *
     * @param params
     */
    public final void parse(String params) {
        if (params == null) {
            config = null;
            stringConfig = null;
            return;
        }
        stringConfig = params.trim();
        try {
            config = new Config(params);
        } catch (Exception e) {
            try {
                Properties p = new Properties();
                p.load(new StringReader(params));
                config = p;
            } catch (Exception e2) {
                config = stringConfig;
            }
        }
    }

    /**
     *
     * @return Config
     */
    public Config getConfig() {
        if (config == null) {
            return null;
        }
        if (config instanceof Config) {
            return (Config) config;
        } else {
            return null;
        }
    }

    /**
     *
     * @return Properties
     */
    public Properties getProperties() {
        if (config == null) {
            return null;
        }
        if (config instanceof Properties) {
            return (Properties) config;
        } else {
            return null;
        }
    }
}
