<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SVN在线更新</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=svncodes.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> <strong>SVN在线更新</strong> - <c:out value='${smcode.dotype}'/></span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table id="Table1" cellspacing="0" cellpadding="0" width="100%" 
            align="center" bgcolor="#f1f1f1" border="0">
            <tbody>

              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
              <tr>
                <td align="right" width="15%" height="30">标题:</td>
                <td width="85%" height="30">&nbsp;
                    <input name="title" class="textbox2" width="200" 				
                  id="srvId" value="<c:out value='${smcode.title}'/>" /></td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">SVN地址:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                <input name="url" class="textbox2" width="200"
                  id="url" value="<c:out value='${smcode.url}'/>" />
                <input name="id" type="hidden" id="id" value="<c:out value='${smcode.id}'/>"/></td>
              </tr>
              

              <tr>
                <td align="right"  height="30">SVN帐号:</td>
                <td height="30">&nbsp;
                <input name="userName" class="textbox" width="200"				
                  id="userName" value="<c:out value='${smcode.userName}'/>" />
                &nbsp;&nbsp;
                SVN密码:<input name="passWord" class="textbox" width="200"				
                  id="passWord" value="<c:out value='${smcode.passWord}'/>" /></td>
              </tr>
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">源码类型:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                <c:choose> 
				<c:when test='${smcode.dirType==0}'>	
                <input type="radio" name="dirType" id="radio1" value="1"  checked="checked"/>
                </c:when>
                <c:otherwise> 
                <input type="radio" name="dirType" id="radio1" value="1"/>
                </c:otherwise> 
                </c:choose> 
                <a href="#" onclick="document.getElementById('radio1').checked=!document.getElementById('radio1').checked">JSP源码</a> &nbsp;&nbsp;
                <c:choose> 
				<c:when test='${smcode.dirType==1}'>	
                <input name="dirType" type="radio" id="radio2" value="0" checked="checked" />
                </c:when>
                <c:otherwise> 
                <input name="dirType" type="radio" id="radio2" value="0" />
                </c:otherwise> 
                </c:choose>                 
                <a href="#" onclick="document.getElementById('radio2').checked=!document.getElementById('radio2').checked">JAVA源码</a></td>
              </tr>
              <tr>
                <td  height="30" align="right">本地目录:</td>
                <td height="30">&nbsp;
                <input name="dirName" class="textbox" width="200"
                  id="dirName" value="<c:out value='${smcode.dirName}'/>" />
                *
                  SVN服务器检出的目标文件夹，java源码应在<a href="smupload.do?pathStr=C:\Program Files\Apache Software Foundation\Tomcat 6.0\webapps\smservices\WEB-INF\">WEB-INF\src下</a></td>
              </tr>
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smcode.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" 
				value="<c:out value='${smcode.dotype}'/>"
				 name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smcode.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>