/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 执行表更新操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class UpdateAction {

    private static final String SUBMIT_BUTTON_NAME = "update_button";//更新按钮名
    private AutoAction autoaction = null;
    private String update_button = null;
    private String primarykeyValue = null;
    private String oldprimarykeyValue = null;
    private String primarykey = null;
    private List<EditField> updateFields = null;
    private Connection con = null;

    public UpdateAction(AutoAction autoaction) {
        this.autoaction = autoaction;
    }

    /**
     * 设置字段提交值
     *
     * @return String
     */
    private String setEditFields() {
        for (EditField qf : updateFields) {
            String name = qf.getName();
            String value = autoaction.getRequest().getParameter(name);
            qf.setFieldValue(value);
        }
        for (EditField qf : updateFields) {//设置默认值
            if (qf.isHiden() || qf.isReadonly()) {
                continue;
            }
            String res = qf.isAvalid();
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    /**
     * 执行
     *
     * @return Forward
     */
    public Forward execute() {
        primarykey = autoaction.getCfg().getPrimarykey();
        update_button = autoaction.getRequest().getParameter(SUBMIT_BUTTON_NAME);
        primarykeyValue = autoaction.getRequest().getParameter(primarykey);
        oldprimarykeyValue = autoaction.getRequest().getParameter("old_" + primarykey);
        updateFields = autoaction.getCfg().getUpdateConfig().getFieldsCopy();
        String datasource = autoaction.getCfg().getDatasource();
        con = autoaction.getConnect(datasource);
        if (update_button == null) {//显示需要查询
            if (primarykeyValue == null) {
                autoaction.closeConnect(con);
                return autoaction.execute();
            }
            try {
                query();
                setCommonParam();
                autoaction.closeConnect(con);
                return new ActionForward("details.jsp");
            } catch (Exception e) {
                autoaction.setMessage("无法查看" + primarykey + "=" + primarykeyValue + "的记录," + e.getMessage());
                try {
                    if (con != null && con.getMetaData().getDatabaseProductName().equalsIgnoreCase("apache derby")) {
                        autoaction.setMessage("autopage工具不支持apache derby数据库，请使用mysql/sqlserver/oracle");
                    }
                } catch (Exception rr) {
                }
                autoaction.getLog().error("无法查看" + primarykey + "=" + primarykeyValue + "的记录," + e.getMessage(), UpdateAction.class);
                autoaction.closeConnect(con);
                return autoaction.execute();
            }
        } else {
            if (oldprimarykeyValue == null) {
                autoaction.closeConnect(con);
                return autoaction.execute();
            }
            String res = setEditFields();
            if (res != null) {
                autoaction.setMessage(res);
                setCommonParam();
                autoaction.closeConnect(con);
                return new ActionForward("details.jsp");
            } else {
                try {
                    update();
                    autoaction.setMessage("更新记录(" + primarykey + "=" + oldprimarykeyValue + ")成功!");
                    autoaction.closeConnect(con);
                    return autoaction.execute();
                } catch (Exception e) {
                    autoaction.setMessage(e.getMessage());
                    setCommonParam();
                    autoaction.closeConnect(con);
                    return new ActionForward("details.jsp");
                }
            }
        }
    }

    /**
     * 生成编辑输入框html
     *
     * @return List<String[]>
     */
    private List<String[]> makeFieldHtml() {
        List<String[]> lists = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        for (EditField qf : updateFields) {
            String s = qf.getHtmlText(qf.getMapping(autoaction.getCfg().getDatasource(), true));
            if (qf.isHiden()) {
                sb.append(s);
                continue;
            }
            String[] line = new String[2];
            line[0] = qf.getComment();
            line[1] = s;
            lists.add(line);
        }
        String ss = sb.toString();
        if (!ss.trim().equals("")) {
            if (lists.isEmpty()) {
                String[] strs = new String[]{"", ss};
                lists.add(strs);
            } else {
                String[] strs = lists.get(lists.size() - 1);
                String s = strs[1];
                s = s + ss;
                strs[1] = s;
            }
        }
        return lists;
    }

    /**
     * 生成按钮html
     *
     * @return String
     */
    private String makeButtonHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<input name=\"" + SUBMIT_BUTTON_NAME + "\" type=\"submit\" class=\"");
        sb.append(ActionField.CLASS_BUTTONBOX);
        sb.append("\" value=\"").append(autoaction.getCfg().getUpdateConfig().getCaption());
        sb.append("\" />&nbsp;&nbsp;&nbsp;&nbsp;");
        sb.append("<input type=\"button\" value=\"返回\" class=\"").append(EditField.CLASS_BUTTONBOX);
        sb.append("\" onclick=\"location.href='");
        sb.append(autoaction.getActionMap().getActionid());
        sb.append("'\"/>&nbsp;&nbsp;");
        sb.append("<input name=\"old_").append(primarykey);
        sb.append("\" type=\"hidden\"  value=\"").append(primarykeyValue).append("\" />");
        return sb.toString();
    }

    /**
     * 生成其他html
     */
    private void setCommonParam() {
        Menu m = new Menu(autoaction);
        autoaction.setAttribute("action", autoaction.getActionMap().getActionid() + ".update");
        autoaction.setAttribute("title", m.getShowMenu() + "-" + autoaction.getCfg().getUpdateConfig().getCaption());
        autoaction.setAttribute("button", makeButtonHtml());
        autoaction.setAttribute("params", makeFieldHtml());
    }

    /**
     * 查询待更新的数据
     *
     * @throws Exception
     */
    private void update() throws Exception {
        String sql = autoaction.getCfg().getUpdateConfig().getSql();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (sql == null || sql.trim().equals("")) {
            String table = autoaction.getCfg().getTable();
            StringBuilder sb = new StringBuilder(" set ");
            String where = " where " + primarykey + "='" + oldprimarykeyValue + "'";
            int ii = 1;
            for (EditField ef : updateFields) {
                String name = ef.getName();
                if (ef.isHiden() || ef.isReadonly()) {
                    continue;
                }
                map.put(name.toLowerCase(), Integer.valueOf(ii));
                sb.append(name).append("=?,");
                ii++;
            }
            String ss = sb.toString();
            if (ss.endsWith(",")) {
                ss = ss.substring(0, ss.length() - 1);
            }
            sql = "update " + table + ss + where;
        } else {
            sql = Replace.replace(sql, "old." + primarykey, "'" + oldprimarykeyValue + "'");
            sql = Replace.replaceSQL(sql, map);//首先替换没有点好的
            Set<String> set = map.keySet();
            for (String name : set) {
                EditField ef = autoaction.getCfg().getUpdateConfig().getField(name);
                if (ef.isHiden() || ef.isReadonly()) {
                    throw new Exception("插入失败:字段" + name + "不允许编辑");
                }
            }
            sql = Replace.replace(sql, autoaction, "");
        }
        try {
            PreparedStatement prest = con.prepareStatement(sql);
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                EditField qf = Edit.getField(entry.getKey(), updateFields);
                Edit.setStatementParam(con, prest, qf, entry.getValue().intValue());
            }
            prest.executeUpdate();
        } catch (Exception e) {
            autoaction.getLog().error("更新失败:" + sql + "," + e.getMessage(), UpdateAction.class);
            throw new Exception("更新失败:" + sql + "," + e.getMessage());
        }
    }

    private void query() throws Exception {
        String table = autoaction.getCfg().getTable();
        StringBuilder filenames = new StringBuilder("select ");
        int count = updateFields.size();
        for (int ii = 0; ii < count; ii++) {
            EditField ef = updateFields.get(ii);
            if (ii < count - 1) {
                filenames.append(ef.getName()).append(",");
            } else {
                filenames.append(ef.getName());
            }
        }
        filenames.append(" from ");
        filenames.append(table);
        filenames.append(" where ");
        filenames.append(primarykey);
        filenames.append("='");
        filenames.append(primarykeyValue);
        filenames.append("'");
        String sql = filenames.toString();
        ResultSet rs = JdbcPerformer.executeQuery(con, sql);
        if (rs.next()) {
            Edit.getResultSet(rs, updateFields);
        } else {
            throw new Exception("找不到记录");
        }
    }
}
