/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.web.SessionMethod;


/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 翻页导航属性</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Navigation {

    private int rowCount = 0; //当前查询记录数
    private int pageCount = 0; //查询总页数
    private int pageSize = 20; //每页记录数
    private int pageNum = 1; //当前页码
    private int direct = 0;//0，1，2，3--〉首页，上页，下页，末页
    private int jump = 1;//允许跳页 0，1 -〉false，true
    private String orderField = null;//排序字段
    private String order = null;//asc/desc

    public Navigation(int jump, int pageSize) {
        this.pageSize = pageSize;
        this.jump = jump;
    }

    /**
     * 生成html
     *
     * @return String
     */
    public String getHtmltext() {
        if (getJump() == 1) {
            return getJumpHtmlText();
        } else if (getJump() == 0) {
            return getNoJumpHtmlText();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("共").append(rowCount).append("条记录");
            sb.append("<input name=\"orderField\" type=\"hidden\" id=\"orderField\" value=\"").append(orderField).append("\" />");
            sb.append("<input name=\"order\" type=\"hidden\" id=\"order\" value=\"").append(order).append("\" />");
            return sb.toString();
        }
    }

    /**
     * 生成不可跳页html
     *
     * @return String
     */
    private String getNoJumpHtmlText() {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name=\"pageSize\" class=\"");
        sb.append(ActionField.CLASS_AUTO_TEXTBOX);
        sb.append("\" id=\"pageSize\" onchange=\"document.Form.submit();\">");
        for (int ii = 10; ii <= 50; ii = ii + 10) {
            String sel = "";
            if (pageSize == ii) {
                sel = " selected=\"selected\"";
            }
            sb.append("<option value=\"").append(ii).append("\"");
            sb.append(sel).append(">").append(ii).append("</option>");
        }
        sb.append("</select> 条/页， ");
        sb.append("[<a href=\"#\" onclick=\"document.Form.direct.value='0';document.Form.submit();\">首页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.direct.value='1';document.Form.submit();\">上一页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.direct.value='2';document.Form.submit();\">下一页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.direct.value='3';document.Form.submit();\">尾页</a>]");
        sb.append("<input name=\"direct\" type=\"hidden\" id=\"direct\" value=\"0\" />");
        sb.append("<input name=\"orderField\" type=\"hidden\" id=\"orderField\" value=\"").append(orderField).append("\" />");
        sb.append("<input name=\"order\" type=\"hidden\" id=\"order\" value=\"").append(order).append("\" />");
        return sb.toString();
    }

    /**
     * 生成可跳页html
     *
     * @return String
     */
    private String getJumpHtmlText() {
        StringBuilder sb = new StringBuilder();
        sb.append("共").append(rowCount).append("条记录，");
        sb.append("<select name=\"pageSize\" class=\"");
        sb.append(ActionField.CLASS_AUTO_TEXTBOX);
        sb.append("\" id=\"pageSize\" onchange=\"document.Form.submit();\">");
        for (int ii = 10; ii <= 50; ii = ii + 10) {
            String sel = "";
            if (pageSize == ii) {
                sel = " selected=\"selected\"";
            }
            sb.append("<option value=\"").append(ii).append("\"");
            sb.append(sel).append(">").append(ii).append("</option>");
        }
        sb.append("</select> 条/页， 当前").append(pageNum).append("/").append(pageCount).append("页， ");
        sb.append("[<a href=\"#\" onclick=\"document.Form.pageNum.value='1';document.Form.submit();\">首页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.pageNum.value=").append(pageNum).append("-1;document.Form.submit();\">上一页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.pageNum.value=").append(pageNum).append("+1;document.Form.submit();\">下一页</a>]&nbsp;");
        sb.append("[<a href=\"#\" onclick=\"document.Form.pageNum.value=").append(pageCount).append(";document.Form.submit();\">尾页</a>]");
        if (pageCount > 0) {
            sb.append("， 跳至<select name=\"pageNum\" class=\"");
            sb.append(ActionField.CLASS_AUTO_TEXTBOX);
            sb.append("\" id=\"pageNum\" onchange=\"document.Form.submit();\">");
            for (int ii = 1; ii <= pageCount; ii++) {
                String sel = "";
                if (pageNum == ii) {
                    sel = " selected=\"selected\"";
                }
                sb.append("<option value=\"").append(ii).append("\"");
                sb.append(sel).append(">").append(ii).append("</option>");
            }
            sb.append("</select>页");
        }
        sb.append("<input name=\"orderField\" type=\"hidden\" id=\"orderField\" value=\"").append(orderField).append("\" />");
        sb.append("<input name=\"order\" type=\"hidden\" id=\"order\" value=\"").append(order).append("\" />");
        return sb.toString();
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
     * @return the pageCount
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * @param pageCount the pageCount to set
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    @SessionMethod
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    @SessionMethod
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the direct
     */
    public int getDirect() {
        return direct;
    }

    /**
     * @param direct the direct to set
     */
    @SessionMethod
    public void setDirect(int direct) {
        this.direct = direct;
    }

    /**
     * @return the orderField
     */
    public String getOrderField() {
        return orderField;
    }

    /**
     * @param orderField the orderField to set
     */
    @SessionMethod
    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    @SessionMethod
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * @return the jump
     */
    public int getJump() {
        return jump;
    }

    /**
     * @param jump the jump to set
     */
    public void setJump(int jump) {
        this.jump = jump;
    }
}
