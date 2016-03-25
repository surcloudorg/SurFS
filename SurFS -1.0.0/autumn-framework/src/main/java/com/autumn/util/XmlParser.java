package com.autumn.util;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>Title: 解析xml</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class XmlParser {

    /**
     * String xml-->Document
     *
     * @param xml
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource insource = new InputSource(new StringReader(xml));
        return db.parse(insource);
    }

    /**
     * InputStream-->Document
     *
     * @param is
     * @return Document
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static Document buildDocument(InputStream is) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(is);
    }

    /**
     * 存储Document
     *
     * @param doc
     * @param os
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public synchronized static void save(Document doc, OutputStream os) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(os));
    }

    /**
     * 节点转换为xmlString
     *
     * @param node
     * @return String
     */
    public static String node2String(Node node) {
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException ex) {
        }
        return null;
    }

    /**
     * 所有指定标签的子节点
     *
     * @param str
     * @param elem
     * @return List<Node>
     */
    public static List<Node> getNodeByTagName(String str, Element elem) {
        List<Node> list = new ArrayList<Node>();
        NodeList nodes = elem.getChildNodes();
        for (int ii = 0, count = nodes.getLength(); ii < count; ii++) {
            Node node = nodes.item(ii);
            if (node.getNodeName().equalsIgnoreCase(str)) {
                list.add(node);
            }
        }
        return list;
    }

    /**
     * 子节点值
     *
     * @param str
     * @param elem
     * @return Document
     */
    public static String getChildText(String str, Node elem) {
        return getText(getChildNode(str, elem));
    }

    /**
     * 节点属性值
     *
     * @param str
     * @param elem
     * @return Document
     */
    public static String getAttributeText(String str, Node elem) {
        NamedNodeMap mp = elem.getAttributes();
        if (mp != null) {
            Node node = mp.getNamedItem(str);
            if (null != node) {
                String nodeValue = node.getNodeValue();
                return null != nodeValue ? nodeValue.trim() : "";
            }
        }
        return "";
    }

    /**
     * 节点值
     *
     * @param elem
     * @return Document
     */
    public static String getText(Node elem) {
        if (elem != null) {
            Node node = elem.getFirstChild();
            if (null != node) {
                String nodeValue = node.getNodeValue();
                return null != nodeValue ? nodeValue.trim() : "";
            }
        }
        return "";
    }

    /**
     * @return int 子节点值
     */
    public static long getChildLong(String str, Node elem, long defaut) {
        return getLong(getChildNode(str, elem), defaut);
    }

    /**
     * @return long 节点属性值
     */
    public static long getAttributeLong(String str, Node elem, long defaut) {
        String str2 = getAttributeText(str, elem);
        if (null == str2 || (str2 = str2.trim()).isEmpty() || str2.equalsIgnoreCase("null")) {
            return defaut;
        }
        try {
            return Long.parseLong(str2.trim());
        } catch (Exception e) {
            return defaut;
        }
    }

    /**
     * @return long 节点值
     */
    public static long getLong(Node elem, long defaut) {
        String str2 = getText(elem);
        if (null == str2 || (str2 = str2.trim()).isEmpty() || str2.equalsIgnoreCase("null")) {
            return defaut;
        }
        try {
            return Long.parseLong(str2.trim());
        } catch (Exception e) {
            return defaut;
        }
    }

    /**
     * 子节点值
     *
     * @param str
     * @param elem
     * @return Date
     * @throws ParseException
     */
    public static Date getChildDate(String str, Node elem) throws ParseException {
        return getDate(getChildNode(str, elem));
    }

    /**
     * 节点属性值
     *
     * @param str
     * @param elem
     * @return Date
     * @throws ParseException
     */
    public static Date getAttributeDate(String str, Node elem) throws ParseException {
        String str2 = getAttributeText(str, elem);
        if (null == str2 || (str2 = str2.trim()).isEmpty() || str2.equalsIgnoreCase("null")) {
            return null;
        }
        return TextUtils.String2Date(str2);
    }

    /**
     * 节点值
     *
     * @param elem
     * @return Date
     * @throws ParseException
     */
    public static Date getDate(Node elem) throws ParseException {
        String str2 = getText(elem);
        if (null == str2 || (str2 = str2.trim()).isEmpty() || str2.equalsIgnoreCase("null")) {
            return null;
        }
        return TextUtils.String2Date(str2);
    }

    /**
     * 子节点
     *
     * @param str
     * @param elem
     * @return Node
     */
    public static Node getChildNode(String str, Node elem) {
        NodeList nodelist = ((Element) elem).getElementsByTagName(str);
        if (nodelist.getLength() > 0) {
            Node node = nodelist.item(0);
            return node;
        } else {
            return null;
        }
    }

    /**
     * 子节点xml
     *
     * @param str
     * @param elem
     * @return String
     */
    public static String getChildXml(String str, Node elem) {
        return getXml(getChildNode(str, elem));
    }

    /**
     * 节点xml
     *
     * @param elem
     * @return String
     */
    public static String getXml(Node elem) {
        if (elem != null) {
            if (elem.getChildNodes().getLength() > 0) {
                String nodeValue = node2String(elem);
                return null != nodeValue ? nodeValue : "";
            }
        }
        return "";
    }
}
