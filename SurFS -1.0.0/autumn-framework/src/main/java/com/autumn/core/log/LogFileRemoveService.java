package com.autumn.core.log;

import com.autumn.core.cfg.Config;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.service.ServiceImpl;
import java.util.Properties;

/**
 * <p>Title: 定时清除日志文件</p>
 *
 * <p>Description: 定时清除日志文件-服务控制</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class LogFileRemoveService implements ServiceImpl {

    @Override
    public void start() throws Exception {
        String dir = null;
        int day = 7;
        Config cfg = ServiceFactory.getServiceConfig().getConfig();
        if (cfg == null) {
            Properties p = ServiceFactory.getServiceConfig().getProperties();
            if (p == null) {
                throw new Exception("配置错误");
            } else {
                dir = p.getProperty("logpath");
                day = Integer.parseInt(p.getProperty("day", "7"));
            }
        } else {
            dir = cfg.getAttributeValue("config.logpath");
            day = cfg.getAttributeIntValue("config.day", 7);
        }
        LogFileRemover.start(dir, day);
    }

    @Override
    public void stop() {
        LogFileRemover.terminate();
    }
}
