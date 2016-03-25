package com.autumn.core.cfg;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

/**
 * <p>Title: 配置解析工具</p>
 *
 * <p>Description: 判断配置是否是xml,Properties,String</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ConfigParser {

    private Object config = null;//从params解析的配置对象
    private String stringConfig = null;//字符串配置

    /**
     * 配置字符串
     *
     * @return String
     */
    public String getString() {
        return stringConfig;
    }

    /**
     * 更新
     */
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
     * 解析params
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
     * 获取配置
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
     * 获取配置
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
