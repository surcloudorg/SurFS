package com.autumn.jsp.console;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.codehaus.xfire.*;
import com.autumn.core.soap.*;

public final class smservices_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  public Object getDependants() {
    return _jspx_dependants;
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

      out.write("\r\n\r\n\r\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n\r\n<title>查看Soap服务</title>\r\n</head>\r\n<LINK href=\"../img/pub/body.css\" type=text/css rel=stylesheet>\r\n<body>\r\n<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"0\">\r\n  <tbody>\r\n    <tr>\r\n      <td height=\"25\" valign=\"bottom\" background=\"../img/pub/content_top_bg.jpg\">&nbsp;&nbsp;<span class=\"JiaCu\"><img height=\"14\" \r\n                  src=\"../img/pub/icoblue.gif\" width=\"14\" \r\n                  align=\"absmiddle\" /><strong> 查看Soap服务</strong></span></td>\r\n    </tr>\r\n    <tr>\r\n      <td valign=\"top\" height=\"500\"><br />\r\n          <table style=\"word-wrap: break-word; word-break: break-all;\" id=\"Table1\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" \r\n            align=\"center\"  border=\"0\">\r\n            <tbody>        \r\n              <tr>\r\n                <td bgcolor=\"#999999\" colspan=\"2\" height=\"1\"></td>\r\n              </tr>\r\n              <tr>\r\n");
      out.write("                <td  height=\"86\" colspan=\"2\" align=\"center\">\r\n                ");
 
				XFire xfire =XFireFactory.newInstance().getXFire();
				if(xfire.getServiceRegistry().getServices().isEmpty()){	
      out.write("\t\t\r\n                <span class=\"redtitle\">没有注册服务</span>\r\n                ");
}else{
      out.write("\r\n<iframe width=\"100%\" height=\"500\" frameborder=\"0\" name=\"letter_body\" id=\"content_frame\" src=\"../services/\">\r\n</iframe>\t\t\r\n");
}
      out.write("\r\n\t\t\t\t</td>\r\n              </tr>\r\n              <tr>\r\n                <td width=\"8%\"  height=\"23\" align=\"left\" background=\"../img/pub/top1.gif\"></td>\r\n                <td width=\"92%\"  height=\"23\" align=\"left\" background=\"../img/pub/top1.gif\"><a href=\"soaps.do\">返回</a></td>\r\n              </tr>\r\n            </tbody>\r\n        </table></td>\r\n    </tr>\r\n  </tbody>\r\n</table>\r\n</body>\r\n</html>");
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
}
