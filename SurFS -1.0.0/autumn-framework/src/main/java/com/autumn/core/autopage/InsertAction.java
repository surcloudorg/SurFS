/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 执行表写入操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class InsertAction {

    private static final String SUBMIT_BUTTON_NAME = "insert_button";//插入按钮名
    private AutoAction autoaction = null;
    private String insert_button = null;
    private List<EditField> insertFields = null;

    public InsertAction(AutoAction autoaction) {
        this.autoaction = autoaction;
    }

    /**
     * 设置字段提交值
     *
     * @return String
     */
    private String setEditFields() {
        for (EditField qf : insertFields) {
            String name = qf.getName();
            String value = autoaction.getRequest().getParameter(name);
            qf.setFieldValue(value);
        }
        for (EditField qf : insertFields) {//设置默认值
            String res = qf.isAvalid();
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    /**
     * 生成编辑按钮html
     *
     * @return String
     */
    private String makeButtonHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<input name=\"" + SUBMIT_BUTTON_NAME + "\" type=\"submit\" class=\"");
        sb.append(ActionField.CLASS_BUTTONBOX);
        sb.append("\" value=\"").append(autoaction.getCfg().getInsertConfig().getCaption());
        sb.append("\" />&nbsp;&nbsp;&nbsp;&nbsp;");
        sb.append("<input type=\"button\" value=\"返回\" class=\"").append(EditField.CLASS_BUTTONBOX);
        sb.append("\" onclick=\"location.href='");
        sb.append(autoaction.getActionMap().getActionid());
        sb.append("'\"/>&nbsp;&nbsp;");
        return sb.toString();
    }

    /**
     * 生成编辑输入框html
     *
     * @return List<String[]>
     */
    private List<String[]> makeFieldHtml() {
        List<String[]> lists = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        for (EditField qf : insertFields) {
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
     * 执行
     *
     * @return Forward
     */
    public Forward execute() {
        insert_button = autoaction.getRequest().getParameter(SUBMIT_BUTTON_NAME);
        insertFields = autoaction.getCfg().getInsertConfig().getFieldsCopy();
        if (insert_button == null) {//显示默认值
            for (EditField qf : insertFields) {//设置默认值
                qf.setFieldValue(qf.getDefaultValue());//默认值需要支持变量
            }//需要生成html
            setCommonParam();
            return new ActionForward("details.jsp");
        } else {
            String res = setEditFields();
            if (res != null) {
                setCommonParam();
                autoaction.setMessage(res);
                return new ActionForward("details.jsp");
            } else {
                try {
                    insert();
                    autoaction.setMessage("添加成功!");
                    setCommonParam();
                    return autoaction.execute();
                } catch (Exception e) {
                    autoaction.setMessage(e.getMessage());
                    setCommonParam();
                    return new ActionForward("details.jsp");
                }
            }
        }
    }

    /**
     * 写入
     *
     * @throws Exception
     */
    private void insert() throws Exception {
        String sql = autoaction.getCfg().getInsertConfig().getSql();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (sql == null || sql.trim().equals("")) {
            String table = autoaction.getCfg().getTable();
            StringBuilder sb = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();
            int count = insertFields.size();
            for (int ii = 1; ii <= count; ii++) {
                String name = insertFields.get(ii - 1).getName();
                map.put(name.toLowerCase(), Integer.valueOf(ii));
                if (ii < count) {
                    sb.append(name).append(",");
                    sb1.append("?,");
                } else {
                    sb.append(name);
                    sb1.append("?");
                }
            }
            sql = "insert into " + table + "(" + sb.toString() + ")values(" + sb1.toString() + ")";
        } else {
            sql = Replace.replaceSQL(sql, map);//首先替换没有点好的
            sql = Replace.replace(sql, autoaction, "'");
        }
        String datasource = autoaction.getCfg().getDatasource();
        Connection con = autoaction.getConnect(datasource);
        try {
            PreparedStatement prest = con.prepareStatement(sql);
            for (EditField qf : insertFields) {
                String name = qf.getName().toLowerCase();
                Integer ii = map.get(name);
                Edit.setStatementParam(con, prest, qf, ii.intValue());
            }
            prest.executeUpdate();
            autoaction.closeConnect(con);
        } catch (Exception e) {
            autoaction.closeConnect(con);
            autoaction.getLog().error("插入失败:" + sql + "," + e.getMessage(), InsertAction.class);
            throw new Exception("插入失败,请检查输入的数据是否有效");
        }
    }

    /**
     * 生成其他位置html
     */
    private void setCommonParam() {
        Menu m = new Menu(autoaction);
        autoaction.setAttribute("action", autoaction.getActionMap().getActionid() + ".insert");
        autoaction.setAttribute("title", m.getShowMenu() + "-" + autoaction.getCfg().getInsertConfig().getCaption());
        autoaction.setAttribute("button", makeButtonHtml());
        autoaction.setAttribute("params", makeFieldHtml());
    }
}
