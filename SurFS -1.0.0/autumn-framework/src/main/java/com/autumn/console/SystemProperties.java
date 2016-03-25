package com.autumn.console;

import com.autumn.core.SystemAttribute;
import com.autumn.core.SystemAttributes;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import java.util.*;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 察看系统变量</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SystemProperties extends Action implements Comparable {

    private String path = "/";
    private String key = "";
    private String classname = null;
    private String size = "未知";
    private String info = "";
    private String stime = "";
    private String etime = "";
    private String timeout = "未到期";
    private boolean oldversion = false;
    //操作类型
    private String dotype = null; //操作类型
    private String doMsg = null; //执行结果
    private List<SystemProperties> rows = new ArrayList<SystemProperties>();

    /**
     * 系统变量--〉SystemProperties
     *
     * @param servicesattribute
     * @param pathstr
     */
    private void putrows(HashMap<Integer, HashMap<String, SystemAttribute>> servicesattribute, String pathstr) {
        for (Map.Entry<Integer, HashMap<String, SystemAttribute>> entry : servicesattribute.entrySet()) {
            HashMap<String, SystemAttribute> map = entry.getValue();
            String p = pathstr + entry.getKey().toString();
            for (Map.Entry<String, SystemAttribute> subentry : map.entrySet()) {
                SystemAttribute obj = subentry.getValue();
                SystemProperties sp = createSystemProperties(obj, subentry.getKey(), p);
                rows.add(sp);
            }
        }
    }

    /**
     *
     * @param obj
     * @param key
     * @param path
     * @return
     */
    private SystemProperties createSystemProperties(SystemAttribute obj, String key, String path) {
        SystemProperties sp = new SystemProperties();
        sp.setPath(path);
        sp.setKey(key);
        sp.setInfo(obj.toString());
        sp.setClassname(obj.getClassName());
        sp.setStime(obj.getStime());
        sp.setEtime(obj.getEtime());
        sp.setOldversion(obj.classNeedReload());
        sp.setTimeout(obj.isTimeOut() ? "已到期" : "未到期");
        long l = obj.getObjectSize();
        if (l > 0) {
            sp.setSize(Long.toString(l) + "字节");
        } else {
            sp.setSize("未知");
        }
        return sp;
    }

    /**
     * 搜索所有系统变量
     *
     * @return Forward
     */
    @SuppressWarnings("unchecked")
    public Forward executeQuery() {
        putrows(SystemAttributes.getServicesAttribute(), "/service/");
        putrows(SystemAttributes.getSoapAttribute(), "/soap/");
        putrows(SystemAttributes.getWebAttribute(), "/web/");
        HashMap<String, SystemAttribute> map = SystemAttributes.getSysAttribute();
        String pathstr = "/";
        Set<String> keys = map.keySet();
        for (String k : keys) {
            SystemAttribute obj = map.get(k);
            SystemProperties sp = createSystemProperties(obj, k, pathstr);
            rows.add(sp);
        }
        if (doMsg == null) {
            doMsg = "变量数目：" + rows.size();
        } else {
            doMsg = doMsg + ",变量数目：" + rows.size();
        }
        Collections.sort(rows);
        setAttribute("sysproperties", this);
        return new ActionForward("sysproperties.jsp");
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the dotype
     */
    public String getDotype() {
        return dotype;
    }

    /**
     * @param dotype the dotype to set
     */
    public void setDotype(String dotype) {
        this.dotype = dotype;
    }

    /**
     * @return the doMsg
     */
    public String getDoMsg() {
        return doMsg;
    }

    /**
     * @return the rows
     */
    public List<SystemProperties> getRows() {
        return rows;
    }

    @Override
    public Forward execute() {
        if (dotype != null && dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //添加操作
            SystemAttributes.removeAttribute(path, key);
            doMsg = "清空系统变量：" + path + "/" + key;
        }
        return executeQuery();
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the stime
     */
    public String getStime() {
        return stime;
    }

    /**
     * @param stime the stime to set
     */
    public void setStime(String stime) {
        this.stime = stime;
    }

    /**
     * @return the etime
     */
    public String getEtime() {
        return etime;
    }

    /**
     * @param etime the etime to set
     */
    public void setEtime(String etime) {
        this.etime = etime;
    }

    /**
     * @return the timeout
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the oldversion
     */
    public boolean isOldversion() {
        return oldversion;
    }

    /**
     * @param oldversion the oldversion to set
     */
    public void setOldversion(boolean oldversion) {
        this.oldversion = oldversion;
    }

    @Override
    public int compareTo(Object o) {
        String s1 = this.path + this.key;
        SystemProperties sp = (SystemProperties) o;
        String s2 = sp.path + sp.key;
        return s1.compareTo(s2);
    }
}
