package com.autumn.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class error_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_out_value_nobody;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_c_out_value_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_c_out_value_nobody.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=utf8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n\r\n\r\n\r\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n<link rel=\"SHORTCUT ICON\" href=\"favicon.ico\" type=\"image/x-icon\">\r\n<title>错误信息</title>\r\n\r\n<style type=\"text/css\">\r\n<!--\r\n.errtext {color: #FF0000}\r\n-->\r\n</style>\r\n<style>\r\n<!--\r\na:link       { font-size: 9pt; font-family: 宋体; color: #000000;text-decoration: none; }\r\na:visited    { color: #000000; font-family: 宋体; font-size: 9pt;text-decoration: none; }\r\na:hover      { font-size: 9pt; font-family: 宋体; color: #000000;TEXT-DECORATION: underline; }\r\nbody         { font-family: 宋体; color: #000000; font-size: 9pt }\r\np            { color: #000000; font-family: 宋体; font-size: 9pt }\r\ntd           { font-size: 9pt; font-family: 宋体; color: #000000 }\r\ninput        { font-size: 9pt; font-family: 宋体; color: #000000 }\r\n-->\r\n</style>\r\n</head>\r\n<body>\r\n<table width=\"95%\" border=\"0\" align=\"center\">\r\n  <tr>\r\n    <td height=\"40\">&nbsp;</td>\r\n    <td>&nbsp;</td>\r\n");
      out.write("    <td>&nbsp;</td>\r\n  </tr>\r\n  <tr>\r\n    <td height=\"297\" colspan=\"3\" valign=\"top\">&nbsp;\r\n    <div  class=\"errtext\">");
      if (_jspx_meth_c_out_0(_jspx_page_context))
        return;
      out.write("</div>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_c_out_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_0 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_0.setPageContext(_jspx_page_context);
    _jspx_th_c_out_0.setParent(null);
    _jspx_th_c_out_0.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${ERROR_MSG}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_0 = _jspx_th_c_out_0.doStartTag();
    if (_jspx_th_c_out_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
    return false;
  }
}
