package com.autumn.core.autopage;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * <p>Title: AUTOPAGE配置</p>
 *
 * <p>Description: 解析autopage的xml脚本</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ActionConfig {

    public static final String ID_KEY_NAME = "id_key_name";//存储选中id，页面出现选中
    private String datasource = null;//数据源
    private String table = null;//表名
    private String primarykey = null;//主健
    private HashMap<String, Field> fields = new HashMap<String, Field>();//存储表的原始属性，由JDBC获取
    private HashMap<String, ActionField> actionFields = new HashMap<String, ActionField>();//存储字段附加属性，脚本中设置
    private Query queryConfig = null;
    private Edit insertConfig = null;
    private Edit updateConfig = null;
    private Edit deleteConfig = null;
    public boolean isSingleTable = true;

    public ActionConfig(String xml) throws Exception {
        parse(xml);
    }

    /**
     * 解析
     *
     * @throws Exception
     */
    private void parse(String sourceXml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(sourceXml));
        Element root = doc.getRootElement();
        datasource = root.getAttributeValue("datasource");
        table = root.getAttributeValue("table");
        primarykey = root.getAttributeValue("primarykey");
        if (datasource == null || datasource.trim().isEmpty()) {
            throw new Exception("没有设置数据源");
        } else {
            datasource = datasource.trim();
        }
        if (table == null || table.trim().equals("")) {
            throw new Exception("没有设置要访问的表");
        } else {
            table = table.trim();
        }
        if (primarykey == null || primarykey.trim().equals("")) {
            throw new Exception("没有设置表的主健");
        } else {
            primarykey = primarykey.trim().toLowerCase();
        }
        //在这里查询表,获取表字段属性
        TableInfo tinfo = new TableInfo(getDatasource(), getTable(), getPrimarykey());
        fields = tinfo.getFieldinfos();
        parseField(root.getChildren("field"));

        Element query = root.getChild("query");
        if (query == null) {
            throw new Exception("没有设置查询项");
        } else {
            queryConfig = new Query(query, this);
        }

        Element insert = root.getChild("insert");
        if (insert != null) {
            insertConfig = new Edit(insert, this);
        }

        Element update = root.getChild("update");
        if (update != null) {
            updateConfig = new Edit(update, this);
        }

        Element delete = root.getChild("delete");
        if (delete != null) {
            deleteConfig = new Edit(delete, this);
        }
    }

    /**
     * 解析字段属性
     *
     * @param dataList
     * @throws Exception
     */
    private void parseField(List dataList) throws Exception {
        if (dataList != null) {
            Iterator i = dataList.iterator();
            while (i.hasNext()) {
                Element ee = (Element) i.next();
                String name = ee.getAttributeValue("name");
                String comment = ee.getAttributeValue("comment");
                String fullname = ee.getAttributeValue("fullname");
                String datefmt = ee.getAttributeValue("datefmt");
                if (name == null || name.trim().isEmpty()) {
                    throw new Exception("字段名name没有设置");
                } else {
                    name = name.trim().toLowerCase();
                }
                comment = comment == null || comment.trim().isEmpty() ? name : comment.trim();
                fullname = fullname == null || fullname.trim().isEmpty() ? getTable() + "." + name : fullname.trim();
                datefmt = datefmt == null || datefmt.trim().isEmpty() ? "yyyy-MM-dd" : datefmt.trim();
                Field field = getFields().get(name);
                if (field == null) {
                    field = Field.queryFieldInfo(datasource, fullname, name);
                    if (field == null) {
                        throw new Exception("字段名" + name + "设置错误,无法从表中找到此字段");
                    } else {
                        if (fullname.toLowerCase().startsWith(table.toLowerCase() + ".")) {
                            isSingleTable = false;
                        }
                    }
                }
                ActionField af = new ActionField(field);
                af.setComment(comment);
                af.setFullName(fullname);
                af.setDatefmt(datefmt);
                af.setItems(ee.getChildren("item"));
                getActionFields().put(name, af);
            }
        }
        for (Field f : fields.values()) {//添加默认的ActionField
            if (!actionFields.containsKey(f.getName())) {
                ActionField af = new ActionField(f);
                af.setComment(f.getName());
                af.setFullName(table + "." + f.getName());
                getActionFields().put(f.getName(), af);
            }
        }
    }

    /**
     * @return the datasource
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @return the primarykey
     */
    public String getPrimarykey() {
        return primarykey;
    }

    /**
     * @return the fields
     */
    public HashMap<String, Field> getFields() {
        return fields;
    }

    /**
     * @return the actionFields
     */
    public HashMap<String, ActionField> getActionFields() {
        return actionFields;
    }

    /**
     * @return the queryConfig
     */
    public Query getQueryConfig() {
        return queryConfig;
    }

    /**
     * @return the insertConfig
     */
    public Edit getInsertConfig() {
        return insertConfig;
    }

    /**
     * @return the updateConfig
     */
    public Edit getUpdateConfig() {
        return updateConfig;
    }

    /**
     * @return the deletesql
     */
    public Edit getDeleteConfig() {
        return deleteConfig;
    }
}
