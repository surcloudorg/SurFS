package com.autumn.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class login_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      			null, false, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n\r\n\r\n\r\n<html>\r\n<head>\r\n"+Constants.CHARSET_UTF8+"<title>autumn-地球最好的java开发框架</title>\r\n\r\n<style type=\"text/css\">\r\n<!--\r\n.errtext {color: #FF0000}\r\n-->\r\n</style>\r\n</head>\r\n<body>\r\n<style>\r\n<!--\r\na:link       { font-size: 9pt; color: #000000;text-decoration: none; }\r\na:visited    { color: #000000; font-size: 9pt;text-decoration: none; }\r\na:hover      { font-size: 9pt; color: #000000;TEXT-DECORATION: underline; }\r\nbody         { color: #000000; font-size: 9pt }\r\np            { color: #000000; font-size: 9pt }\r\ntd           { font-size: 9pt; color: #000000 }\r\ninput        { font-size: 9pt; color: #000000 }\r\n-->\r\n</style>\r\n<div style=\"position: absolute; width: 16px; height: 13px; z-index: 3; left: 738px; top: 217px\" id=\"layer6\">\r\n\t<a title=\"关闭窗口\" style=\"text-decoration: none; font-family: AdobeSm; color: #000000\" href=\"javascript:window.close()\">\r\n\t<span style=\"BACKGROUND-COLOR: #ffffff\">×</span></a></div>\r\n<script language=javascript>\r\n<!--\r\nfunction CheckForm(){\r\n\tif(document.form.username.value==\"\"){\r\n\t\tdocument.getElementById(\"errorInfo\").innerHTML=\"请输入用户名！\";\r\n");
      out.write("\t\tdocument.form.username.focus();\r\n\t\treturn false;\r\n\t}\r\n\tif(document.form.password.value == \"\"){\r\n\t\tdocument.getElementById(\"errorInfo\").innerHTML=\"请输入密码！\";\r\n\t\tdocument.form.password.focus();\r\n\t\treturn false;\r\n\t}\r\n}\r\n//-->\r\n</script>\r\n<div style=\"position: absolute; width: 140px; height: 15px; z-index: 1; left: 301px; top: 251px\" id=\"layer1\">\r\n\t<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\r\n  <form name=\"form\" onSubmit=\"return CheckForm()\" method=\"post\" action=\"login.do\">\r\n\t\t<tr>\r\n\t\t\t<td><img border=\"0\" src=\"img/icon.png\"></td>\r\n\t\t</tr>\r\n\t</table>\r\n</div>\r\n<div style=\"position: absolute; width: 493px; height: 204px; z-index: 2; left: 272px; top: 203px\" id=\"layer2\">\r\n\t<table border=\"0\" width=\"493\" cellspacing=\"0\" cellpadding=\"0\" height=\"194\">\r\n\t\t<tr>\r\n\t\t\t<td width=\"2\" height=\"8\" background=\"img/table_bg_t.gif\">\r\n\t\t\t<img border=\"0\" src=\"img/table_corner_t_l.gif\" width=\"8\" height=\"8\"></td>\r\n\t\t  <td height=\"8\" width=\"477\" background=\"img/table_bg_t.gif\"></td>\r\n\t\t\t<td height=\"8\" width=\"8\">\r\n\t\t\t<img border=\"0\" src=\"img/table_corner_t_r.gif\" width=\"8\" height=\"8\"></td>\r\n");
      out.write("\t\t</tr>\r\n\t\t<tr>\r\n\t\t\t<td width=\"8\" height=\"186\" background=\"img/table_bg_l.gif\">　</td>\r\n\t\t  <td height=\"186\" width=\"477\">\r\n\t\t\t<div style=\"position: absolute; width: 100px; height: 100px; z-index: 1; left: 12px; top: 15px\" id=\"layer3\">\r\n\t\t\t\t<img border=\"0\" src=\"img/title_fast_deposit_channel.gif\" width=\"200\" height=\"30\"></div>\r\n\t\t\t<div style=\"position: absolute; width: 288px; height: 120px; z-index: 2; left: 214px; top: 52px\" id=\"layer4\">\r\n\t\t\t  <table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" height=\"125\">\r\n\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t<td valign=\"top\">\r\n\t\t\t\t\t\t<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" height=\"122\">\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t  <td colspan=\"2\"><span class=\"errtext\"><span id=\"errorInfo\" style=\"font-size: 9pt\">");
      if (_jspx_meth_c_out_0(_jspx_page_context))
        return;
      out.write("</span></span></td>\r\n\t\t\t\t\t\t  </tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td width=\"21%\"><font color=\"#006699\"><span style=\"font-size: 9pt\">\r\n\t\t\t\t\t\t\t\t登录帐号：</span></font></td>\r\n\t\t\t\t\t\t\t\t<td width=\"79%\">\r\n\t\t\t\t\t\t\t\t&nbsp;<input type=\"text\" name=\"username\" maxlength=\"20\" style=\"border: 1px solid #006699;width: 150;\" value=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${param.username}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("\"> </td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td width=\"21%\"><font color=\"#006699\"><span style=\"font-size: 9pt\">\r\n\t\t\t\t\t\t\t\t登录密码：</span></font></td>\r\n\t\t\t\t\t\t\t\t<td width=\"79%\">\r\n\t\t\t\t\t\t\t\t&nbsp;<input type=\"password\" name=\"password\" maxlength=\"20\" style=\"border: 1px solid #006699;width: 150;\"> </td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t\t\r\n\t\t\t\t\t\t\t\r\n\t\t\t\t\t\t\t<tr>\r\n                                <td width=\"21%\"></td>\r\n\t\t\t\t\t\t\t\t<td width=\"79%\">\r\n\t\t\t\t\t\t \t\t&nbsp;<input type=\"submit\" value=\"确定\" name=\"B1\" style=\"border: 1px solid #006699; background-color: #FFFFFF\">&nbsp;&nbsp;\r\n\t\t\t\t\t\t\t\t<input type=\"reset\" value=\"重置\" name=\"B2\" style=\"border: 1px solid #006699; background-color: #FFFFFF\">\r\n\t\t\t\t\t\t\t  </td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</table>\r\n\t\t\t\t\t\t</td>\r\n\t\t\t\t\t</tr>\r\n\t\t\t  </table>\r\n\t\t\t</div>\r\n\t\t\t<p></td>\r\n\t\t\t<td height=\"186\" width=\"8\" background=\"img/table_bg_r.gif\">\r\n\t\t\t<p align=\"right\"></td>\r\n\t\t</tr>\r\n\t\t<tr>\r\n\t\t\t<td width=\"2\" height=\"1\" background=\"img/table_bg_b.gif\">\r\n\t\t\t<img border=\"0\" src=\"img/table_corner_b_l.gif\" width=\"8\" height=\"8\"></td>\r\n\t\t  <td height=\"1\" width=\"477\" background=\"img/table_bg_b.gif\"></td>\r\n");
      out.write("\t\t\t<td height=\"1\" width=\"8\" background=\"img/table_corner_b_r.gif\"></td>\r\n\t\t</tr>\r\n\t</table>\r\n</div>\r\n<div align=\"center\"><span style=\"position: absolute; width: 360px; height: 14px; z-index: 1; left: 335px; top: 407px\"><font color=\"#B5B5B5\"><span style=\"font-size: 8pt\">JAVA工程管理框架1.0版 QQ:109825486 刘社朋 2012年5月</span></font></span></div>\r\n</body>\r\n</form>\r\n</html>");
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
    _jspx_th_c_out_0.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${error}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_0 = _jspx_th_c_out_0.doStartTag();
    if (_jspx_th_c_out_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
    return false;
  }
}
