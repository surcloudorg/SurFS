<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>输入图形验证码</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form name=Form action="${verify.config_ActionId}.do" method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img height="14" src="../img/pub/icoblue.gif" width="14" align="absmiddle" />输入图形验证码</strong></td>
    </tr>
    <tr>
      <td height="500" align="right" valign="top">
<br />
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" colspan="2" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">
            <c:if test="${empty enter}">没有输入图形验证码的需求</c:if>
            <c:if test="${!empty enter}">${enter.comment}</c:if>
            </td>
          </tr>
<c:if test="${!empty enter}">
          <tr>
            <td height="23" align="right" valign="middle" nowrap="nowrap" bgcolor="#EEEEEE"><img src="${verify.config_ActionId}.do?dotype=getimg&enterHashcode=${verify.enterHashcode}"/></td>
            <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EEEEEE">&nbsp;&nbsp;<input name="dotype" type="submit" class="bottonbox" id="dotype" value="看不清，换一张" />
              <input name="enterHashcode" type="hidden" id="enterHashcode" value="${verify.enterHashcode}" /></td>
          </tr>
          <tr>
            <td width="33%" height="23" align="right" valign="middle" nowrap="nowrap"    bgcolor="#EEEEEE">验证码:
              <input name="code" type="text" class="textbox1" id="code" /></td>
            <td width="67%" height="32" align="left" valign="middle" nowrap="nowrap"    bgcolor="#EEEEEE"> &nbsp;&nbsp;<input name="dotype" type="submit" class="bottonbox" id="dotype" value="提交" /></td>
          </tr>
</c:if>         
<c:if test="${empty enter}">
          <tr>
            <td height="23" colspan="2" align="center" valign="middle" nowrap="nowrap" bgcolor="#EEEEEE">&nbsp; </td>
            </tr>
</c:if>    
          <tr>
            <td height="23" colspan="2" align="center" valign="middle" nowrap="nowrap"  background="../img/pub/top1.gif" bgcolor="#EEEEEE">&nbsp; </td>
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