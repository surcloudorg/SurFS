package com.autumn.core.sql;

import java.sql.*;
import java.text.MessageFormat;

/**
 * <p>Title:记录集分页显示</p>
 *
 * <p>Description: 不能跳页，可以跳到首页末页</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SmRowSetEx implements java.io.Serializable {

    private static final long serialVersionUID = 20120701000300L;
    private Connection conn = null;
    private String commandText = ""; //查询语句
    private int pagesize = 10; //每页显示10行
    private int timeout = 60; //查询超时
    private String keyfield = "id"; //按字段id分页，必须是int
    private boolean keyOrderAsc = false; //从大到小分页
    private String maxid = null;
    private String minid = null;
    private int rowCount = -1; //总记录数（需要计算时计算）
    private ResultSet rowset = null; //记录
    private String dbProductName = "Microsoft SQL Server";

    public SmRowSetEx(Connection conn, String CommandText, String Keyfield) throws SortedSQLException {
        try {
            this.conn = conn;
            this.commandText = CommandText;
            this.keyfield = Keyfield;
            DatabaseMetaData metaData = conn.getMetaData();
            dbProductName = metaData.getDatabaseProductName();
            if (!(dbProductName.equalsIgnoreCase("oracle")
                    || dbProductName.equalsIgnoreCase("Microsoft SQL Server")
                    || dbProductName.equalsIgnoreCase("mysql"))) {
                throw new SQLException("不支持" + dbProductName + "数据库");
            }
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
        String sql;
        PreparedStatement stat = null;
        ResultSet rs;
        if (rowCount == -1) {
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
            } catch (SQLException r) {
                throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
            } finally {
                JdbcUtils.closeStatement(stat);
            }
        }
        return rowCount;
    }

    /**
     * 设置最大最小ID
     *
     * @throws Exception
     */
    private void setMax_Min_id() throws SortedSQLException {
        try {
            if (keyOrderAsc) {
                rowset.first();
                minid = rowset.getString(keyfield);
                rowset.last();
                maxid = rowset.getString(keyfield);
            } else {
                rowset.first();
                maxid = rowset.getString(keyfield);
                rowset.last();
                minid = rowset.getString(keyfield);
            }
            rowset.beforeFirst();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        }
    }

    /**
     * 查询首页
     *
     * @return boolean
     * @throws Exception
     */
    public boolean firstPage() throws SortedSQLException {
        String sql;
        PreparedStatement stat;
        try {
            if (dbProductName.equalsIgnoreCase("oracle")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from ({0}) order by {1}) where rownum<={2}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                } else {
                    sql = "select * from (select * from ({0}) order by {1} desc) where rownum<={2}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                }
            } else if (dbProductName.equalsIgnoreCase("mysql")) {
                if (keyOrderAsc) {
                    sql = "select * from ({0}) as mytab order by {1} limit 0,{2}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                } else {
                    sql = "select  * from ({0}) as mytab order by {1} desc limit 0,{2}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                }
            } else {
                if (keyOrderAsc) {
                    sql = "select Top {0} * from ({1}) as mytab order by {2}";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield});
                } else {
                    sql = "select Top {0} * from ({1}) as mytab order by {2} desc";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield});
                }
            }
            stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(timeout);
            rowset=stat.executeQuery();
            if (!rowset.next()) {
                return false;
            } else {
                rowset.beforeFirst();
            }
            setMax_Min_id();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        }
        return true;
    }

    /**
     * 跳至最后一页
     *
     * @return boolean
     * @throws Exception
     */
    public boolean lastPage() throws SortedSQLException {
        String sql;
        PreparedStatement stat;
        try {
            if (dbProductName.equalsIgnoreCase("oracle")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from (select * from ({0}) order by {1} desc) where rownum<={2}) order by {1}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                } else {
                    sql = "select * from (select * from (select * from ({0}) order by {1}) where rownum<={2}) order by {1} desc";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                }
            } else if (dbProductName.equalsIgnoreCase("mysql")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from ({0}) as mytab order by {1} desc limit 0,{2}) as mytab1 order by {1}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                } else {
                    sql = "select * from (select * from ({0}) as mytab order by {1} limit 0,{2}) as mytab1 order by {1} desc";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, pagesize});
                }
            } else {
                if (keyOrderAsc) {
                    sql = "select * from (select Top {0} * from ({1}) as mytab order by {2} desc) as mytab1 order by {2}";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield});
                } else {
                    sql = "select * from (select Top {0} * from ({1}) as mytab order by {2}) as mytab1 order by {2} desc";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield});
                }
            }
            stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(timeout);
            rowset=stat.executeQuery();
            if (!rowset.next()) {
                return false;
            } else {
                rowset.beforeFirst();
            }
            setMax_Min_id();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        } 
        return true;
    }

    /**
     * 刷新
     *
     * @return boolean
     * @throws Exception
     */
    public boolean refresh() throws SortedSQLException {
        if (minid == null && maxid == null) {
            return firstPage();
        }
        String sql;
        PreparedStatement stat;
        try {
            if (dbProductName.equalsIgnoreCase("oracle")) {
                sql = "select * from ({0}) where {1}>='{2}' and {1}<='{3}' order by {1}";
                sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, maxid});
                if (!keyOrderAsc) {
                    sql = sql + " desc";
                }
            } else {
                sql = "select * from ({0}) as mytab where {1}>='{2}' and {1}<='{3}' order by {1}";
                sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, maxid});
                if (!keyOrderAsc) {
                    sql = sql + " desc";
                }
            }
            stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(timeout);
            rowset=stat.executeQuery();
            if (!rowset.next()) {
                return firstPage();
            } else {
                rowset.beforeFirst();
            }
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        }
        return true;
    }

    /**
     * 下一页
     *
     * @return boolean
     * @throws Exception
     */
    public boolean nextPage() throws SortedSQLException {
        if (minid == null && maxid == null) {
            return firstPage();
        }
        String sql;
        PreparedStatement stat;
        try {
            if (dbProductName.equalsIgnoreCase("oracle")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from ({0}) where {1}>'{2}' order by {1}) where rownum<={3}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, maxid, pagesize});
                } else {
                    sql = "select * from (select * from ({0}) where {1}<'{2}' order by {1} desc) where rownum<={3}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, pagesize});
                }
            } else if (dbProductName.equalsIgnoreCase("mysql")) {
                if (keyOrderAsc) {
                    sql = "select * from ({0}) as mytab where {1}>'{2}' order by {1} limit 0,{3}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, maxid, pagesize});
                } else {
                    sql = "select * from ({0}) as mytab where {1}<'{2}' order by {1} desc limit 0,{3}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, pagesize});
                }
            } else {
                if (keyOrderAsc) {
                    sql = "select Top {0} * from ({1}) as mytab where {2}>'{3}' order by {2}";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield, maxid});
                } else {
                    sql = "select Top {0} * from ({1}) as mytab where {2}<'{3}' order by {2} desc";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield, minid});
                }
            }
            stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(timeout);
            rowset=stat.executeQuery();
            if (!rowset.next()) {
                return refresh();
            } else {
                rowset.beforeFirst();
            }
            setMax_Min_id();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        } 
        return true;
    }

    /**
     * 上一页
     *
     * @return boolean
     * @throws Exception
     */
    public boolean previousPage() throws SortedSQLException {
        if (minid == null && maxid == null) {
            return firstPage();
        }
        String sql;
        PreparedStatement stat;
        try {
            if (dbProductName.equalsIgnoreCase("oracle")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from (select * from ({0}) where {1}<'{2}' order by {1} desc) where rownum<={3}) order by {1}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, pagesize});
                } else {
                    sql = "select * from (select * from (select * from ({0}) where {1}>'{2}' order by {1}) where rownum<={3}) order by {1} desc";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, maxid, pagesize});
                }
            } else if (dbProductName.equalsIgnoreCase("mysql")) {
                if (keyOrderAsc) {
                    sql = "select * from (select * from ({0}) as mytab where {1}<'{2}' order by {1} desc limit 0,{3}) as tab order by {1}";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, minid, pagesize});
                } else {
                    sql = "select * from (select * from ({0}) as mytab where {1}>'{2}' order by {1} limit 0,{3}) as tab order by {1} desc";
                    sql = MessageFormat.format(sql, new Object[]{commandText, keyfield, maxid, pagesize});
                }
            } else {
                if (keyOrderAsc) {
                    sql = "select * from (select Top {0} * from ({1}) as mytab where {2}<'{3}' order by {2} desc) as tab order by {2}";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield, minid});
                } else {
                    sql = "select * from (select Top {0} * from ({1}) as mytab where {2}>'{3}' order by {2}) as tab order by {2} desc";
                    sql = MessageFormat.format(sql, new Object[]{pagesize, commandText, keyfield, maxid});
                }
            }
            stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(timeout);
            rowset=stat.executeQuery();
            if (!rowset.next()) {
                return refresh();
            } else {
                rowset.beforeFirst();
            }
            setMax_Min_id();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(conn, r);
        } 
        return true;
    }

    /**
     * 清除查询结果
     */
    public void clear() {
        rowCount = -1;
        minid = null;
        maxid = null;
        try {
            this.rowset.close();
        } catch (Exception e) {
        }
        this.rowset = null;
    }

    /**
     * 查询语句
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
     * 分页排序方向，true升序/false将序
     *
     * @return boolean
     */
    public boolean isKeyOrderAsc() {
        return keyOrderAsc;
    }

    /**
     * 当前记录集最大id
     *
     * @return int
     */
    public String getMaxid() {
        return maxid;
    }

    /**
     * 当前记录集最小id
     *
     * @return int
     */
    public String getMinid() {
        return minid;
    }

    /**
     * 返回当前查询结果集
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
     * 查询超时时长
     *
     * @return int
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 每页显示的行数
     *
     * @return int
     */
    public int getPagesize() {
        return pagesize;
    }

    /**
     * 设置查询语句
     *
     * @param commandText String
     */
    public void setCommandText(String commandText) {
        clear();
        this.commandText = commandText;
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
     * 设置分页字段
     *
     * @param keyfield String
     */
    public void setKeyfield(String keyfield) {
        if (keyfield.equalsIgnoreCase(this.keyfield)) {
            return;
        }
        clear();
        this.keyfield = keyfield;
    }

    /**
     * 设置分页排序方向
     *
     * @param keyOrderAsc boolean
     */
    public void setKeyOrderAsc(boolean keyOrderAsc) {
        if (keyOrderAsc == this.keyOrderAsc) {
            return;
        }
        clear();
        this.keyOrderAsc = keyOrderAsc;
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
     * 设置每页显示的行数
     *
     * @param pagesize int
     */
    public void setPagesize(int pagesize) {
        if (pagesize < 5) {
            pagesize = 5;
        }
        if (pagesize == this.pagesize) {
            return;
        }
        clear();
        this.pagesize = pagesize;
    }

    /**
     * 设置最大id,下一次翻页查询将根据该字段
     *
     * @param maxid int
     */
    public void setMaxid(String maxid) {
        this.maxid = maxid;
    }

    /**
     * 设置最小id,下一次翻页查询将根据该字段
     *
     * @param minid int
     */
    public void setMinid(String minid) {
        this.minid = minid;
    }
}
