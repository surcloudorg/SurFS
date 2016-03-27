/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.util.FileOperation;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * <p>Title: Hibernate会话配置</p>
 *
 * <p>Description: Hibernate会话配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HibernateConfig {

    private static Properties properties = null;//公共属性
    private static String filename = null;//属性文件名

    /**
     * 获取hibernate通用属性文件名
     *
     * @return String
     */
    public static String getConfigFileName() {
        if (properties == null) {
            configure();
        }
        return filename;
    }

    /**
     * 读取属性，每次重建资源池，就会查找一次该文件
     */
    public static synchronized void configure() {
        String classFileName = FileOperation.searchFile("hibernate.properties");
        if (classFileName != null) {
            try {
                properties = new Properties();
                properties.load(new FileInputStream(classFileName));
                filename = classFileName;
                LogFactory.info("读取配置：" + classFileName, HibernateConfig.class);
            } catch (Exception e) {
                LogFactory.error("读取配置：" + classFileName + "错误," + e.getMessage(), HibernateConfig.class);
                properties = null;
            }
        }
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(HibernateConfig.class.getResourceAsStream("hibernate.properties"));
                LogFactory.warn("Hibernate使用默认配置：" + HibernateConfig.class.getResource("hibernate.properties").getPath(), HibernateConfig.class);
            } catch (Exception r) {//不会发生
                LogFactory.trace("Hibernate读取默认配置失败！", r, HibernateConfig.class);
            }
        }
    }
    private String jdbc = ""; //数据库连接池名
    private Document document = null; //配置文档

    /**
     * 生成通用配置文档
     *
     * @param jdbc
     * @return String
     */
    private String makeCommonDocument() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        sb.append("<hibernate-configuration>\r\n");
        sb.append("<session-factory>\r\n");
        //sb.append("<property name=\"hibernate.dialect\">org.hibernate.dialect.MySQLDialect</property>\r\n");
        sb.append("<property name=\"connection.datasource\">");
        sb.append(jdbc);
        sb.append("</property>\r\n");
        if (properties == null || properties.isEmpty()) {
            sb.append("<property name=\"hibernate.show_sql\">true</property>\r\n");
            sb.append("<property name=\"hibernate.format_sql\">true</property>\r\n");
            sb.append("<property name=\"hibernate.use_sql_comments\">true</property>\r\n");
        } else {
            Enumeration e = properties.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if (!key.equalsIgnoreCase("connection.datasource")) {
                    String value = properties.getProperty(key);
                    sb.append("<property name=\"");
                    sb.append(key);
                    sb.append("\">");
                    sb.append(value);
                    sb.append("</property>\r\n");
                }
            }
        }
        sb.append("</session-factory>\r\n");
        sb.append("</hibernate-configuration>");
        return sb.toString();
    }

    /**
     * 创建配置文档
     *
     * @param jdbc String 数据库连接池名
     * @throws Exception
     */
    public HibernateConfig(String jdbc) throws ParserConfigurationException, SAXException, IOException {
        configure();
        this.jdbc = jdbc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        String doc=makeCommonDocument();
        document = db.parse(new InputSource(new StringReader(doc)));//解析文件
    }

    /**
     * 获取配置文档
     *
     * @return Document
     */
    public Document getDocument() {
        return document;
    }
 

    /**
     * 获取数据源名
     *
     * @return String
     */
    public String getDataSource() {
        return jdbc;
    }
}
