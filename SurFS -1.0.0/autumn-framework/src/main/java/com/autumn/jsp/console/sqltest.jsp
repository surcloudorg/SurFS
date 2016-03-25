<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>数据库连接池</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/wdatepicker/wdatepicker.js"></script>
<body>
<FORM id=Form1 name=Form1 action=sqltest.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 数据库连接池</strong><strong> - 连接测试</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>

          <tr>
            <td height="23" align="left" valign="middle" background="../img/pub/top1.gif" bgcolor="#cccccc" class="JiaCu">&nbsp;连接池：
              <c:out value="${sqltest.jndiname}"/>
              <input name="jndiname" type="hidden" id="jndiname" value="<c:out value='${sqltest.jndiname}'/>"/></td>
          </tr>
          
          
          <tr>
            <td height="120" align="left" valign="middle" bgcolor="#EEEEEE"> &nbsp;<textarea name="sql" cols="100" rows="8" class="textarea"><c:out value='${sqltest.sql}'/></textarea>
              <c:if test="${sqltest.accessPermission>1}"><input name="Submit"  type="submit" class="bottonbox" value="执行" /></c:if>
              <input name="Submit2" type="button" class="bottonbox" value="清空" onclick="document.Form1.sql.value=''"/>
              <input name="Submit3" type="submit" class="bottonbox" value="返回" onclick="document.Form1.action='dbpools.do'" /></td>
          </tr>
        </tbody>
      </table>
	  
	  <table cellspacing="0" cellpadding="0" width="100%" align="center" bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="30" colspan="2" align="left" valign="middle">
                
                <table style="word-wrap: break-word; word-break: break-all;" width="100%" border="0" cellpadding="0" cellspacing="5" bordercolor="#EEEEEE">
                  <tbody>
                    <tr>
                      <td class="msg" width="100%" height="50" valign="top" bordercolor="#999999" bgcolor="#FFFFFF">
					 <c:forEach items="${sqltest.findResult}" var="result"> 
					 &nbsp;&nbsp;<c:out value='${result}'/> <br/>
					 </c:forEach>					  </td>
                    </tr>
                  </tbody>
                </table>
                
                </td>
              </tr>
              <tr>
                <td width="99%" height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;
                <c:out value='${sqltest.timeconsum}'/></td>
                <td width="1%" align="left" valign="left" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;</td>
              </tr>
            </tbody>
        </table>
</td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>