package com.autumn.core.autopage;

import com.autumn.core.log.Logger;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.sql.SmRowSetEx;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Menu;
import com.autumn.util.TextUtils;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

/**
 * <p>Title: AUTOACTION</p>
 *
 * <p>Description: 执行表查询操作</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class QueryAction {

    private static final String SUBMIT_BUTTON_NAME = "query_button";//查询按钮名
    private AutoAction autoaction = null;//action
    private String query_button = null;//查询按钮
    private List<QueryField> queryFields = null;//查询字段
    private Navigation navi = null;//导航条
    private Connection con = null;
    private Logger log = null;

    public QueryAction(AutoAction autoaction) {
        this.autoaction = autoaction;
        this.log = autoaction.getLog().getLogger(QueryAction.class);
    }

    //从request,session,default获取查询字段值
    private void makeQueryFields() {
        String url = autoaction.getRequest().getRequestURI().toLowerCase();
        queryFields = autoaction.getCfg().getQueryConfig().getFieldsCopy();
        if (queryFields.isEmpty()) {
            return;
        }
        if (query_button != null) {//从request里获取
            for (QueryField qf : queryFields) {
                String name = qf.getName();
                if (qf.getRelation().equals("between")) {
                    String valuebegin = autoaction.getRequest().getParameter(name + "_begin");
                    String valueend = autoaction.getRequest().getParameter(name + "_end");
                    String res = qf.isAvalid(valuebegin);
                    if (res != null) {
                        qf.setFieldValue("");
                    } else {
                        qf.setFieldValue(valuebegin);
                    }
                    autoaction.setSessionAttribute(url + "-" + name + "_begin", qf.getFieldValue());
                    res = qf.isAvalid(valueend);
                    if (res != null) {
                        qf.setFieldValue2("");
                    } else {
                        qf.setFieldValue2(valueend);
                    }
                    autoaction.setSessionAttribute(url + "-" + name + "_end", qf.getFieldValue2());
                } else {
                    String value = autoaction.getRequest().getParameter(name);
                    qf.setFieldValue(value);
                    String res = qf.isAvalid();
                    if (res != null) {
                        qf.setFieldValue("");
                    }
                    autoaction.setSessionAttribute(url + "-" + name, qf.getFieldValue());
                }
            }
        } else {
            for (QueryField qf : queryFields) {
                String name = qf.getName();
                if (qf.getRelation().equals("between")) {
                    String valuebegin = (String) autoaction.getSessionAttribute(url + "-" + name + "_begin");
                    String valueend = (String) autoaction.getSessionAttribute(url + "-" + name + "_end");
                    if (valuebegin == null) {
                        valuebegin = "";
                    }
                    if (valueend == null) {
                        valueend = "";
                    }
                    String res = qf.isAvalid(valuebegin);
                    if (res != null) {
                        qf.setFieldValue("");
                    } else {
                        qf.setFieldValue(valuebegin);
                    }
                    res = qf.isAvalid(valueend);
                    if (res != null) {
                        qf.setFieldValue2("");
                    } else {
                        qf.setFieldValue2(valueend);
                    }
                } else {
                    String value = (String) autoaction.getSessionAttribute(url + "-" + name);
                    if (value == null) {
                        value = "";
                    }
                    qf.setFieldValue(value);
                    String res = qf.isAvalid();
                    if (res != null) {
                        qf.setFieldValue("");
                    }
                }
            }
        }
    }

    /**
     * 生成查询区域的html,一行两个字段,最后加查询按钮
     *
     * @return List<String>
     */
    private List<String> makeFieldHtml() {
        List<String> list = new ArrayList<String>();
        String ss = "";
        StringBuilder clear = new StringBuilder();
        for (QueryField qf : queryFields) {
            String s = qf.getHtmlText(qf.getMapping(autoaction.getCfg().getDatasource(), true));
            if (qf.getRelation().equals("between")) {
                clear.append("document.Form.").append(qf.getName()).append("_begin").append(".value='';");
                clear.append("document.Form.").append(qf.getName()).append("_end").append(".value='';");
            } else {
                clear.append("document.Form.").append(qf.getName()).append(".value='';");
            }
            if (ss.equals("")) {
                ss = s;
            } else {
                ss = ss + "&nbsp;&nbsp;" + s;
                list.add(ss);
                ss = "";
            }
        }
        //搜索按钮
        StringBuilder sb = new StringBuilder();
        if (!queryFields.isEmpty()) {
            sb.append("<input name=\"");
            sb.append(SUBMIT_BUTTON_NAME).append("\"  type=\"submit\" class=\"");
            sb.append(ActionField.CLASS_BUTTONBOX).append("\" value=\"");
            sb.append(autoaction.getCfg().getQueryConfig().getCaption());
            sb.append("\" /> &nbsp;&nbsp;");
            //清空按钮
            sb.append("<input type=\"button\" value=\"清空\" class=\"");
            sb.append(EditField.CLASS_BUTTONBOX);
            sb.append("\" onclick=\"javascript:");
            sb.append(clear.toString());
            sb.append("\"/>&nbsp;&nbsp;");
        }
        if (autoaction.getCfg().getInsertConfig() != null) {
            sb.append("<input type=\"button\" value=\"");
            sb.append(autoaction.getCfg().getInsertConfig().getCaption());
            sb.append("\" class=\"").append(EditField.CLASS_BUTTONBOX);
            sb.append("\" onclick=\"location.href='");
            sb.append(autoaction.getActionMap().getActionid());
            sb.append(".insert'\"/>&nbsp;&nbsp;");
        }//添加按钮
        if (ss.equals("")) {//添加按钮
            if (!list.isEmpty()) {
                String str = list.remove(list.size() - 1);
                list.add(str + "&nbsp;&nbsp;" + sb.toString());
            } else {
                String str = sb.toString().trim();
                if (!str.equals("")) {
                    list.add(str);
                }
            }
        } else {
            list.add(ss + "&nbsp;&nbsp;" + sb.toString());
        }
        return list;
    }

    /**
     * 生成查询语句
     *
     * @return String
     * @throws Exception
     */
    private String getSql() throws Exception {
        String sql = autoaction.getCfg().getQueryConfig().getSql();
        sql = Replace.replace(sql, autoaction, "'");
        StringBuilder sb = new StringBuilder();
        for (QueryField qf : queryFields) {
            String name = qf.getFullName();
            if (autoaction.getCfg().isSingleTable) {
                name = qf.getName();
            }
            String value = qf.getFieldValue();
            if (value == null || value.trim().equals("")) {
                continue;
            }
            if (qf.getClassname().equalsIgnoreCase("java.sql.Timestamp") && (!qf.getRelation().equals("between"))) {
                Date date1,date2;
                if (value.length() == 10 || value.endsWith("00:00:00")) {
                    date1 = TextUtils.String2Date(value, "yyyy-MM-dd");
                    date2 = new Date(date1.getTime() + 1000 * 60 * 60 * 24);
                } else {
                    if (value.endsWith("00:00")) {
                        date1 = TextUtils.String2Date(value.substring(0, 13), "yyyy-MM-dd HH");
                        date2 = new Date(date1.getTime() + 1000 * 60 * 60);
                    } else {
                        date1 = TextUtils.String2Date(value.substring(0, 16), "yyyy-MM-dd HH:mm");
                        date2 = new Date(date1.getTime() + 1000 * 60);
                    }
                }
                String d1 = TextUtils.Date2String(date1, "yyyy-MM-dd HH:mm:ss");
                String d2 = TextUtils.Date2String(date2, "yyyy-MM-dd HH:mm:ss");
                sb.append(" ").append(name).append(" between '").append(d1);
                sb.append("' and '").append(d2).append("' and");
            } else {
                if (qf.getRelation().equals("like")) {
                    sb.append(" ").append(name).append(" like '%").append(value).append("%' and");
                } else if (qf.getRelation().equals("between")) {
                    String begin = qf.getFieldValue();
                    String end = qf.getFieldValue2();
                    if ((!begin.equals("")) && (!end.equals(""))) {
                        sb.append(" ").append(name).append(" between '").append(begin).append("' and '").append(end).append("' and");
                    }
                    if ((!begin.equals("")) && (end.equals(""))) {
                        sb.append(" ").append(name).append(" >= '").append(begin).append("' and");
                    }
                    if ((begin.equals("")) && (!end.equals(""))) {
                        sb.append(" ").append(name).append(" <= '").append(end).append("' and");
                    }
                } else {
                    sb.append(" ").append(name).append(" = '").append(value).append("' and");
                }
            }
        }
        String newsql = sb.toString();
        if (newsql.endsWith("and")) {
            newsql = newsql.substring(0, newsql.length() - 3);
        }
        if (!newsql.trim().equals("")) {
            newsql = " where " + newsql;
        }
        String dbtype = con.getMetaData().getDatabaseProductName();
        if (dbtype.equalsIgnoreCase("oracle")) {
            sql = "select * from (" + sql + ") " + newsql;
        } else {
            sql = "select * from (" + sql + ") as tmptab  " + newsql;
        }
        return sql;
    }

    /**
     * 初始化导航条
     */
    private void makeNavigation() {
        int pagesize = autoaction.getCfg().getQueryConfig().getPagesize();
        int jump = autoaction.getCfg().getQueryConfig().getAllowjump();
        try {
            navi = new Navigation(jump, pagesize);
            autoaction.putBean(navi);
            if (navi.getOrderField() == null) {//搜索默认排序字段
                List<Column> columns = autoaction.getCfg().getQueryConfig().getColumns();
                for (Column column : columns) {
                    if (column.getOrder() != null) {
                        navi.setOrderField(column.getOrderField());
                        navi.setOrder(column.getOrder());
                    }
                }
            }
            if (navi.getOrderField() == null) {//无默认按主健排序
                navi.setOrderField(autoaction.getCfg().getPrimarykey());
                navi.setOrder("desc");
            }
        } catch (Exception e) {
        }
    }

    /**
     * 查询数据库
     *
     * @param sql
     * @return ResultSet
     * @throws Exception
     */
    private ResultSet query(String sql) throws Exception {
        Object obj = autoaction.getSessionAttribute(autoaction.getActionMap().getActionid() + "-pageset");
        ResultSet crs = null;
        String primarykey = autoaction.getCfg().getPrimarykey();
        if (autoaction.getCfg().getQueryConfig().getAllowjump() == 1) {
            SmRowSet myrs;
            if (obj != null && obj instanceof SmRowSet) {
                myrs = (SmRowSet) obj;
                myrs.setConn(con);
            } else {
                myrs = new SmRowSet(con, sql, navi.getOrderField(), primarykey);
            }
            if (navi.getOrder().equalsIgnoreCase("asc")) {
                myrs.setKeyOrder(true);
            } else {
                myrs.setKeyOrder(false);
            }
            myrs.setCommandText(sql);
            myrs.setKeyfield(navi.getOrderField());
            myrs.setPagesize(navi.getPageSize());
            navi.setPageCount(myrs.getPageCount());
            navi.setRowCount(myrs.getRowCount());
            if (navi.getPageNum() < 1) {
                navi.setPageNum(1);
            }
            if (navi.getPageNum() > navi.getPageCount()) {
                navi.setPageNum(navi.getPageCount());
            }
            if (myrs.movePage(navi.getPageNum())) {
                crs = myrs.getRowset();
            }
            autoaction.setSessionAttribute(autoaction.getActionMap().getActionid() + "-pageset", myrs);
        } else if (autoaction.getCfg().getQueryConfig().getAllowjump() == 0) {
            SmRowSetEx myrs;
            if (obj != null && obj instanceof SmRowSetEx) {
                myrs = (SmRowSetEx) obj;
                myrs.setConn(con);
            } else {
                myrs = new SmRowSetEx(con, sql, primarykey);
            }
            myrs.setCommandText(sql);
            if (navi.getOrder().equalsIgnoreCase("asc")) {
                myrs.setKeyOrderAsc(true);
            } else {
                myrs.setKeyOrderAsc(false);
            }
            myrs.setPagesize(navi.getPageSize());
            if (autoaction.getRequest().getParameter("direct") == null) {
                myrs.refresh();
            } else {
                if (navi.getDirect() == 0) {
                    myrs.firstPage();
                } else if (navi.getDirect() == 1) {
                    myrs.previousPage();
                } else if (navi.getDirect() == 2) {
                    myrs.nextPage();
                } else if (navi.getDirect() == 3) {
                    myrs.lastPage();
                } else {
                    myrs.refresh();
                }
            }
            crs = myrs.getRowset();
            autoaction.setSessionAttribute(autoaction.getActionMap().getActionid() + "-pageset", myrs);
        } else {
            String dbProductName = con.getMetaData().getDatabaseProductName();
            String newsql;
            if (dbProductName.equalsIgnoreCase("oracle")) {
                newsql = "select * from (" + sql + ") order by " + navi.getOrderField() + " " + navi.getOrder();
            } else {
                newsql = "select * from (" + sql + ") as tmptab order by " + navi.getOrderField() + " " + navi.getOrder();
            }
            crs = JdbcPerformer.executeQuery(con, newsql);
        }
        return crs;
    }

    /**
     * 执行
     *
     * @return ActionForward
     */
    public ActionForward execute() {
        con = autoaction.getConnect(autoaction.getCfg().getDatasource());
        query_button = autoaction.getRequest().getParameter(SUBMIT_BUTTON_NAME);

        makeQueryFields();//设置查询字段的值

        String sql = null;//设置查询语句
        try {
            sql = getSql();
        } catch (Exception e) {
            autoaction.setMessage("查询语句配置错误:" + e.getMessage());
        }

        makeNavigation();//初始化导航条

        List<String[]> list = makeColumnHeadHtml();
        autoaction.setAttribute("columnsize", Integer.toString(list.size()));
        autoaction.setAttribute("columnhead", list);
        autoaction.setAttribute("fields", makeFieldHtml());
        autoaction.setAttribute("navigation", navi);
        autoaction.setAttribute("delete", deleteHtml());
        autoaction.setAttribute("title", (new Menu(autoaction)).getShowMenu());
        autoaction.setAttribute("action", autoaction.getActionMap().getActionid());

        ResultSet rs;//查询数据库
        try {
            rs = query(sql);
            List<List> lists = makeColumnsHtml(rs);
            autoaction.setAttribute("columns", lists);
            if (navi.getJump() < 0) {
                navi.setRowCount(lists.size());
            }
        } catch (Exception e) {
            autoaction.setMessage("查询失败:" + e.getMessage());
            try {
                if (con != null && con.getMetaData().getDatabaseProductName().equalsIgnoreCase("apache derby")) {
                    autoaction.setMessage("autopage工具不支持apache derby数据库，请使用mysql/sqlserver/oracle");
                }
            } catch (Exception rr) {
            }
            log.error("查询失败:" + e.getMessage());
        }
        autoaction.closeConnect(con);
        return new ActionForward("list.jsp");
    }

    /**
     * 生成删除html
     *
     * @return String
     */
    private String deleteHtml() {
        StringBuilder sb = new StringBuilder();
        if (autoaction.getCfg().getDeleteConfig() != null) {
            sb.append("<input name=\"").append(DeleteAction.SUBMIT_BUTTON_NAME);
            sb.append("\" type=\"submit\" class=\"");
            sb.append(EditField.CLASS_BUTTONBOX).append("\" value=\"");
            sb.append(autoaction.getCfg().getDeleteConfig().getCaption());
            sb.append("\" onClick=\"return(confirmdelete('确认删除？'))\"/>&nbsp;&nbsp;");
        }
        return sb.toString();
    }

    /**
     * 生成表格头
     *
     * @return List<String[]>
     */
    private List<String[]> makeColumnHeadHtml() {
        List<String[]> list = new ArrayList<String[]>();
        List<Column> columns = autoaction.getCfg().getQueryConfig().getColumns();
        for (Column column : columns) {
            String img = "";
            if (autoaction.getCfg().getQueryConfig().getAllowjump() == 0) {
                String privatekey = autoaction.getCfg().getPrimarykey();
                if (column.getOrderField() != null && column.getOrderField().equalsIgnoreCase(privatekey)) {//添加图标
                    if (navi.getOrder().equalsIgnoreCase("asc")) {
                        img = "<a href=\"#\" onclick=\"document.Form.order.value='desc';document.Form.order.direct='5';document.Form.submit();\"> <img src=\"../img/pub/up.gif\" border=\"0\" /></a>";
                    } else {
                        img = "<a href=\"#\" onclick=\"document.Form.order.value='asc';document.Form.order.direct='5';document.Form.submit();\"> <img src=\"../img/pub/down.gif\" border=\"0\" /></a>";
                    }
                }
            } else {
                if (column.getOrderField() != null) {//添加图标
                    if (column.getOrderField().equalsIgnoreCase(navi.getOrderField())) {
                        if (navi.getOrder().equalsIgnoreCase("asc")) {
                            img = "<a href=\"#\" onclick=\"document.Form.order.value='desc';document.Form.submit();\"> <img src=\"../img/pub/up.gif\" border=\"0\" /></a>";
                        } else {
                            img = "<a href=\"#\" onclick=\"document.Form.order.value='asc';document.Form.submit();\"> <img src=\"../img/pub/down.gif\" border=\"0\" /></a>";
                        }
                    } else {
                        img = "<a href=\"#\" onclick=\"document.Form.orderField.value='" + column.getOrderField()
                                + "';document.Form.submit();\"> <img src=\"../img/pub/downup.gif\" border=\"0\" /></a>";
                    }
                }
            }

            String[] col = new String[3];
            col[0] = Integer.toString(column.getWidth()) + "%";
            col[1] = column.getAlign();
            if (column.isCheck()) {
                String caption = column.getCaption();
                StringBuilder sb = new StringBuilder();
                sb.append("<input type=\"checkbox\" name=\"checkbox\" value=\"checkbox\" onClick='selectall(this.checked)'/>");
                sb.append("<a href=\"javascript:selectall(document.Form.checkbox.checked);\" onClick=\"document.Form.checkbox.checked=!document.Form.checkbox.checked\">");
                sb.append(caption);
                sb.append("</a>");
                col[2] = sb.toString();
            } else {
                col[2] = img + column.getCaption();
            }
            list.add(col);
        }
        return list;
    }

    /**
     * 生成表格html
     *
     * @param rs
     * @return List<String[]>
     * @throws Exception
     */
    private List<String[]> makeColumnHtml(ResultSet rs) throws Exception {
        List<String[]> list = new ArrayList<String[]>();
        HashMap<String, String> map = null;
        try {
            map = Edit.getResultSet(rs);//获取记录，存入hash
        } catch (Exception e) {
            throw new Exception("读取记录时出现错误:" + e.getMessage());
        }
        //先替换列参数中link中的${field},这需要字段值表未替换之前来做
        List<Column> columns = autoaction.getCfg().getQueryConfig().getColumns();
        for (Column column : columns) {
            String[] col = new String[3];
            col[0] = Integer.toString(column.getWidth()) + "%";
            col[1] = column.getAlign();
            String link = column.getLink();
            if (link == null || link.trim().equals("")) {
                link = "";
            } else {
                for(Map.Entry<String,String> entry:map.entrySet()){
                    String name=entry.getKey();
                    String value=entry.getValue();
                    value = URLEncoder.encode(value, autoaction.getWebDirectory().getCharset());
                    link = Replace.replace(link, name, value);
                }
                link = Replace.replace(link, autoaction, "");
            }
            col[2] = link;
            list.add(col);
        }
        //替换存在hashmap的字段值
        for(Map.Entry<String,String> entry:map.entrySet()){
            String name=entry.getKey();
            HashMap<String, ActionField> actionFields = autoaction.getCfg().getActionFields();
            ActionField af = actionFields.get(name);
            if (af != null) {
                HashMap<String, String> mapping = af.getMapping(autoaction.getCfg().getDatasource(), false);
                if (mapping == null) {
                    continue;
                }
                String value = entry.getValue();
                String newvalue = mapping.get(value);
                if (newvalue != null) {
                    entry.setValue(newvalue);
                }
            } else {
                continue;
            }
        }
        //替换text中定义的${字段名},${icon.}${loginuser.}
        int ii = 0;
        for (Column column : columns) {
            String[] col = list.get(ii);
            ii++;
            String link = col[2];
            String text = column.getText();
            for(Map.Entry<String,String> entry:map.entrySet()){
                String name=entry.getKey();
                text = Replace.replace(text, name, entry.getValue());
            }
            text = Replace.replaceIcon(text);
            text = Replace.replace(text, autoaction, "");
            String id = map.get(autoaction.getCfg().getPrimarykey());
            if (column.isCheck() && id != null) {//不加连接
                StringBuilder v = new StringBuilder("<input name=\"id_key_name\" type=\"checkbox\" id=\"id");
                v.append(id).append("\" value=\"").append(id).append("\" />");
                v.append("<a href=\"#\" onClick=\"setcheck('id");
                v.append(id).append("')\">");
                v.append(text).append("</a>");
                col[2] = v.toString();
            } else {
                if (link.equals("")) {
                    col[2] = text;
                } else {
                    StringBuilder v = new StringBuilder("<a href=\"");
                    v.append(link).append("\">");
                    v.append(text).append("</a>");
                    col[2] = v.toString();
                }
            }
        }
        return list;
    }

    /**
     * 生成表格数据
     *
     * @param rs
     * @return List<List>
     * @throws Exception
     */
    private List<List> makeColumnsHtml(ResultSet rs) throws Exception {
        List<List> lists = new ArrayList<List>();
        if (rs != null) {
            while (rs.next()) {
                List<String[]> list = makeColumnHtml(rs);
                lists.add(list);
            }
        }
        return lists;
    }
}
