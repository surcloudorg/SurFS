package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.JdbcUtils;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-控制器</p>
 *
 * <p>Description: 控制器上下文</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ActionContext {

    private static ThreadLocal<ActionContext> threadService = new ThreadLocal<ActionContext>();//线程变量
    private ActionMap actionMap = null;//参数配置
    HttpServletRequest request; //请求
    HttpServletResponse response; //回应
    private WebDirectory webDirectory = null;//目录配置
    private int permission = 0;//权限
    private List<Connection> ConnList = new ArrayList<Connection>(); //数据库连接

    /**
     * 设置线程变量
     *
     * @param s
     */
    public static void setActionContext(ActionContext s) {
        threadService.set(s);
    }

    /**
     * 获取线程变量
     */
    public static ActionContext getActionContext() {
        return threadService.get();
    }

    /**
     * 移出线程变量
     */
    public static void removeActionContext() {
        threadService.remove();
    }

    public ActionContext(HttpServletRequest request, HttpServletResponse response, WebDirectory webDirectory, ActionMap actionMap) {
        this.request = request;
        this.response = response;
        this.webDirectory = webDirectory;
        this.actionMap = actionMap;
    }

    /**
     * @return the log
     */
    public Logger getLogger() {
        if (webDirectory == null) {
            return LogFactory.getLogger(LogFactory.SYSTEM_LOGNAME);
        } else {
            return webDirectory.getLogger();
        }
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnect() {
        for (Connection conn : ConnList) {
            JdbcUtils.closeConnect(conn);
        }
        ConnList.clear();
    }

    /**
     * @return the actionMap
     */
    public ActionMap getActionMap() {
        return actionMap;
    }

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return the webDirectory
     */
    public WebDirectory getWebDirectory() {
        return webDirectory;
    }

    /**
     * @return the Config_Permission
     */
    public int getPermission() {
        return permission;
    }

    /**
     * @param Config_Permission the Config_Permission to set
     */
    protected void setPermission(int Config_Permission) {
        this.permission = Config_Permission;
    }

    /**
     * @return the ConnList
     */
    protected List<Connection> getConnList() {
        return ConnList;
    }
}
