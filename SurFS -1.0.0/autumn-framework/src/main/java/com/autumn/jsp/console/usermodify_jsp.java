package com.autumn.jsp.console;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class usermodify_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("\r\n\r\n\r\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n<title>修改登录资料</title>\r\n</head>\r\n<LINK href=\"../img/pub/body.css\" type=text/css rel=stylesheet>\r\n<script type=\"text/javascript\" src=\"../img/pub/sys.js\"></script>\r\n<body>\r\n<FORM id=Form1 name=Form1 action=modifyuser.do method=post>\r\n<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"0\">\r\n  <tbody>\r\n    <tr>\r\n      <td height=\"25\" valign=\"bottom\" background=\"../img/pub/content_top_bg.jpg\">&nbsp;&nbsp;<span class=\"JiaCu\"><img height=\"14\" \r\n                  src=\"../img/pub/icoblue.gif\" width=\"14\" \r\n                  align=\"absmiddle\" /> 用户管理 - 修改登录资料</span></td>\r\n    </tr>\r\n    <tr>\r\n      <td valign=\"top\" height=\"500\"><br />\r\n          <table id=\"Table1\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" \r\n            align=\"center\" bgcolor=\"#EFEFEF\" border=\"0\">\r\n            <tbody>\r\n\r\n              <tr>\r\n                <td bgcolor=\"#999999\" colspan=\"2\" height=\"1\"></td>\r\n");
      out.write("              </tr>\r\n              <tr>\r\n                <td width=\"22%\" height=\"30\" align=\"right\">用户名:</td>\r\n                <td width=\"78%\" height=\"30\">&nbsp;\r\n                  <input name=\"username\" class=\"textbox\" width=\"200\" \r\n                  id=\"moTask\" value=\"");
      if (_jspx_meth_c_out_0(_jspx_page_context))
        return;
      out.write("\">\r\n\t\t\t\t  \r\n\t\t\t\t  *需要验证账号，账号名不能更改 </td>\r\n              </tr>\r\n              <tr>\r\n                <td align=\"right\"  bgcolor=\"#f8f8f8\" height=\"30\">旧的密码:</td>\r\n                <td  bgcolor=\"#f8f8f8\" height=\"30\">&nbsp;\r\n                  <input name=\"password\" type=\"password\" class=\"textbox\" \r\n                  id=\"className\" width=\"200\"/></td>\r\n              </tr>\r\n\r\n              <tr>\r\n                <td align=\"right\" height=\"30\">新的密码:</td>\r\n                <td height=\"30\">&nbsp;\r\n                  <input name=\"newpwd1\" type=\"password\" class=\"textbox\" \r\n                  id=\"timeOut\" value=\"\" width=\"200\" />\r\n                  *不填不修改密码</td>\r\n              </tr>\r\n              \r\n              <tr>\r\n                <td align=\"right\" bgcolor=\"#f8f8f8\" height=\"30\">确认新密码:</td>\r\n                <td bgcolor=\"#f8f8f8\" height=\"30\">&nbsp;\r\n                  <input name=\"newpwd2\" type=\"password\" class=\"textbox\" \r\n                  id=\"timeout\" value=\"\" width=\"200\" /></td>\r\n              </tr>\r\n\t\t\t            \r\n              <tr>\r\n");
      out.write("                <td align=\"right\" height=\"30\">姓名:</td>\r\n                <td height=\"30\">&nbsp;\r\n                  <input name=\"realname\" class=\"textbox\" width=\"200\" \r\n                  id=\"timeout2\" value=\"");
      if (_jspx_meth_c_out_1(_jspx_page_context))
        return;
      out.write("\" \r\n\t\t\t\t   /></td>\r\n              </tr>\r\n              <tr>\r\n                <td align=\"right\" bgcolor=\"#f8f8f8\" height=\"30\">电子信箱:</td>\r\n                <td bgcolor=\"#f8f8f8\" height=\"30\">&nbsp;\r\n                  <input name=\"email\" class=\"textbox\" width=\"200\" \r\n                  id=\"timeout2\" value=\"");
      if (_jspx_meth_c_out_2(_jspx_page_context))
        return;
      out.write("\" \r\n\t\t\t\t   /></td>\r\n              </tr>\r\n              <tr>\r\n                <td align=\"right\" height=\"30\">手机号:</td>\r\n                <td height=\"30\">&nbsp;\r\n                  <input name=\"mobile\" class=\"textbox\" width=\"200\" \r\n                  id=\"timeout3\" value=\"");
      if (_jspx_meth_c_out_3(_jspx_page_context))
        return;
      out.write("\" \t\t\t\t  \r\n\t\t\t\t   /></td>\r\n              </tr>\r\n              \r\n              <tr>\r\n                <td  height=\"23\" align=\"right\" background=\"../img/pub/top1.gif\">&nbsp;</td>\r\n                <td  height=\"23\" background=\"../img/pub/top1.gif\">&nbsp;\r\n                <input class=\"bottonbox\" id=\"submit\" type=\"submit\" \r\n\t\t\t\tvalue=\"修改账号\"\r\n\t\t\t\t name=\"dotype\"  onclick=\"return(checkLoginuser());\"/>\r\n&nbsp;\r\n<input class=\"bottonbox\" type=\"reset\" value=\"重新填写\" name=\"Submit11\" /><span class=\"redtitle\" id=\"dogetmsg\">\r\n\t\t\t\t   \t");
      if (_jspx_meth_c_out_4(_jspx_page_context))
        return;
      out.write("\t\t\t\r\n\t\t\t\t  </span></td>\r\n              </tr>\r\n            </tbody>\r\n        </table></td>\r\n    </tr>\r\n  </tbody>\r\n</table>\r\n</FORM>\r\n</body>\r\n</html>");
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
    _jspx_th_c_out_0.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smusermodify.username}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_0 = _jspx_th_c_out_0.doStartTag();
    if (_jspx_th_c_out_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_0);
    return false;
  }

  private boolean _jspx_meth_c_out_1(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_1 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_1.setPageContext(_jspx_page_context);
    _jspx_th_c_out_1.setParent(null);
    _jspx_th_c_out_1.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smusermodify.realname}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_1 = _jspx_th_c_out_1.doStartTag();
    if (_jspx_th_c_out_1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_1);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_1);
    return false;
  }

  private boolean _jspx_meth_c_out_2(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_2 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_2.setPageContext(_jspx_page_context);
    _jspx_th_c_out_2.setParent(null);
    _jspx_th_c_out_2.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smusermodify.email}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_2 = _jspx_th_c_out_2.doStartTag();
    if (_jspx_th_c_out_2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_2);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_2);
    return false;
  }

  private boolean _jspx_meth_c_out_3(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_3 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_3.setPageContext(_jspx_page_context);
    _jspx_th_c_out_3.setParent(null);
    _jspx_th_c_out_3.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smusermodify.mobile}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_3 = _jspx_th_c_out_3.doStartTag();
    if (_jspx_th_c_out_3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_3);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_3);
    return false;
  }

  private boolean _jspx_meth_c_out_4(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:out
    org.apache.taglibs.standard.tag.rt.core.OutTag _jspx_th_c_out_4 = (org.apache.taglibs.standard.tag.rt.core.OutTag) _jspx_tagPool_c_out_value_nobody.get(org.apache.taglibs.standard.tag.rt.core.OutTag.class);
    _jspx_th_c_out_4.setPageContext(_jspx_page_context);
    _jspx_th_c_out_4.setParent(null);
    _jspx_th_c_out_4.setValue((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${smusermodify.doMsg}", java.lang.Object.class, (PageContext)_jspx_page_context, null, false));
    int _jspx_eval_c_out_4 = _jspx_th_c_out_4.doStartTag();
    if (_jspx_th_c_out_4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_4);
      return true;
    }
    _jspx_tagPool_c_out_value_nobody.reuse(_jspx_th_c_out_4);
    return false;
  }
}
