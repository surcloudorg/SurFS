/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.jsp.console;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class system_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_if_test;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_c_out_value_nobody;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_c_if_test = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_c_out_value_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_c_if_test.release();
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

      out.write("\r\n\r\n\r\n\r\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n<title>重载类</title>\r\n</head>\r\n<LINK href=\"../img/pub/body.css\" type=text/css rel=stylesheet>\r\n<script type=\"text/javascript\" src=\"../img/pub/sys.js\"></script>\r\n<body>\r\n<FORM id=Form1 name=Form1 action=system.do method=post>\r\n<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"0\">\r\n  <tbody>\r\n    <tr>\r\n      <td\r\n            height=\"25\" valign=\"bottom\" background=\"../img/pub/content_top_bg.jpg\">&nbsp;&nbsp;<strong><img\r\n                  height=\"14\" src=\"../img/pub/icoblue.gif\" width=\"14\"\r\n                  align=\"absmiddle\" /> 系统 - 重载类</strong></td>\r\n    </tr>\r\n    <tr>\r\n      <td valign=\"top\" height=\"500\">\r\n\t  <br/>\r\n\t  <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" bgcolor=\"#eeeeee\" border=\"0\">\r\n        <tbody>\r\n          <tr>\r\n            <td height=\"23\" colspan=\"2\" align=\"left\" valign=\"middle\" nowrap=\"nowrap\" background=\"../img/pub/top1.gif\" bgcolor=\"#cccccc\">\r\n");
      out.write("            &nbsp;&nbsp;<span class=\"JiaCu\">重载类</span>\r\n            &nbsp;&nbsp;<a href=\"sysproperties.do\">查看系统变量</a>            \r\n            &nbsp;&nbsp;<a href=\"systhreads.do\">激活线程</a>            \r\n            &nbsp;&nbsp;<a href=\"sysmemory.do\">系统信息</a> \r\n            </td>\r\n          </tr>\r\n          <tr>\r\n            <td height=\"30\" colspan=\"2\" align=\"left\" valign=\"middle\">\r\n            \r\n            <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"15\" bgcolor=\"#EEEEEE\" bordercolor=\"#EEEEEE\">\r\n                <tbody>\r\n                  <tr>\r\n                    <td class=\"msg\" width=\"100%\" height=\"50\" valign=\"top\" bordercolor=\"#999999\" bgcolor=\"#FFFFFF\"><br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重载类的作用是让系统加载你最新上传的class类文件，如果你没有做任何更新，请不要重载，只有在必要的时候执行重载，重载意味着系统需要把classpath下的<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;所有类都要重新加载一次（当然是在用到时侯开始加载），注意系统不会单独对一个类文件实现重载。<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;执行重载命令后，运行于框架中的服务，只有重新启动才会使更新生效，目前框架中仅页面控制器(com.autumn.core.web.Action)支持重载后更新立刻生效！<br />\r\n");
      out.write("&nbsp;&nbsp;&nbsp;&nbsp;当然可以对服务进行二次封装来实现重载后更新的代码立刻生效，所以对于重载后必须重启的服务需要在备注里加入说明，一般服务运行时显示的图标可以区别当前服<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;务运行的是否是一个过期版本。<br />\r\n<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"../img/pub/run.gif\" width=\"22\" height=\"22\" align=\"absmiddle\"/>表示服务运行的是最新版本<br />\r\n<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"../img/pub/runwithwarn.gif\" width=\"22\" height=\"22\" align=\"absmiddle\"/>表示服务运行的可能是过期版本<br />\r\n<br/> \r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;需要注意的是，如果两个服务之间需要进行数据交互，可能需要保证两个服务处于同一个类加载器中，一般做法是执行重载后重启这两个服务，更好的做法是对需<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;要交互的数据模型进行二次封装。<br/>\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关于类加载的相关问题，请阅读开发规范！\r\n<br/>\r\n<br/> \t \t\t\t\t                   </td>\r\n                  </tr>\r\n                </tbody>\r\n            </table>\r\n            \r\n            </td>\r\n          </tr>\r\n          <tr>\r\n            <td width=\"33%\" height=\"23\" align=\"left\" valign=\"middle\" nowrap=\"nowrap\" background=\"../img/pub/top1.gif\">&nbsp;&nbsp;\r\n                <input name=\"isread\" type=\"checkbox\" id=\"isread\" value=\"checkbox\"/><a href=\"#\" onclick=\"document.Form1.isread.checked=!document.Form1.isread.checked\">我已经了解重载操作的注意事项</a></td>\r\n");
      out.write("            <td width=\"67%\" align=\"left\" valign=\"middle\" nowrap=\"nowrap\" background=\"../img/pub/top1.gif\">");
      if (_jspx_meth_c_if_0(_jspx_page_context))
        return;
      out.write("\r\n              &nbsp;&nbsp;<span class=\"redtitle\" id=\"loadcalssmsg\">");
      if (_jspx_meth_c_out_0(_jspx_page_context))
        return;
      out.write("</span></td>\r\n          </tr>\r\n        </tbody>\r\n      </table>\r\n\t  </td>\r\n    </tr>\r\n  </tbody>\r\n</table>\r\n</FORM>\r\n</body>\r\n</html>");
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

  private boolean _jspx_meth_c_if_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_if_0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _jspx_tagPool_c_if_test.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_if_0.setPageContext(_jspx_page_context);
    _jspx_th_c_if_0.setParent(null);
    _jspx_th_c_if_0.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smsystem.accessPermission>1}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_if_0 = _jspx_th_c_if_0.doStartTag();
    if (_jspx_eval_c_if_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("<input name=\"loadclass\" type=\"submit\" class=\"bottonbox\" id=\"loadclass\" value=\"重载\" onclick=\"return(checkloadclass());\" />");
        int evalDoAfterBody = _jspx_th_c_if_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_if_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
      return true;
    }
    _jspx_tagPool_c_if_test.reuse(_jspx_th_c_if_0);
    return false;
  }

  private boolean _jspx_meth_c_out_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_0 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_0.setPageContext(_jspx_page_context);
    _jspx_th_c_out_0.setParent(null);
    _jspx_th_c_out_0.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smsystem.loadclassmsg}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_0 = _jspx_th_c_out_0.doStartTag();
    if (_jspx_th_c_out_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
    return false;
  }
}
