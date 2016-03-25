package com.autumn.core.web;

import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: xml输出器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class XmlForward implements Forward {

    /**
     * 输出从 request.Attributes生成的xml文档
     *
     * @param root
     * @return XmlForward
     */
    public static XmlForward responseXml(String root) {
        HttpServletRequest request = ActionContext.getActionContext().getRequest();
        String xml = getXmlString(request, root);
        return new XmlForward(xml);
    }
    private String xml = null;

    /**
     * 输出xml
     *
     * @param xml
     */
    public XmlForward(String xml) {
        this.xml = xml;
    }

    @Override
    public void doForward(Action action) throws Throwable {
        HttpServletResponse response = action.getResponse();
        action.getResponse().setContentType("text/xml; charset=".concat(action.getRequest().getCharacterEncoding()));
        PrintWriter out = response.getWriter();
        out.write(xml);
        out.flush();
        out.close();
    }

    /**
     * 从 request.Attributes生成xml文档
     *
     * @param request
     */
    private static String getXmlString(HttpServletRequest request, String rootName) {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"");
        sb.append(request.getCharacterEncoding());
        sb.append("\"?>\r\n");
        sb.append("<");
        sb.append(rootName);
        sb.append(">\r\n");
        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            Object value = request.getAttribute(key);
            sb.append("<");
            sb.append(key);
            sb.append(">");
            sb.append(value == null ? "" : value.toString());
            sb.append("</");
            sb.append(key);
            sb.append(">\r\n");
        }
        sb.append("</");
        sb.append(rootName);
        sb.append(">");
        return sb.toString();
    }
}
