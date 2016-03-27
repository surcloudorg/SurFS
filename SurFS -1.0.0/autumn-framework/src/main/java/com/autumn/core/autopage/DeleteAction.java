/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.util.Function;
import java.sql.Connection;

/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 执行表删除操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DeleteAction {

    public static final String SUBMIT_BUTTON_NAME = "delete_button";//删除按钮名
    private AutoAction autoaction = null;

    public DeleteAction(AutoAction autoaction) {
        this.autoaction = autoaction;
    }

    /**
     * 执行
     */
    public void execute() {
        String sql = autoaction.getCfg().getDeleteConfig().getSql();
        String keyname = autoaction.getCfg().getPrimarykey();
        String[] ids = autoaction.getRequest().getParameterValues(ActionConfig.ID_KEY_NAME);
        if (ids == null || ids.length == 0) {
            autoaction.setMessage("没有选中需要删除的记录");
            return;
        }
        String idstr = Function.arraytoString(ids);
        if (sql == null || sql.trim().equals("")) {
            String table = autoaction.getCfg().getTable();
            sql = "delete from " + table + " where " + keyname + " in(" + idstr + ")";

        } else {
            //检查是否有${keyname}
            if (Replace.find(sql, keyname) == -1) {
                autoaction.setMessage("删除语句中没有包含主健" + keyname + "的条件语句");
                return;
            }
            //首先替换ids
            sql = Replace.replace(sql, keyname, idstr);
            try {
                sql = Replace.replace(sql, autoaction, "'");
            } catch (Exception e) {
                autoaction.setMessage(e.getMessage());
                return;
            }
        }
        Connection con = null;
        try {
            String datasource = autoaction.getCfg().getDatasource();
            con = autoaction.getConnect(datasource);
            int rt = con.createStatement().executeUpdate(sql);
            if (rt <= 0) {
                autoaction.setMessage("你没有权限删除选定的记录!");
            } else {
                if (idstr.length() < 10) {
                    autoaction.setMessage(keyname + "(" + idstr + ")删除成功");
                } else {
                    autoaction.setMessage(keyname + "(" + idstr.substring(0, 10) + ")删除成功");
                }
            }
        } catch (Exception e) {
            autoaction.setMessage("删除失败,请检查删除语句是否正确!");
            try {
                if (con != null && con.getMetaData().getDatabaseProductName().equalsIgnoreCase("apache derby")) {
                    autoaction.setMessage("autopage工具不支持apache derby数据库，请使用mysql/sqlserver/oracle");
                }
            } catch (Exception rr) {
            }
            autoaction.error("删除失败:" + sql + "," + e.getMessage());
        }
        autoaction.closeConnect(con);
    }
}
