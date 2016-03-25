package com.autumn.core;

import java.util.*;

/**
 * <p>Title: 系统变量清除</p>
 *
 * <p>Description: 清除过期系统变量</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SystemAttributeClear extends TimerTask {

    private static Timer clear = null;

    /**
     * 启动系统变量清除线程
     */
    public synchronized static void startClear() {
        if (clear != null) {
            clear = new Timer(true);
            clear.schedule(new SystemAttributeClear(), 0, 1000 * 60);
        }
    }

    /**
     * 关闭系统变量清除线程
     */
    public static void stopClear() {
        if (clear != null) {
            clear.cancel();
        }
    }

    @Override
    public void run() {
        HashMap<Integer, HashMap<String, SystemAttribute>> attribute = SystemAttributes.servicesattribute;
        Collection<HashMap<String, SystemAttribute>> col = attribute.values();
        for (HashMap<String, SystemAttribute> sas : col) {
            clear(sas);
        }
        attribute = SystemAttributes.soapattribute;
        col = attribute.values();
        for (HashMap<String, SystemAttribute> sas : col) {
            clear(sas);
        }
        attribute = SystemAttributes.webattribute;
        col = attribute.values();
        for (HashMap<String, SystemAttribute> sas : col) {
            clear(sas);
        }
        clear(SystemAttributes.sysattribute);
    }

    /**
     * 清除过期变量
     *
     * @param attributes
     */
    private void clear(HashMap<String, SystemAttribute> attributes) {
        for(Map.Entry<String, SystemAttribute> entry:attributes.entrySet()){
            SystemAttribute sa = entry.getValue();
            if (sa.isTimeOut()) {
                attributes.remove(entry.getKey());
            }
        }
    }
}
