package com.autumn.core.web;

import com.autumn.core.ClassManager;
import com.autumn.core.autopage.ActionConfig;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigParser;
import com.autumn.core.sql.ConnectionFactory;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;
import org.apache.commons.fileupload.FileItem;

/**
 * <p>Title: WEB框架-控制器</p>
 *
 * <p>Description: 控制器配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public final class ActionMap {

    private String actionid = "";
    private int dirid = 0;//目录ID
    private String functionName = "";//下一级目录
    protected String classname = "";//类名
    private String menu = "";//菜单
    private int permissionorder = -1;//权限
    private ConfigParser params = new ConfigParser();//服务运行时配置（string,Config,properties格式）
    private ActionConfig autoActionConfig = null;
    private boolean regex = false;
    private Method method = null;
    private Class actionClass = null;
    private Boolean multipartRequest = null;

    /**
     * 复制
     *
     * @return ActionMap
     */
    @Override
    public ActionMap clone() {
        ActionMap am = new ActionMap(actionid);
        am.autoActionConfig = this.autoActionConfig;
        am.classname = this.classname;
        am.dirid = this.dirid;
        am.functionName = this.functionName;
        am.menu = this.menu;
        am.params = this.params;
        am.permissionorder = this.permissionorder;
        am.regex = this.regex;
        return am;
    }

    /**
     * @return the autoActionConfig
     */
    public synchronized ActionConfig getAutoActionConfig() throws Exception {
        if (autoActionConfig == null) {
            autoActionConfig = new ActionConfig(params.getString());
        }
        return autoActionConfig;
    }

    /**
     * 获取配置
     *
     * @return Properties
     */
    public Properties getProperties() {
        return params.getProperties();
    }

    /**
     * 获取配置
     *
     * @return Config
     */
    public Config getConfig() {
        return params.getConfig();
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params.getString();
    }

    /**
     * 创建
     *
     * @param actionid
     */
    public ActionMap(String actionid) {
        this.setActionid(actionid);
    }

    /**
     * @return the actionid
     */
    public String getActionid() {
        return actionid;
    }

    /**
     * @param actionid the actionid to set
     */
    protected void setActionid(String actionid) {
        this.actionid = actionid;
        regex = !actionid.matches(actionid);//自己不能匹配自己一般是正则
    }

    /**
     * @return the dirid
     */
    public int getDirid() {
        return dirid;
    }

    /**
     * @param dirid the dirid to set
     */
    protected void setDirid(int dirid) {
        this.dirid = dirid;
    }

    /**
     * @return the subdir
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * @param functionName the subdir to set
     */
    protected void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * @param classname the classname to set
     */
    protected void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    protected void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @return the permissionorder
     */
    public int getPermissionorder() {
        return permissionorder;
    }

    /**
     * @param permissionorder the permissionorder to set
     */
    protected void setPermissionorder(int permissionorder) {
        this.permissionorder = permissionorder;
    }

    /**
     * @param params the params to set
     */
    public synchronized void setParams(String params) {
        this.params.parse(params);
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public Connection getConnect() {
        if (getConfig() != null) {
            String jdbc = getConfig().getAttributeValue("config.datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        if (getProperties() != null) {
            String jdbc = getProperties().getProperty("datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        return null;
    }

    /**
     * @return the regex
     */
    public boolean isRegex() {
        return regex;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        try {
            Method m = getMethod();
            ActionMethod am = m.isAnnotationPresent(ActionMethod.class) ? m.getAnnotation(ActionMethod.class) : null;
            return am == null ? null : am.contentType();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @return the multipartRequest
     */
    public synchronized boolean isMultipartRequest() {
        if (multipartRequest == null) {
            try {
                multipartRequest = Boolean.FALSE;
                Class cls = getActionClass();
                if (cls != null) {
                    Method[] mym = cls.getDeclaredMethods();
                    for (Method m : mym) {
                        String namestr = m.getName().toLowerCase();
                        if (namestr.startsWith("set")) {
                            if (m.getParameterTypes().length == 1 && m.getReturnType().getName().equalsIgnoreCase("void")) {
                                if (m.getParameterTypes()[0].getCanonicalName().equals(FileItem.class.getName())) {
                                    multipartRequest = Boolean.TRUE;
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
        return multipartRequest.booleanValue();
    }
    static final String defauMethodName = "execute";

    /**
     * @return the method
     */
    public synchronized Method getMethod() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (method == null) {
            if (getActionClass() != null) {
                if (functionName == null || functionName.isEmpty()) {
                    functionName = defauMethodName;
                }
                Method[] mym = actionClass.getDeclaredMethods();
                for (Method m : mym) {
                    if (m.getName().equalsIgnoreCase(functionName)) {
                        if (m.getParameterTypes() == null || m.getParameterTypes().length == 0) {
                            method = m;
                            break;
                        }
                    }
                }
            }
        }
        if (method == null) {
            throw new IllegalAccessException("找不到函数:[" + functionName + "]");
        }
        return method;
    }

    /**
     * @return the actionClass
     */
    public synchronized Class getActionClass() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (actionClass == null) {
            Class cls = ClassManager.loadclass(classname);
            Object obj = cls.newInstance();
            if (obj instanceof Action) {
                actionClass = cls;
            } else {
                throw new InstantiationException("控制器必须继承" + Action.class.getName());
            }
        }
        return actionClass;
    }
}
