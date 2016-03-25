package com.autumn.core.sql;

import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * <p>Title: Hibernate映射配置</p>
 *
 * <p>Description: Hibernate映射配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HibernateMapping {

    private String classname = ""; //类名
    private String tablename = ""; //表名
    private String catalog = ""; //数据库目录
    private String xml = ""; //映射文档

    public HibernateMapping(String xml, String classname, String tablename, String catalog) {
        this.xml = xml;
        this.classname = classname;
        this.tablename = tablename;
        this.catalog = catalog;
    }

    public HibernateMapping(String xml) throws Exception {
        this.xml = xml;
        parser(removeComment(xml));
    }

    /**
     * 删除xml中的<!DOCTYPE>,提高xml解析的速度
     *
     * @param s
     * @return String
     */
    private String removeComment(String s) {
        int startindex = s.indexOf("<!");
        if (startindex <= 0) {
            return s;
        }
        String head = s.substring(0, startindex);
        int endindex = s.indexOf(">", startindex);
        if (endindex <= 0) {
            return s;
        }
        String body = s.substring(endindex + 1);
        return head + body;
    }

    /**
     * 解析
     *
     * @param xmlInput InputStream
     * @throws Exception
     */
    private void parser(String xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xml));
        Element root = doc.getRootElement();
        String name = root.getName();
        if (!"hibernate-mapping".equalsIgnoreCase(name)) {
            throw new Exception("格式错误!");
        }
        String packagename = root.getAttributeValue("package");
        if (packagename == null) {
            packagename = "";
        }
        String database = root.getAttributeValue("catalog");
        if (database == null) {
            database = "";
        }
        List dataList = root.getChildren();
        if (dataList == null || dataList.isEmpty()) {
            throw new Exception("没有映射记录!");
        }
        Element ee = (Element) dataList.get(0);
        String str = ee.getName();
        if (!"class".equalsIgnoreCase(str)) {
            throw new Exception("格式错误!");
        }
        classname = ee.getAttributeValue("name");
        if (getClassname() == null) {
            classname = ee.getAttributeValue("entity-name");
        }
        if (getClassname() == null) {
            throw new Exception("没有指定类名!");
        }
        if (!packagename.equals("")) {
            classname = packagename + "." + getClassname();
        }
        tablename = ee.getAttributeValue("table");
        if (getTablename() == null) {
            int index = getClassname().lastIndexOf(".");
            if (index > 0) {
                tablename = getClassname().substring(index + 1);
            } else {
                tablename = getClassname();
            }
        }
        catalog = ee.getAttributeValue("catalog");
        if (getCatalog() == null) {
            catalog = database;
        }
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * @return the catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }
}
