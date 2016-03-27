/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.util.TextUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

/**
 * <p>Title: AUTOPAGE-表字段附加属性</p>
 *
 * <p>Description: 脚本中设置的附加属性，一般包含字段注释，或多表关联时的fullname</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ActionField extends Field {
    //这里指定了几个页面元素显示的css名称

    public static final String CLASS_TEXTBOX = "textbox";
    public static final String CLASS_AUTO_TEXTBOX = "textbox1";
    public static final String CLASS_BIG_TEXTBOX = "textbox2";
    public static final String CLASS_TEXTAREA = "textarea";
    public static final String CLASS_BUTTONBOX = "bottonbox";
    
    private String comment = ""; //注释
    private String fullName = ""; //字段名，如（tabname.fieldname）
    private String defaultValue = "";//默认值
    private String datefmt = "yyyy-MM-dd";//时间格式
    //组合框的列表内容
    private List items = null;
    private String fieldValue = "";//查询，编辑提交的字段值
    private String fieldValue2 = "";//当查询按照between查询时，下标值

    public ActionField(Field info) {
        this.setClassname(info.getClassname());
        this.setIncrement(info.isIncrement());
        this.setName(info.getName());
        this.setNullable(info.getNullable());
        this.setSize(info.getSize());
        this.setType(info.getType());
        this.setTypeNum(info.getTypeNum());
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String isAvalid() {
        String value = this.getFieldValue();
        return isAvalid(value);
    }

    /**
     * 检测输入字段值是否有效
     *
     * @param value
     * @return 无效的原因
     */
    public String isAvalid(String value) {
        if (value == null || value.equals("")) {
            return null;
        }
        if (this.getClassname().equalsIgnoreCase("java.lang.Float")
                || this.getClassname().equalsIgnoreCase("java.lang.Double")
                || this.getClassname().equalsIgnoreCase("java.math.BigDecimal")) {
            try {
                BigDecimal ss = new BigDecimal(value);
            } catch (Exception e) {
                return "字段" + this.getComment() + "是浮点数!";
            }
        } else if (this.getClassname().equalsIgnoreCase("java.lang.Integer")
                || this.getClassname().equalsIgnoreCase("java.lang.Long")
                || this.getClassname().equalsIgnoreCase("java.math.BigInteger")
                || this.getClassname().equalsIgnoreCase("java.lang.Short")) {
            try {
                BigInteger ss = new BigInteger(value);
            } catch (Exception e) {
                return "字段" + this.getComment() + "是整数!";
            }
        } else if (this.getClassname().equalsIgnoreCase("java.sql.Timestamp")
                || this.getClassname().equalsIgnoreCase("oracle.sql.TIMESTAMP")) {
            try {
                TextUtils.String2Date(value, datefmt);
            } catch (Exception e) {
                return "字段" + this.getComment() + "是时间类型!";
            }
        } else if (this.getClassname().equalsIgnoreCase("java.lang.Boolean")) {
            try {
                TextUtils.parseBoolean(value);
            } catch (Exception e) {
                return "字段" + this.getComment() + "是布尔类型!";
            }
        } else {
            if (value.getBytes().length > this.getSize()) {
                return "字段" + this.getComment() + "长度不能超过" + this.getSize();
            }
        }
        return null;
    }

    /**
     * 判断编辑框类型,日期/输入框/组合框/整数/ 0输入框不限制 1整数,只能输入0-9 2带小数点 3组合框 4日期 5多行
     *
     * @return 需要在不同数据库上详细测试
     */
    public int getEditType() {
        if (items != null && (!items.isEmpty())) {
            return 3;
        }
        if (this.getClassname().equalsIgnoreCase("java.lang.Integer")
                || this.getClassname().equalsIgnoreCase("java.lang.Long")
                || this.getClassname().equalsIgnoreCase("java.math.BigInteger")
                || this.getClassname().equalsIgnoreCase("java.lang.Short")) {
            return 1;
        } else if (this.getClassname().equalsIgnoreCase("java.lang.Float")
                || this.getClassname().equalsIgnoreCase("java.lang.Double")
                || this.getClassname().equalsIgnoreCase("java.math.BigDecimal")) {
            return 2;
        } else if (this.getClassname().equalsIgnoreCase("java.sql.Timestamp")
                || this.getClassname().equalsIgnoreCase("oracle.sql.TIMESTAMP")) {
            return 4;
        } else {
            if (this.getSize() > 80) {
                return 5;
            } else {
                return 0;
            }
        }
    }

    /**
     * @return the items
     */
    public List getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List items) {
        this.items = items;
    }

    private void querymapping(String datasource, HashMap<String, String> newitems, String key, String value, String sql) {
        Connection con = null;
        try {
            con = ConnectionFactory.getConnect(datasource, ActionField.class);
            ResultSet rs = JdbcPerformer.executeQuery(con, sql);
            ResultSetMetaData mdata = rs.getMetaData();
            int count = mdata.getColumnCount();
            while (rs.next()) {
                String newkey = key;
                String newvalue = value;
                for (int ii = 1; ii <= count; ii++) {
                    String v = rs.getString(ii);
                    String name = mdata.getColumnName(ii);
                    newkey = Replace.replace(newkey, name, v);
                    newvalue = Replace.replace(newvalue, name, v);
                }
                newitems.put(newkey, newvalue);
            }
        } catch (Exception e) {
            LogFactory.warn("初始化字段值对照表错误,语句:" + sql + "," + e.getMessage(), ActionField.class);
        }
        JdbcUtils.closeConnect(con);
    }

    /**
     * 返回组合框的mapping 如果要替换页面记录的显示数据foredit=false, 如果要显示编辑时的下拉表foredit=true
     *
     * @return the mapping
     */
    public HashMap<String, String> getMapping(String datasource, boolean foredit) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        HashMap<String, String> newitems = new HashMap<String, String>();
        Iterator i = items.iterator();
        while (i.hasNext()) {
            Element ee = (Element) i.next();
            String key = ee.getAttributeValue("key");
            String value = ee.getAttributeValue("value");
            String sql = ee.getAttributeValue("sql");
            if (key == null || key.trim().equals("") || value == null || value.trim().equals("")) {
                continue;
            }
            if (key.trim().equalsIgnoreCase("${empty}")) {
                newitems.put("", value);
            }
            if (sql != null && (!sql.equals(""))) {
                querymapping(datasource, newitems, key, value, sql);
            } else {
                if (foredit) {
                    value = Replace.replace(value);//替换""
                } else {
                    value = Replace.replaceIcon(value);//替换图标
                }
                newitems.put(key, value);
            }
        }
        if (foredit) {
            newitems.remove("");//不能有空健
        }
        return newitems;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        if (defaultValue != null) {
            this.defaultValue = defaultValue;
        }
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    public void setFieldValue(String fieldValue) {
        if (fieldValue == null) {
            this.fieldValue = "";
        } else {
            this.fieldValue = fieldValue.trim();
        }
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue2() {
        return fieldValue2;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    public void setFieldValue2(String fieldValue) {
        if (fieldValue == null) {
            this.fieldValue2 = "";
        } else {
            this.fieldValue2 = fieldValue.trim();
        }
    }

    /**
     * @return the datefmt
     */
    public String getDatefmt() {
        return datefmt;
    }

    /**
     * @param datefmt the datefmt to set
     */
    public void setDatefmt(String datefmt) {
        this.datefmt = datefmt;
    }
}
