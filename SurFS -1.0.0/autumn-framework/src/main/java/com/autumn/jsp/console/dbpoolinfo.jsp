<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>数据库连接池监视</title>
</style>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>

<body>
<FORM id=Form1 name=Form1 action=dbpools.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 数据库连接池 - 监视</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          
<table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
                       <tr>
                <td height="1" colspan="2" bgcolor="#999999"></td>
              </tr>
          <tr>
            <td height="30" align="left" valign="middle" nowrap="nowrap">&nbsp; &nbsp;&nbsp;
            连接池名:<c:out value="${jndiname}"/>,<c:out value="${coninfo}"/>
            </td>
          </tr>
        </tbody>
      </table>
        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
           >
            <tbody>
              
              <tr>
                <td width="30%"  height="23" align="center" background="../img/pub/top1.gif" class="JiaCu">类名</td>
                <td width="40%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">最后访问时间</td>
                <td width="15%" align="middle" background="../img/pub/top1.gif" class="JiaCu">创建的声明数目</td>
                <td width="15%" align="middle" background="../img/pub/top1.gif" class="JiaCu">是否空闲</td>
              </tr>

			  <c:forEach items="${cons}" var="mysrv">
              <tr>
                <td height="23" align="center" nowrap="nowrap" bgcolor="#EFEFEF"><c:out value="${mysrv.className}"/></td>
                <td
              height="23" align="left"><c:out value="${mysrv.lastAccessTime}"/></td>
                <td align="middle" nowrap="nowrap"><c:out value="${mysrv.statementSize}"/></td>
                <td align="middle" nowrap="nowrap"><c:if test='${mysrv.inUse==true}'>占用</c:if></td>
                </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              
              <tr>
                <td height="23" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"><input name="jndiname" type="hidden" id="jndiname" value="<c:out value='${jndiname}'/>" />
                  <input name="dotype"  type="submit" class="bottonbox" id="Submit" value="刷新" />
			&nbsp;&nbsp;
            <input class="bottonbox" type="submit" value="返回" name="Submit11" /></td>
              </tr>
            </tbody>
        </table></td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>