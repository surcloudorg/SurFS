/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.ConnectionParam;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SmartDataSource;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.WebFactory;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NameNotFoundException;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 数据库连接池编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DbSourceManager extends Action {

    private String driver; //数据库驱动程序
    private String dburl; //数据连接的URL
    private String user; //数据库用户名
    private String pwd; //数据库密码
    private int minconnection = 2; //保留最小连接数
    private int maxconnection = 50; //最大连接数
    private int timeoutvalue = 600000; //连接的最大空闲时间,  超过这个时间，释放
    private String jndiname = ""; //连接池名称
    private String testsql = "select 1"; //测试指令
    private int maxstatement = 50; //允许最多创建多少Statement,=0不托管Statement
    private SmartDataSource ds = null;
    //操作类型
    private String dotype = null; //操作类型
    private String doMsg = null; //执行结果
    private int rowCount = 0; //当前查询记录数
    private final List<DbSourceManager> rows = new ArrayList<DbSourceManager>();
    private Connection con = null;

    //执行查询操作
    public ActionForward executeQuery() {
        try {
            String sql = "select * from datasource where host='" + WebFactory.getInstanceName() + "'";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                DbSourceManager mysrv = (DbSourceManager) this.assemble(rs);
                mysrv.user = rs.getString("username");
                mysrv.setDs(ConnectionFactory.lookup(mysrv.getJndiname()));
                getRows().add(mysrv);
            }
        } catch (Exception e) {
            error("查询失败:" + e);
            setDoMsg("查询失败,可能输入的查询条件违法");
        }
        rowCount = rows.size();
        setAttribute("smlogs", this);
        return new ActionForward("dbpools.jsp");
    }

    /**
     * DbSourceManager--〉ConnectionParam
     *
     * @return
     */
    private ConnectionParam convert() throws Exception {
        ConnectionParam cp = new ConnectionParam(jndiname);
        cp.setDriver(driver);
        cp.setMaxConnection(maxconnection);
        cp.setMaxStatement(maxstatement);
        cp.setMinConnection(minconnection);
        cp.setPassword(pwd);
        cp.setTestsql(testsql);
        cp.setTimeoutValue(timeoutvalue);
        cp.setUrl(dburl);
        cp.setUser(user);
        return cp;
    }

    /**
     * 连接测试
     */
    private void check() {
        if (jndiname == null || jndiname.trim().equals("")) {
            doMsg = "必须指定连接池名称";
        }
        if (!TextUtils.isValidFileName(jndiname)) {
            doMsg = "连接池名只能是字母数字下划线组成的字符串";
            return;
        }
        Connection conn = null;
        try {
            conn = JdbcUtils.getConnect(driver, dburl, user, pwd);
            if (testsql != null && (!testsql.trim().equals(""))) {
                conn.createStatement().executeQuery(testsql);
            }
        } catch (Exception e) {
            doMsg = "连接失败：" + e.getMessage();
        }
        JdbcUtils.closeConnect(conn);
    }

    /**
     * 更新操作
     *
     * @return ActionForward
     */
    public ActionForward executeUpdate() {
        if (getDotype() == null) {
            return null;
        }
        if ((getDotype().equalsIgnoreCase("修改配置") || getDotype().equalsIgnoreCase("新建连接池")) && this.getAccessPermission() > 1) {
            check();
            if (getDotype().equalsIgnoreCase("新建连接池")) {
                try {
                    if (ConnectionFactory.lookup(jndiname) != null) {
                        setDoMsg("连接池（" + jndiname + "）已经存在不能创建");
                        setAttribute("smlog", this);
                        return new ActionForward("dbpooltail.jsp");
                    }
                } catch (NameNotFoundException ex) {
                }
            }
            if (jndiname.equalsIgnoreCase(ConnectionFactory.systemSourceName)) {
                setDoMsg("名称为（" + jndiname + "）的连接池不允许添加修改");
                setAttribute("smlog", this);
                return new ActionForward("dbpooltail.jsp");
            }
            if (getDoMsg() != null) {
                setAttribute("smlog", this);
                return new ActionForward("dbpooltail.jsp");
            }
        }
        if (getDotype().equalsIgnoreCase("修改配置") && this.getAccessPermission() > 1) { //更新
            try {
                jndiname = jndiname.replaceAll("\\.", "");
                String sql = "update datasource set driver=?,dburl=?,username=?,pwd=?,maxconnection=?,minconnection=?,"
                        + "timeoutvalue=?,testsql=?,maxstatement=? where jndiname=? and host=?";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, driver);
                prest.setString(2, dburl);
                prest.setString(3, user);
                prest.setString(4, pwd);
                prest.setInt(5, maxconnection);
                prest.setInt(6, minconnection);
                prest.setInt(7, timeoutvalue);
                prest.setString(8, testsql);
                prest.setInt(9, maxstatement);
                prest.setString(10, jndiname);
                prest.setString(11, WebFactory.getInstanceName());
                prest.executeUpdate();
                ConnectionParam cp = convert();
                ConnectionFactory.rebind(cp);
                warn(getDoMsg());
                setDoMsg("连接池（" + jndiname + "）设置更改成功");
                warn("修改连接池:" + this.toString());
            } catch (Exception e2) { //返回编辑页
                setDoMsg("连接池（" + jndiname + "）设置更改失败！");
                error(getDoMsg() + e2.getMessage());
                setAttribute("smlog", this);
                return new ActionForward("dbpooltail.jsp");
            }
        } else if (getDotype().equalsIgnoreCase("新建连接池") && this.getAccessPermission() > 1) {
            try {
                jndiname = jndiname.replaceAll("\\.", "");
                String sql = "insert into datasource(driver,dburl,username,pwd,maxconnection,minconnection,"
                        + "timeoutvalue,testsql,maxstatement,jndiname,host) values(?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, driver);
                prest.setString(2, dburl);
                prest.setString(3, user);
                prest.setString(4, pwd);
                prest.setInt(5, maxconnection);
                prest.setInt(6, minconnection);
                prest.setInt(7, timeoutvalue);
                prest.setString(8, testsql);
                prest.setInt(9, maxstatement);
                prest.setString(10, jndiname);
                prest.setString(11, WebFactory.getInstanceName());
                prest.executeUpdate();
                ConnectionParam cp = convert();
                ConnectionFactory.rebind(cp);
                warn(getDoMsg());
                setDoMsg("连接池（" + jndiname + "）添加成功");
                warn("创建连接池:" + this.toString());
            } catch (Exception e2) { //返回编辑页
                setDoMsg("连接池（" + jndiname + "）添加失败!");
                error(getDoMsg() + e2.getMessage());
                setAttribute("smlog", this);
                return new ActionForward("dbpooltail.jsp");
            }
        } else if (getDotype().equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //修改
            if (jndiname.equalsIgnoreCase(ConnectionFactory.systemSourceName)) {
                setDoMsg("删除连接池（" + jndiname + "）不允许删除！");
            } else {
                String sql = "delete from datasource where jndiname= '" + jndiname + "' and host='" + WebFactory.getInstanceName() + "'";
                try {
                    con.createStatement().executeUpdate(sql);
                    ConnectionFactory.unbind(jndiname);
                    setDoMsg("删除连接池（" + jndiname + "）成功！");
                    warn("删除连接池:jndiname=" + jndiname);
                } catch (Exception d) {
                    setDoMsg("删除连接池（" + jndiname + "）失败！");
                    error(getDoMsg() + d.getMessage());
                }
            }
        } else if (getDotype().equalsIgnoreCase("edit")) { //修改
            try {
                DbSourceManager smlog;
                String sql = "SELECT * from datasource where jndiname= '" + jndiname + "' and host='" + WebFactory.getInstanceName() + "'";
                ResultSet crs = con.createStatement().executeQuery(sql);
                if (crs.next()) {
                    smlog = (DbSourceManager) this.assemble(crs);
                    smlog.user = crs.getString("username");
                    smlog.setDotype("修改配置");
                    setAttribute("smlog", smlog);
                    return new ActionForward("dbpooltail.jsp");
                } else {
                    setDoMsg("连接池不存在或没有权限查看连接池配置: " + jndiname);
                    throw new Exception(getDoMsg());
                }
            } catch (Exception d) {
                setDoMsg("查看连接池（" + jndiname + "）配置失败！");
                error(getDoMsg() + d.getMessage());
            }
        } else if (getDotype().equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //修改
            DbSourceManager smlog = new DbSourceManager();
            smlog.setDotype("新建连接池");
            setAttribute("smlog", smlog);
            return new ActionForward("dbpooltail.jsp");
        }
        return null;
    }

    @Override
    public Forward execute() {
        if (dotype != null && (getDotype().equalsIgnoreCase("view") || getDotype().equalsIgnoreCase("刷新")))  { //修改
            SmartDataSource dsi = null;
            try {
                dsi = ConnectionFactory.lookup(jndiname);
            } catch (Exception e) {
            }
            if (dsi == null) {
                setDoMsg("连接池（" + jndiname + "）不存在！");
            } else {
                List cons = dsi.getConns();
                setAttribute("coninfo", "总连接数：" + dsi.getConnectionCount()
                        + ",占用连接:" + dsi.getUseConCount() + ",空闲连接：" + dsi.getFreeConCount());
                setAttribute("cons", cons);
                setAttribute("jndiname", jndiname);
                return new ActionForward("dbpoolinfo.jsp");
            }
        }
        con = this.getConnect(ConnectionFactory.systemSourceName);
        ActionForward af = executeUpdate();
        if (af != null) {
            return af;
        }
        return executeQuery();
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
     * @param doMsg the doMsg to set
     */
    public void setDoMsg(String doMsg) {
        this.doMsg = doMsg;
    }

    /**
     * @return the rows
     */
    public List<DbSourceManager> getRows() {
        return rows;
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the dburl
     */
    public String getDburl() {
        return dburl;
    }

    /**
     * @param dburl the dburl to set
     */
    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the minconnection
     */
    public int getMinconnection() {
        return minconnection;
    }

    /**
     * @param minconnection the minconnection to set
     */
    public void setMinconnection(int minconnection) {
        this.minconnection = minconnection;
    }

    /**
     * @return the maxconnection
     */
    public int getMaxconnection() {
        return maxconnection;
    }

    /**
     * @param maxconnection the maxconnection to set
     */
    public void setMaxconnection(int maxconnection) {
        this.maxconnection = maxconnection;
    }

    /**
     * @return the timeoutvalue
     */
    public int getTimeoutvalue() {
        return timeoutvalue;
    }

    /**
     * @param timeoutvalue the timeoutvalue to set
     */
    public void setTimeoutvalue(int timeoutvalue) {
        this.timeoutvalue = timeoutvalue;
    }

    /**
     * @return the jndiname
     */
    public String getJndiname() {
        return jndiname;
    }

    /**
     * @param jndiname the jndiname to set
     */
    public void setJndiname(String jndiname) {
        this.jndiname = jndiname;
    }

    /**
     * @return the testsql
     */
    public String getTestsql() {
        return testsql;
    }

    /**
     * @param testsql the testsql to set
     */
    public void setTestsql(String testsql) {
        this.testsql = testsql;
    }

    /**
     * @return the maxstatement
     */
    public int getMaxstatement() {
        return maxstatement;
    }

    /**
     * @param maxstatement the maxstatement to set
     */
    public void setMaxstatement(int maxstatement) {
        this.maxstatement = maxstatement;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the ds
     */
    public SmartDataSource getDs() {
        return ds;
    }

    /**
     * @param ds the ds to set
     */
    public void setDs(SmartDataSource ds) {
        this.ds = ds;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("driver=").append(driver).append("\r\n");
        sb.append("dburl=").append(dburl).append("\r\n");
        sb.append("user=").append(user).append("\r\n");
        sb.append("pwd=").append(pwd).append("\r\n");
        sb.append("maxconnection=").append(maxconnection).append("\r\n");
        sb.append("minconnection=").append(minconnection).append("\r\n");
        sb.append("timeoutvalue=").append(timeoutvalue).append("\r\n");
        sb.append("testsql=").append(testsql).append("\r\n");
        sb.append("maxstatement=").append(maxstatement).append("\r\n");
        sb.append("jndiname=").append(jndiname);
        return sb.toString();
    }
}
