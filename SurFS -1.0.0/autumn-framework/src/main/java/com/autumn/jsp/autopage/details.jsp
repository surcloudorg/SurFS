<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><c:out value="${title}"/></title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/wdatepicker/wdatepicker.js"></script>
<body>
<FORM id=Form name=Form action="${action}" method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" />${title}</strong></td>
    </tr>
    <tr>
      <td height="500" align="right" valign="top">
<br />
	  
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
         
          <tr>
            <td height="23" colspan="2" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"><span class="redtitle"><c:out value='${domessage}'/> 
            </span></td>
          </tr>
          <c:forEach items="${params}" var="srv">
          <tr>
            <td width="20%" height="30" align="right" valign="middle" nowrap="nowrap"><c:out value='${srv[0]}'/></td>
            <td width="80%" height="30" align="left" valign="middle" nowrap="nowrap" bgcolor="#f8f8f8">&nbsp;<c:out value='${srv[1]}' escapeXml="false"/></td>
          </tr>
          </c:forEach> 
          <tr>
            <td height="23" align="center" valign="middle" nowrap="nowrap"  background="../img/pub/top1.gif" bgcolor="#EEEEEE">&nbsp; </td>
            <td height="23" align="left" valign="middle" nowrap="nowrap"  background="../img/pub/top1.gif" bgcolor="#EEEEEE"><c:out value='${button}' escapeXml="false"/></td>
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