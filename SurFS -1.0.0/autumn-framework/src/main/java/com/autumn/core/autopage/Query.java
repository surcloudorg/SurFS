/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

/**
 * <p>Title: AUTOPAGE-查询配置</p>
 *
 * <p>Description: 解析xml脚本中的（query）查询配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Query {

    private List<QueryField> fields = new ArrayList<QueryField>();//查询条件
    private List<Column> columns = new ArrayList<Column>();//展示的列
    private String caption = "搜索";//按钮名
    private String sql = "";//查询语句
    private int allowjump = -1;//是否允许翻页，0/1--〉false/true
    private int pagesize = 20;//每页行数
    private ActionConfig config = null;

    /**
     * 解析
     *
     * @param query
     * @param cfg
     * @throws Exception
     */
    public Query(Element query, ActionConfig cfg) throws Exception {
        this.config = cfg;
        sql = query.getAttributeValue("sql");
        if (sql == null || sql.trim().equals("")) {
            sql = "select * from " + cfg.getTable();
        } else {
            sql = sql.trim();
        }
        caption = query.getAttributeValue("caption");
        if (caption == null || caption.trim().equals("")) {
            caption = "搜索";
        } else {
            caption = caption.trim();
        }
        String jump = query.getAttributeValue("allowjump");
        if (jump != null) {
            if (jump.trim().equalsIgnoreCase("true")) {
                allowjump = 1;
            } else {
                allowjump = 0;
            }
        }
        String size = query.getAttributeValue("pagesize");
        if (size != null && (!size.trim().equals(""))) {
            try {
                pagesize = Integer.parseInt(size);
            } catch (Exception e) {
            }
        }
        parseParam(query.getChildren("param"));
        parseColnum(query.getChildren("column"));
    }

    /**
     * 解析列
     *
     * @param dataList
     * @throws Exception
     */
    private void parseColnum(List dataList) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            throw new Exception("没有设置列属性");
        } else {
            Iterator i = dataList.iterator();
            while (i.hasNext()) {
                Element ee = (Element) i.next();
                Column col = new Column();
                col.setAlign(ee.getAttributeValue("align"));
                col.setCaption(ee.getAttributeValue("caption"));
                col.setCheck(ee.getAttributeValue("check"));
                col.setLink(ee.getAttributeValue("link"));
                col.setText(ee.getAttributeValue("text"));
                col.setWidth(ee.getAttributeValue("width"));
                String orderfield = ee.getAttributeValue("orderfield");
                String order = ee.getAttributeValue("order");
                if (orderfield != null) {
                    orderfield = orderfield.trim().toLowerCase();
                    if (config.getActionFields().get(orderfield) == null) {
                        throw new Exception("排序字段" + orderfield + "没有在字段属性表里设置");
                    }
                    col.setOrderField(orderfield);
                }
                if (order != null && orderfield != null) {
                    order = order.trim();
                    if (order.equalsIgnoreCase("asc")) {
                        col.setOrder("asc");
                    } else {
                        col.setOrder("desc");
                    }
                }
                columns.add(col);
            }
        }
        if (columns.isEmpty()) {
            throw new Exception("没有设置列属性");
        }
    }

    /**
     * 解析查询条件
     *
     * @param dataList
     * @throws Exception
     */
    private void parseParam(List dataList) throws Exception {
        if (!(dataList == null || dataList.isEmpty())) {
            Iterator i = dataList.iterator();
            while (i.hasNext()) {
                Element ee = (Element) i.next();
                String name = ee.getAttributeValue("name");
                String like = ee.getAttributeValue("relation");
                String defaultvalue = ee.getAttributeValue("default");
                if (name == null || name.trim().equals("")) {
                    throw new Exception("查询参数name没有设置");
                } else {
                    name = name.trim().toLowerCase();
                }
                HashMap<String, ActionField> actionFields = config.getActionFields();
                ActionField af = actionFields.get(name);
                if (af == null) {
                    throw new Exception("查询参数" + name + "没有在字段属性表里设置");
                }
                if (af.getSize() > 400) {
                    throw new Exception("字段" + name + "长度超过400,不适合作为查询条件");
                }
                QueryField qf = new QueryField(af);
                qf.setDefaultValue(defaultvalue);
                qf.setRelation(like);
                if (this.getField(name) == null) {
                    fields.add(qf);
                }
            }
        }
    }

    /**
     * @return the fields
     */
    public List<QueryField> getFields() {
        return fields;
    }

    public QueryField getField(String name) {
        for (QueryField ef : fields) {
            if (ef.getName().equalsIgnoreCase(name)) {
                return ef;
            }
        }
        return null;
    }

    /**
     * @return the fields
     */
    public List<QueryField> getFieldsCopy() {
        List<QueryField> list = new ArrayList<QueryField>();
        for (QueryField q : fields) {
            list.add(q.newCopy());
        }
        return list;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @return the columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @return the allowjump
     */
    public int getAllowjump() {
        return allowjump;
    }

    /**
     * @return the pagesize
     */
    public int getPagesize() {
        return pagesize;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }
}
