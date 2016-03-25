package com.autumn.console;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.ScriptExecuter;
import com.autumn.core.sql.SmartDataSource;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.SessionMethod;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 数据库连接测试</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SqlTest extends Action {

    private String jndiname = null;
    private String sql = null;
    private List<String> findResult = new ArrayList<String>();
    private String timeconsum = null;
    private String submit = null; //是否提交

    @Override
    public Forward execute() {
        if (submit == null || sql == null) { //初始化
            this.setAttribute("sqltest", this);
            return new ActionForward("sqltest.jsp");
        }

        String[] sqls = sql.split("\r\n");
        String newsql = "";
        for (String s : sqls) {
            s = s.trim();
            if ((!s.startsWith("--")) && (!s.isEmpty())) {
                newsql = newsql + s + "\r\n";
            }
        }
        if (newsql.isEmpty()) {
            this.setAttribute("sqltest", this);
            return new ActionForward("sqltest.jsp");
        }
        if (this.getAccessPermission() > 1) {
            try {
                executeSQL(newsql);
            } catch (Exception ex) {
                findResult.add("执行失败:" + ex.getMessage());
            }
        } else {
            findResult.add("没有操作权限！");
        }
        this.setAttribute("sqltest", this);
        return new ActionForward("sqltest.jsp");
    }

    private void executeSQL(String newsql) throws Exception {
        findResult.clear();
        Connection con = null;
        try {
            long stime = System.currentTimeMillis();
            SmartDataSource ds = ConnectionFactory.lookup(jndiname);
            con = ds.getConnParam().getConnect();
            String sumtime = " (" + String.valueOf(System.currentTimeMillis() - stime) + " ms)";
            findResult.add("创建连接成功！" + sumtime);
            findResult.add("------------------------------------------------------");
            findResult.add("");
        } catch (Exception ex) {
            throw ex;
        }
        try {
            long consum = ScriptExecuter.doScript(con, findResult, newsql);
            timeconsum = "耗时：" + String.valueOf(consum) + "毫秒";
        } catch (Exception ex) {
            throw ex;
        } finally {
            JdbcUtils.closeConnect(con);
        }
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
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql the sql to set
     */
    @SessionMethod
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the findResult
     */
    public List<String> getFindResult() {
        return findResult;
    }

    /**
     * @param findResult the findResult to set
     */
    public void setFindResult(List<String> findResult) {
        this.findResult = findResult;
    }

    /**
     * @return the timeconsum
     */
    public String getTimeconsum() {
        return timeconsum;
    }

    /**
     * @param timeconsum the timeconsum to set
     */
    public void setTimeconsum(String timeconsum) {
        this.timeconsum = timeconsum;
    }

    /**
     * @param submit the submit to set
     */
    public void setSubmit(String submit) {
        this.submit = submit;
    }
}
