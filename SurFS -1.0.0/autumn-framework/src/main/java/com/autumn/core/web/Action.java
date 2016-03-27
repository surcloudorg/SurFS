/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.SystemAttribute;
import com.autumn.core.SystemAttributes;
import com.autumn.core.autopage.ActionConfig;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.ResultSetFill;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>Title: WEB框架-控制器</p>
 *
 * <p>Description: 处理web请求输出回应</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public abstract class Action {

    /**
     * 获取actionid
     *
     * @return String
     */
    public final String getActionName() {
        return ActionContext.getActionContext().getActionMap().getActionid();
    }

    /**
     * 获取权限
     *
     * @return int
     */
    public final int getAccessPermission() {
        return ActionContext.getActionContext().getPermission();
    }

    /**
     * @return the autoActionConfig
     */
    public final ActionConfig getAutoActionConfig() throws Exception {
        return ActionContext.getActionContext().getActionMap().getAutoActionConfig();
    }

    /**
     * 将request组装为bean实体
     *
     * @param cl Class 要组装的类
     * @return Object 类实体
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public final Object getBean(Class cl) throws InstantiationException, IllegalAccessException {
        Object obj = cl.newInstance();
        putBean(obj);
        return obj;
    }

    /**
     * 将request更新到类实体
     *
     * @param object Object 要更新的类
     * @throws Exception
     */
    public final void putBean(Object object) {
        RequestFill.assemble(ActionContext.getActionContext().getRequest(), object);
    }

    /**
     * 将记录集组装为类实体
     *
     * @param rs ResultSet 记录集,从当前行组装
     * @return Object 实体类,返回当前action实体
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public final Object assemble(ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException {
        Class cls = ActionContext.getActionContext().getActionMap().getActionClass();
        Object object = cls.newInstance();
        ResultSetFill.assemble(rs, object);
        return object;
    }

    /**
     * 将记录集组装为类实体
     *
     * @param rs
     * @param object
     * @throws SQLException
     */
    public final void assemble(ResultSet rs, Object object) throws SQLException {
        ResultSetFill.assemble(rs, object);
    }

    /**
     * 将记录集组装为类实体
     *
     * @param rs
     * @param cls
     * @return Object
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public final Object assemble(ResultSet rs, Class cls) throws SQLException, InstantiationException, IllegalAccessException {
        Object object = cls.newInstance();
        ResultSetFill.assemble(rs, object);
        return object;
    }

    /**
     * @return the request
     */
    public final HttpServletRequest getRequest() {
        return ActionContext.getActionContext().getRequest();
    }

    /**
     * @return the response
     */
    public final HttpServletResponse getResponse() {
        return ActionContext.getActionContext().getResponse();
    }

    /**
     * @return the log
     */
    public final Logger getLog() {
        return ActionContext.getActionContext().getLogger();
    }

    /**
     * @return the loginUser
     */
    public final LoginUser getLoginUser() {
        return LoginUser.getLoginUser(this.getRequest());
    }

    /**
     * 设置页面级变量
     *
     * @param key String 主健
     * @param value Object 值
     */
    public final void setAttribute(String key, Object value) {
        getRequest().setAttribute(key, value);
    }

    /**
     * 获取变量值,范围:request
     *
     * @param key String 主健
     * @return Object 值
     */
    public final Object getAttribute(String key) {
        return getRequest().getAttribute(key);
    }

    /**
     * 移出变量
     *
     * @param key
     */
    public final void removeAttribute(String key) {
        getRequest().removeAttribute(key);
    }

    /**
     * 创建session
     *
     * @return HttpSession
     */
    public final HttpSession getSession() {
        LoginUser log = this.getLoginUser();
        HttpSession hs = getRequest().getSession(false);
        if (hs == null) {
            hs = getRequest().getSession(true);
            if (log != null) {
                if (log.getStimeOut().intValue() > 1000 * 30 && log.getStimeOut().intValue() < 1000 * 60 * 60 * 24 * 7) {
                    hs.setMaxInactiveInterval(log.getStimeOut().intValue());
                }
            }
        }
        return hs;
    }

    /**
     * 设置对话级变量
     *
     * @param key String 主健
     * @param value Object 值
     */
    public final void setSessionAttribute(String key, Object value) {
        HttpSession mySession = getSession();
        if (value != null) {
            mySession.setAttribute(key, value);
        } else {
            mySession.removeAttribute(key);
        }
    }

    /**
     * 获取对话级变量
     *
     * @param key
     * @return Object
     */
    public final Object getSessionAttribute(String key) {
        HttpSession mySession = getSession();
        Object obj = mySession.getAttribute(key);
        if (obj == null) {
            return null;
        }
        return SystemAttribute.checkClassLoader(obj);
    }

    /**
     * 移出变量
     *
     * @param key
     */
    public final void removeSessionAttribute(String key) {
        getSession().removeAttribute(key);
    }

    /**
     * 设置系统变量
     *
     * @param key
     * @param value
     */
    public final void setSystemAttribute(String key, Object value) {
        SystemAttributes.setAttribute(key, value);
    }

    /**
     * 设置系统变量
     *
     * @param key
     * @param value
     * @param timeout
     */
    public final void setSystemAttribute(String key, Object value, long timeout) {
        SystemAttributes.setAttribute(key, value, timeout);
    }

    /**
     * 移出变量
     *
     * @param key
     */
    public final void removeSystemAttribute(String key) {
        SystemAttributes.removeAttribute(key);
    }

    /**
     * 获取系统变量
     *
     * @param key
     * @return Object
     */
    public final Object getSystemAttribute(String key) {
        return SystemAttributes.getAttribute(key);
    }

    /**
     * 获取cookie中的值
     *
     * @param name
     * @return Cookie
     */
    public Cookie getCookie(String name) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 写日志
     *
     * @param s String
     */
    public final void warn(String s) {
        this.getLog().warn(s, this.getClass());
    }

    /**
     * 写日志
     *
     * @param pattern
     * @param args
     */
    public final void warn(String pattern, Object[] args) {
        this.getLog().warn(pattern, args, this.getClass());
    }

    /**
     * 写日志
     *
     * @param s String
     */
    public final void debug(String s) {
        this.getLog().debug(s, this.getClass());
    }

    /**
     * 写日志
     *
     * @param pattern
     * @param args
     */
    public final void debug(String pattern, Object[] args) {
        this.getLog().debug(pattern, args, this.getClass());
    }

    /**
     * 写日志
     *
     * @param s String
     */
    public final void info(String s) {
        this.getLog().info(s, this.getClass());
    }

    /**
     * 写日志
     *
     * @param pattern
     * @param args
     */
    public final void info(String pattern, Object[] args) {
        this.getLog().info(pattern, args, this.getClass());
    }

    /**
     * 写日志
     *
     * @param s String
     */
    public final void error(String s) {
        this.getLog().error(s, this.getClass());
    }

    /**
     * 写日志
     *
     * @param pattern
     * @param args
     */
    public final void error(String pattern, Object[] args) {
        this.getLog().error(pattern, args, this.getClass());
    }

    /**
     * 写日志
     *
     * @param s String
     */
    public final void fatal(String s) {
        this.getLog().fatal(s, this.getClass());
    }

    /**
     * 写日志
     *
     * @param pattern
     * @param args
     */
    public final void fatal(String pattern, Object[] args) {
        this.getLog().fatal(pattern, args, this.getClass());
    }

    /**
     * 写日志
     *
     * @param msg
     * @param e
     */
    public final void trace(String msg, Throwable e) {
        this.getLog().trace(msg, e);
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public final Connection getConnect() {
        Connection conn = getActionMap().getConnect();
        if (conn == null) {
            conn = getWebDirectory().getConnect();
            if (conn == null) {
                conn = getConnect(ConnectionFactory.systemSourceName);
            }
        }
        if (conn != null) {
            ActionContext.getActionContext().getConnList().add(conn);
        } else {
            error("获取数据库连接失败！");
        }
        return conn;
    }

    /**
     * 获取数据库连接
     *
     * @param jndi String 连接池名
     * @return Connection
     */
    public final Connection getConnect(String jndi) {
        Connection conn = ConnectionFactory.getConnect(jndi, this.getClass());
        if (conn != null) {
            ActionContext.getActionContext().getConnList().add(conn);
        } else {
            error("获取数据库连接失败！");
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn Connection
     */
    public final void closeConnect(Connection conn) {
        JdbcUtils.closeConnect(conn);
        ActionContext.getActionContext().getConnList().remove(conn);
    }

    /**
     * 关闭数据库连接
     */
    public final void closeConnect() {
        ActionContext.getActionContext().closeConnect();
    }

    /**
     * 执行action,处理请求包
     *
     * @return ActionForward
     */
    public abstract Forward execute();

    /**
     * @return the actionMap
     */
    public final ActionMap getActionMap() {
        return ActionContext.getActionContext().getActionMap();
    }

    /**
     * @return the webDirectory
     */
    public final WebDirectory getWebDirectory() {
        return ActionContext.getActionContext().getWebDirectory();
    }
}
