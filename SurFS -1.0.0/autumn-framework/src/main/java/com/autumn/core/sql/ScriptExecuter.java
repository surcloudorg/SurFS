/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: sql脚本执行器</p>
 *
 * <p>
 * Description: 执行sql脚本,返回文本信息</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ScriptExecuter {

    /**
     * 查询表名
     *
     * @param conn
     * @return List<String>
     * @throws SQLException
     */
    public static List<String> showtables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();
        ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
        while (rs.next()) {
            tables.add(rs.getString(3).toLowerCase());
        }
        return tables;
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param text 脚本字符串
     * @return long
     * @throws SortedSQLException
     * @throws IOException
     */
    public static long doScript(Connection conn, String text) throws SortedSQLException, IOException {
        return doScript(conn, null, text);
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param findResult
     * @param text
     * @return long
     * @throws SortedSQLException
     * @throws IOException
     */
    public static long doScript(Connection conn, List<String> findResult, String text) throws SortedSQLException, IOException {
        ByteArrayInputStream bo = new ByteArrayInputStream(text.getBytes("utf-8"));
        return doScript(conn, findResult, bo);
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param file 脚本文件
     * @return long
     * @throws IOException
     */
    public static long doScript(Connection conn, File file) throws SortedSQLException, IOException {
        return doScript(conn, null, file);
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param findResult
     * @param file
     * @return long
     * @throws SortedSQLException
     * @throws IOException
     */
    public static long doScript(Connection conn, List<String> findResult, File file) throws SortedSQLException, IOException {
        FileInputStream is = new FileInputStream(file);
        return doScript(conn, findResult, is);
    }

    /**
     * 执行结果
     *
     * @param findResult
     * @param st
     * @param sql
     * @throws SQLException
     */
    private static void executeSQL(List<String> findResult, Statement st, String sql) throws SQLException, IOException {
        long stime = System.currentTimeMillis();
        sql = sql.trim();
        if(sql.isEmpty()){
            return;
        }
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        boolean isres = st.execute(sql);
        if (findResult == null) {
            return;
        }
        if (!isres) {
            int rc = st.getUpdateCount();
            String sumtime = " (" + String.valueOf(System.currentTimeMillis() - stime) + " ms)";
            findResult.add("执行成功，影响行数:" + rc + sumtime);
            findResult.add("------------------------------------------------------");
            findResult.add("");
        } else {
            ResultSet rs = st.getResultSet();
            ResultSetMetaData rmd = rs.getMetaData();
            int count = rmd.getColumnCount();
            int rownum = 0;
            while (rs.next()) {
                String row;
                for (int ii = 1; ii <= count; ii++) {
                    row = rmd.getColumnName(ii) + ":";
                    String data = JdbcUtils.getResultSetStringValue(rs, ii);
                    if (data == null) {
                        findResult.add(row + "null");
                    } else {
                        String[] datas = data.split("\n");
                        findResult.add(row + datas[0]);
                        if (datas.length > 1) {
                            for (int iii = 1; iii < datas.length; iii++) {
                                findResult.add(datas[iii]);
                            }
                        }
                    }
                }
                rownum++;
                findResult.add("------------------------------------------------------");
                if (rownum >= 100) {
                    break;
                }
            }
            String sumtime = " (" + String.valueOf(System.currentTimeMillis() - stime) + " ms)";
            findResult.add("查询成功，返回前" + String.valueOf(rownum) + "行记录" + sumtime);
            findResult.add("------------------------------------------------------");
            findResult.add("");
        }
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param is
     * @return long
     * @throws SortedSQLException
     * @throws IOException
     */
    public static long doScript(Connection conn, InputStream is) throws SortedSQLException, IOException {
        return doScript(conn, null, is);
    }

    /**
     * 执行脚本
     *
     * @param conn
     * @param findResult
     * @param is
     * @return long
     * @throws SortedSQLException
     * @throws IOException
     */
    public static long doScript(Connection conn, List<String> findResult, InputStream is) throws SortedSQLException, IOException {
        Statement st = null;
        long stime = System.currentTimeMillis();
        try {
            st = conn.createStatement();
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String tmp;
            while (true) {
                tmp = in.readLine();
                if (tmp == null) {
                    break;
                }
                tmp = tmp.trim();
                if (tmp.equalsIgnoreCase("go")) {
                    tmp = ";";
                }
                if (tmp.startsWith("/*")) {
                    continue;
                } else if (tmp.startsWith("--")) {
                    continue;
                } else if (tmp.equals("/")) {
                    continue;
                } else {
                    if (tmp.toLowerCase().startsWith("create ")) {
                        executeSQL(findResult, st, sb.toString());
                        sb = new StringBuilder();
                    }
                    sb.append(tmp);
                    sb.append("\r\n");
                }
            }
            if (!sb.toString().trim().isEmpty()) {
                executeSQL(findResult, st, sb.toString());
            }
        } catch (SQLException ex) {
            if (findResult != null) {
                findResult.add("执行失败!");
                findResult.add("SQLState:" + ex.getSQLState());
                findResult.add("ErrorCode:" + ex.getErrorCode());
                findResult.add(ex.getMessage());
            }
            throw new SortedSQLException(conn, ex);
        } finally {
            JdbcUtils.closeStatement(st);
        }
        return System.currentTimeMillis() - stime;
    }
}
