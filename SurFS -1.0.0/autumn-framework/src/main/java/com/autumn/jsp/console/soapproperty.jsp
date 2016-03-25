<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>参数设置</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=soaps.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 参数设置</strong></td>
    </tr>
    <tr>
      <td height="500" align="right" valign="top">
<br />
	  
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
         
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;&nbsp;<c:out value='${smsoap.id}'/>-<c:out value='${smsoap.title}'/></td>
            </tr>
            <c:forEach items="${smsoap.soapConfig.properties}" var="mapItem"> 
          <tr>
            <td width="30%" height="30" align="right" valign="middle" nowrap="nowrap">
            <c:out value='${mapItem.comment}'/>(<c:out value='${mapItem.key}'/>)</td>
            <td width="70%" height="30" align="left" valign="middle" nowrap="nowrap" bgcolor="#f8f8f8">&nbsp;<input name="<c:out value='${mapItem.key}'/>" type="text"  class="textbox2"  value="<c:out value='${mapItem.value}'/>" /></td>
          </tr>
          </c:forEach> 
          <tr>
            <td height="23" align="right" valign="middle" nowrap="nowrap" bgcolor="#EEEEEE"  background="../img/pub/top1.gif">&nbsp;
              <input type="hidden" name="id" id="id" value="<c:out value='${smsoap.id}'/>" />
              &nbsp;
              <c:if test="${smsoap.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="设置属性" /></c:if>            </td>
            <td  align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;&nbsp;
            &nbsp;<c:if test="${smsoap.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="保存属性" /></c:if>&nbsp;&nbsp;
            &nbsp;
            <input class="bottonbox" type="submit" value="返回" name="Submit11" />
            <span class="redtitle">
              <c:out value="${smsoap.doMsg}"/>
              
            </span></td>
            </tr>
        </tbody>
      </table>	  
	  </td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>