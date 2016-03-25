package com.autumn.core.autopage;

import com.autumn.core.sql.JdbcUtils;
import com.autumn.util.TextUtils;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import org.jdom.Element;

/**
 * <p>Title: AUTOPAGE-编辑配置</p>
 *
 * <p>Description: 解析autopage的xml脚本中insert,update,delete项</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Edit {

    private String type = "insert";//编辑类型
    private String caption = null;//按钮名
    private List<EditField> fields = new ArrayList<EditField>();//相关字段属性
    private String sql = null;//执行sql语句
    private ActionConfig config = null;

    /**
     * 解析
     *
     * @param insert
     * @param cfg
     * @throws Exception
     */
    public Edit(Element insert, ActionConfig cfg) throws Exception {
        this.config = cfg;
        this.type = insert.getName().trim();
        sql = insert.getAttributeValue("sql");
        if (sql != null) {
            sql = sql.trim();
        }
        caption = insert.getAttributeValue("caption");
        if (caption == null || caption.trim().equals("")) {
            if (type.equalsIgnoreCase("update")) {
                caption = "修改";
            } else if (type.equalsIgnoreCase("insert")) {
                caption = "添加";
            } else {
                caption = "删除";
            }
        } else {
            caption = caption.trim();
        }
        if (type.equalsIgnoreCase("update") || type.equalsIgnoreCase("insert")) {
            parseParam(insert.getChildren("param"));
        }
    }

    /**
     * 解析update,insert时设置的字段属性
     *
     * @param dataList
     * @throws Exception
     */
    private void parseParam(List dataList) throws Exception {
        HashMap<String, ActionField> actionFields = config.getActionFields();
        if (!(dataList == null || dataList.isEmpty())) {
            Iterator i = dataList.iterator();
            while (i.hasNext()) {
                Element ee = (Element) i.next();
                String name = ee.getAttributeValue("name");
                String hiden = ee.getAttributeValue("hiden");
                String readonly = ee.getAttributeValue("readonly");
                String defaultvalue = ee.getAttributeValue("default");
                if (name == null || name.trim().equals("")) {
                    throw new Exception("编辑参数name没有设置");
                } else {
                    name = name.trim().toLowerCase();
                }
                ActionField af = actionFields.get(name);
                if (af == null) {
                    throw new Exception("编辑字段" + name + "没有在字段属性表里设置");
                }
                EditField qf = new EditField(af);
                qf.setDefaultValue(defaultvalue);
                qf.setHiden(hiden);
                qf.setReadonly(readonly);
                if (qf.isIncrement()) {
                    if (!qf.isHiden()) {
                        if (!qf.isReadonly()) {
                            throw new Exception("字段" + name + "是自增序列,不能编辑");
                        }
                    }
                }
                addField(qf);
            }
        }
        if (sql != null && (!sql.equals(""))) {//编辑字段只能包含sql指定的
            String primarykey = config.getPrimarykey();
            if (type.equalsIgnoreCase("update")) {//没有primarykey抛出错误
                if (Replace.find(sql, "old." + primarykey) < 0) {//old.primarykey
                    throw new Exception("update语句必须以" + primarykey + "={old." + primarykey + "}作为更新条件");
                }
            }
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            List<EditField> oldfields = fields;
            fields = new ArrayList<EditField>();
            String newsql = sql;
            Replace.replaceSQL(newsql, map);
            Set<String> set = map.keySet();
            for (String name : set) {
                EditField efield = Edit.getField(name, oldfields);
                if (efield != null) {
                    addField(efield);
                    continue;
                }
                ActionField af = actionFields.get(name);
                if (af == null) {
                    throw new Exception("编辑字段" + name + "没有在字段属性表里设置");
                }
                EditField qf = new EditField(af);
                addField(qf);
            }
            if (type.equalsIgnoreCase("update")) {
                for (EditField ef : oldfields) {
                    EditField efield = getField(ef.getName());
                    if (efield == null) {
                        ef.setReadonly(true);
                        fields.add(ef);
                    }
                }
            }
        } else {
            if (fields.isEmpty()) {
                throw new Exception("在insert/update项需要至少设置一个编辑字段");
            } else {
                if (type.equalsIgnoreCase("update")) {//没有primarykey添加一个
                    String primarykey = config.getPrimarykey();
                    EditField efield = getField(primarykey);
                    if (efield == null) {//必需要有主健
                        ActionField af = actionFields.get(primarykey);
                        EditField ef = new EditField(af);
                        ef.setHiden(true);
                        addField(ef);
                    }
                }
            }
        }
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @return the fields
     */
    public List<EditField> getFields() {
        return fields;
    }

    public static EditField getField(String name, List<EditField> list) {
        for (EditField ef : list) {
            if (ef.getName().equalsIgnoreCase(name)) {
                return ef;
            }
        }
        return null;
    }

    private void addField(EditField ef) throws Exception {
        //自增量,不可编辑update,去除自增量insrt
        if (getField(ef.getName()) == null) {
            if (ef.isIncrement()) {
                if (type.equalsIgnoreCase("update")) {
                    ef.setReadonly(true);
                } else {
                    throw new Exception(ef.getName() + "是增量字段，不可编辑！");
                }
            }
            fields.add(ef);
        }
    }

    public EditField getField(String name) {
        for (EditField ef : fields) {
            if (ef.getName().equalsIgnoreCase(name)) {
                return ef;
            }
        }
        return null;
    }

    /**
     * 获取记录，存入hash
     *
     * @param rs
     * @return HashMap<String, String>
     * @throws Exception
     */
    public static HashMap<String, String> getResultSet(ResultSet rs) throws Exception {
        HashMap<String, String> msp = new HashMap<String, String>();
        ResultSetMetaData rmd = rs.getMetaData();
        int count = rmd.getColumnCount();
        for (int ii = 1; ii <= count; ii++) {
            String name = rmd.getColumnName(ii).toLowerCase();
            String value = JdbcUtils.getResultSetStringValue(rs, ii);
            msp.put(name, value == null ? " " : value);
        }
        return msp;
    }

    /**
     * 获取记录，存入EditField
     *
     * @param rs
     * @param updateFields
     * @throws Exception
     */
    public static void getResultSet(ResultSet rs, List<EditField> updateFields) throws Exception {
        ResultSetMetaData rmd = rs.getMetaData();
        int count = rmd.getColumnCount();
        for (int ii = 1; ii <= count; ii++) {
            EditField ef = updateFields.get(ii - 1);
            String value = JdbcUtils.getResultSetStringValue(rs, ii);
            ef.setFieldValue(value == null ? "" : value);
        }
    }

    /**
     * 从EditField中获取参数值,设置PreparedStatement的参数
     *
     * @param con
     * @param prest
     * @param ef
     * @param index
     * @throws Exception
     */
    public static void setStatementParam(Connection con, PreparedStatement prest, EditField ef, int index) throws Exception {
        String classname = ef.getClassname();
        String value = ef.getFieldValue();
        if ((value == null || value.equals("")) && ef.getNullable() == 1) {
            prest.setNull(index, ef.getTypeNum());
            return;
        }
        if (classname.equalsIgnoreCase("java.lang.Integer")) {
            int v = Integer.parseInt(value);
            prest.setInt(index, v);
        } else if (classname.equalsIgnoreCase("java.lang.Boolean")) {
            boolean b = TextUtils.parseBoolean(value);
            prest.setBoolean(index, b);
        } else if (classname.equalsIgnoreCase("java.math.BigInteger")) {
            BigDecimal b = new BigDecimal(value);
            prest.setBigDecimal(index, b);
        } else if (classname.equalsIgnoreCase("java.math.BigDecimal")) {
            BigDecimal b = new BigDecimal(value);
            prest.setBigDecimal(index, b);
        } else if (classname.equalsIgnoreCase("java.lang.Byte")) {
            byte b = Byte.parseByte(value);
            prest.setByte(index, b);
        } else if (classname.equalsIgnoreCase("java.lang.Double")) {
            Double d = Double.parseDouble(value);
            prest.setDouble(index, d);
        } else if (classname.equalsIgnoreCase("java.lang.Float")) {
            Float d = Float.parseFloat(value);
            prest.setFloat(index, d);
        } else if (classname.equalsIgnoreCase("java.lang.Long")) {
            Long d = Long.parseLong(value);
            prest.setLong(index, d);
        } else if (classname.equalsIgnoreCase("java.lang.Short")) {
            Short d = Short.parseShort(value);
            prest.setShort(index, d);
        } else if (classname.equalsIgnoreCase("java.sql.Timestamp")
                || classname.equalsIgnoreCase("oracle.sql.TIMESTAMP")) {
            String val = value;
            Date date;
            if (val.length() == 10) {
                date = TextUtils.String2Date(val, "yyyy-MM-dd");
            } else {
                date = TextUtils.String2Date(val, "yyyy-MM-dd HH:mm:ss");
            }
            Timestamp ts = new Timestamp(date.getTime());
            prest.setTimestamp(index, ts);
        } else {
            prest.setString(index, value);
        }
    }

    /**
     * @return the fields
     */
    public List<EditField> getFieldsCopy() {
        List<EditField> list = new ArrayList<EditField>();
        for (EditField q : fields) {
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
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
}
