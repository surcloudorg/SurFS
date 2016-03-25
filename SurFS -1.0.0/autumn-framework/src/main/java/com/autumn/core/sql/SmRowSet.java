package com.autumn.core.sql;

import java.sql.*;
import java.text.MessageFormat;

/**
 * <p>Title:记录集分页显示</p>
 *
 * <p>Description: 总记录数，总页数，排序</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SmRowSet implements java.io.Serializable {

    private static final long serialVersionUID = 20120701000200L;
    private Connection conn = null;
    private String commandText = ""; //查询语句
    private int timeout = 60; //查询超时
    private int pagesize = 10; //每页显示10行
    private String keyfield = "id"; //按字段id分页
    private boolean keyOrderAsc = false; //从大到小分页
    private int pageCount = -1; //总页数
    private int rowCount = 0; //总记录数
    private int CurrentPage = 0; //当前页
    private ResultSet rowset = null; //记录
    private String dbProductName = "Microsoft SQL Server";
    private int dbVersion = 8;
    private String primarykey = null;

    /**
     * 初始化
     *
     * @param conn
     * @param CommandText
     * @param Keyfield
     * @param primarykey
     * @throws SQLException
     */
    private void init(Connection conn, String CommandText, String Keyfield, String primarykey) throws SQLException {
        this.conn = conn;
        this.commandText = CommandText;
        this.keyfield = Keyfield;
        DatabaseMetaData metaData = conn.getMetaData();
        dbProductName = metaData.getDatabaseProductName();
        this.primarykey = primarykey;
        String ver = metaData.getDatabaseProductVersion();//9.00
        int index = ver.indexOf(".");
        if (index > 0) {
            ver = ver.substring(0, index);
        }
        try {
            dbVersion = Integer.parseInt(ver);
        } catch (Exception e) {
        }
        if (!(dbProductName.equalsIgnoreCase("oracle")
                || dbProductName.equalsIgnoreCase("mysql")
                || dbProductName.equalsIgnoreCase("apache derby")
                || dbProductName.equalsIgnoreCase("Microsoft SQL Server"))) {
            throw new SQLException("不支持" + dbProductName + "数据库");
        }

    }

    public SmRowSet(Connection conn, String CommandText, String Keyfield) throws SortedSQLException {
        this(conn, CommandText, Keyfield, null);
    }

    public SmRowSet(Connection conn, String CommandText, String Keyfield, String primarykey) throws SortedSQLException {
        try {
            init(conn, CommandText, Keyfield, primarykey);
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        }
    }

    /**
     * 获取记录数，如果为-1，执行查询
     *
     * @return int
     * @throws Exception
     */
    public int getRowCount() throws SortedSQLException {
        if (pageCount == -1) {
            getPageCount();
        }
        return rowCount;
    }

    /**
     * 计算页数，如果PageCount == -1，执行查询，否则返回PageCount
     *
     * @return int
     * @throws Exception
     */
    public int getPageCount() throws SortedSQLException {
        String sql;
        PreparedStatement stat = null;
        ResultSet rs;
        if (pageCount == -1) {
            try {
                if (dbProductName.equalsIgnoreCase("oracle")) {
                    sql = MessageFormat.format("select count(*) as num from ({0})", new Object[]{commandText});
                } else {
                    sql = MessageFormat.format("select count(*) as num from ({0}) as mytab", new Object[]{commandText});
                }
                stat = conn.prepareStatement(sql);
                stat.setQueryTimeout(timeout);
                rs = stat.executeQuery();
                rs.next();
                rowCount = rs.getInt("num");
                if (rowCount % pagesize == 0) {
                    pageCount = rowCount / pagesize;
                } else {
                    pageCount = rowCount / pagesize + 1;
                }
            } catch (SQLException r) {
                throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
            } finally {
                JdbcUtils.closeStatement(stat);
            }
        }
        return pageCount;
    }

    /**
     * 前翻
     *
     * @return boolean
     * @throws Exception
     */
    public boolean previousPage() throws SortedSQLException {
        return movePage(CurrentPage - 1);
    }

    /**
     * 后翻
     *
     * @return boolean
     * @throws Exception
     */
    public boolean nextPage() throws SortedSQLException {
        return movePage(CurrentPage + 1);
    }

    /**
     * 移动到指定页
     *
     * @param page int
     * @return boolean
     * @throws Exception
     */
    public boolean movePage(int page) throws SortedSQLException {
        String sql = null;
        PreparedStatement stat;
        boolean result = true;
        try {
            if (pageCount == -1) {
                getPageCount();
            }
            if (keyOrderAsc) {
                if (page > pageCount || page < 1) {
                    result = false;
                } else {
                    CurrentPage = page;
                    int count = pagesize * CurrentPage;
                    int topnum = count - pagesize;
                    if (dbProductName.equalsIgnoreCase("oracle")) {
                        sql = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (SELECT * FROM ({0})  order by {1} asc) A WHERE ROWNUM <= {2})WHERE RN > {3}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, count, topnum});
                    } else if (dbProductName.equalsIgnoreCase("mysql")) {
                        sql = "SELECT * FROM ({0}) as tab order by {1} asc limit {2},{3}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, topnum, pagesize});
                    } else if (dbProductName.equalsIgnoreCase("apache derby")) {
                        sql = "SELECT * FROM (SELECT tab.*,ROW_NUMBER() OVER () as RowNumber  FROM ({0}) as tab) as tab1 "
                                + "WHERE RowNumber between {1} and {2}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, topnum + 1, count});
                    } else {
                        if (dbVersion > 8) {
                            sql = "SELECT * FROM (SELECT *,ROW_NUMBER() OVER (order by {0} asc) as RowNumber  FROM"
                                    + " ({1}) as tab) as tab1 WHERE RowNumber between {2} and {3}";
                            sql = MessageFormat.format(sql, new Object[]{keyfield, commandText, topnum + 1, count});
                        } else {
                            if (primarykey != null && (!keyfield.equalsIgnoreCase(primarykey))) {
                                sql = "select * from ({0}) as tab where {1} in(SELECT top {2} {1} FROM (select top {3} * FROM"
                                        + " ({0}) as tab1 order by {4},{1} asc)as tab2 order by {4},{1} desc) order by {4}";
                                sql = MessageFormat.format(sql, new Object[]{commandText, primarykey, pagesize, count, keyfield});
                            } else {
                                sql = "select * from ({0}) as tab where {1} in(SELECT top {2} {1} FROM (select top {3} * FROM"
                                        + " ({0}) as tab1 order by {1} asc)as tab2 order by {1} desc) order by {1}";
                                sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize, count});
                            }
                        }
                    }
                }
            } else {
                if (page > pageCount || page < 1) {
                    result = false;
                } else {
                    CurrentPage = page;
                    int count = pagesize * CurrentPage;
                    int topnum = count - pagesize;
                    if (dbProductName.equalsIgnoreCase("oracle")) {
                        sql = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (SELECT * FROM ({0})  order by {1} desc) A WHERE"
                                + " ROWNUM <= {2})WHERE RN > {3}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, count, topnum});
                    } else if (dbProductName.equalsIgnoreCase("mysql")) {
                        sql = "SELECT * FROM ({0}) as tab order by {1} desc limit {2},{3}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, topnum, pagesize});
                    } else if (dbProductName.equalsIgnoreCase("apache derby")) {
                        sql = "SELECT * FROM (SELECT tab.*,ROW_NUMBER() OVER () as RowNumber  FROM ({0}) as tab) as tab1 "
                                + "WHERE RowNumber between {1} and {2}";
                        sql = MessageFormat.format(sql, new Object[]{commandText, topnum + 1, count});
                    } else {
                        if (dbVersion > 8) {
                            sql = "SELECT * FROM (SELECT *,ROW_NUMBER() OVER (order by {0} desc) as RowNumber  FROM "
                                    + "({1}) as tab) as tab1 WHERE RowNumber between {2} and {3}";
                            sql = MessageFormat.format(sql, new Object[]{keyfield, commandText, topnum + 1, count});
                        } else {
                            if (primarykey != null && (!keyfield.equalsIgnoreCase(primarykey))) {
                                sql = "select * from ({0}) as tab where {1} in(SELECT top {2} {1} FROM (select top {3} * FROM"
                                        + " ({0}) as tab1 order by {4},{1} desc)as tab2 order by {4},{1} asc) order by {4} desc";
                                sql = MessageFormat.format(sql, new Object[]{commandText, primarykey, pagesize, count, keyfield});
                            } else {
                                sql = "select * from ({0}) as tab where {1} in(SELECT top {2} {1} FROM (select top {3} * FROM"
                                        + " ({0}) as tab1 order by {1} desc)as tab2 order by {1} asc) order by {1} desc";
                                sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize, count});
                            }
                        }
                    }
                }
            }
            if (sql != null) {
                stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stat.setQueryTimeout(timeout);
                rowset=stat.executeQuery();     
            }
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        } 
        return result;
    }

    /**
     * 清除查询结果
     */
    public void clear() {
        this.CurrentPage = 0;
        this.pageCount = -1;
        this.rowCount = 0;
        try {
            this.rowset.close();
        } catch (Exception e) {
        }
        this.rowset = null;
    }

    /**
     * 设置查询语句
     *
     * @param CommandText String
     */
    public void setCommandText(String CommandText) {
        clear();
        this.commandText = CommandText;
    }

    /**
     * 获取查询语句
     *
     * @return String
     */
    public String getCommandText() {
        return commandText;
    }

    /**
     * 获取分页字段，一般为表的id(自增量),
     *
     * @return String
     */
    public String getKeyfield() {
        return keyfield;
    }

    /**
     * 设置分页字段
     *
     * @param Keyfield String
     */
    public void setKeyfield(String Keyfield) {
        if (Keyfield.equalsIgnoreCase(this.keyfield)) {
            return;
        }
        clear();
        this.keyfield = Keyfield;
    }

    /**
     * 获取当前页
     *
     * @return int
     */
    public int getCurrentPage() {
        return CurrentPage;
    }

    /**
     * 分页排序方向，true升序/false将序
     *
     * @return boolean
     */
    public boolean isKeyOrderAsc() {
        return keyOrderAsc;
    }

    /**
     * 设置分页排序方向
     *
     * @param OrderbyAsc boolean
     */
    public void setKeyOrder(boolean OrderbyAsc) {
        if (OrderbyAsc == this.keyOrderAsc) {
            return;
        }
        clear();
        this.keyOrderAsc = OrderbyAsc;
    }

    /**
     * 查询超时时长
     *
     * @return int
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 查询后的纪录集
     *
     * @return ResultSet
     */
    public ResultSet getRowset() {
        if (rowset != null) {
            try {
                if (!rowset.isBeforeFirst()) {
                    rowset.beforeFirst();
                }
            } catch (Exception e) {
            }
        }
        return rowset;
    }

    /**
     * 每页显示Pagesize行
     *
     * @return int
     */
    public int getPagesize() {
        return pagesize;
    }

    /**
     * 设置查询超时时长
     *
     * @param timeout int
     */
    public void setTimeout(int timeout) {
        if (timeout < 10) {
            timeout = 10;
        }
        this.timeout = timeout;
    }

    /**
     * 设置数据库连接
     *
     * @param conn Connection
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * 设置每页显示的行数
     *
     * @param Pagesize int
     */
    public void setPagesize(int Pagesize) {
        if (Pagesize < 5) {
            Pagesize = 5;
        }
        if (Pagesize == this.pagesize) {
            return;
        }
        clear();
        this.pagesize = Pagesize;
    }
}
