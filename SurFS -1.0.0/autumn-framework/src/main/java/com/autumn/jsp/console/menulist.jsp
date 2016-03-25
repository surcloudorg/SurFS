<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=gb2312"%>

<link rel="stylesheet" type="text/css" href="../img/pub/menu.css">
<script type="text/javascript" src="../img/pub/sys.js"></script>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html:html><head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>www.autumn.com</title>
</head>
<body style="margin:0px" onLoad="document.getElementById('<c:out value="${smmenu.init}"/>').style.backgroundImage='url(../img/pub/over.gif)'">
	<table border="0" width="100%" height="100%" align="center" cellpadding="0" cellspacing="0">
		<tr>
		  <td valign="top">
          
          <table border="0" id="imgtable" cellpadding="0" align="center" cellspacing="0" width="100%">
            <tr valign="bottom">
              <td height="25" background="../img/pub/content_top_bg.jpg" valign="bottom">&nbsp;&nbsp;&nbsp;&nbsp;<strong>
                      <c:out value="${LoginUser.realname}"/>
              </strong></td>
            </tr>
            <tr>
              <td style="padding-left:6px;">&nbsp;</td>
            </tr>
              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
            <tr>
              <td class="frame">
              <table width="100%" border="0" cellpadding="0" cellspacing="4" id="folder" style="cursor: hand;" background="../img/pub/side-bg.png">	  
			  <c:forEach items="${smmenu.rows}" var="mysrv">
                <tr id="${mysrv.action}">
                  <td width="100%" height="25"><a href="${mysrv.action}" 
				 target="mainFrame" class="a" style="padding-top:4px" onClick="onSelectImg('${mysrv.action}');"><img src="../img/pub/${mysrv.action}.gif" width="16" height="16" border="0" style="margin-bottom:-3px"/> <c:out value='${mysrv.showMenu}'/> </a>
				   </td>
                </tr>  
			   </c:forEach>
                <tr id="help.jsp">
                  <td width="100%" height="25"><a href="../img/help.htm" target="_blank" class="a" style="padding-top:4px"> <img src="../img/pub/help.jsp.gif" width="16" height="16" border="0" style="margin-bottom:-3px"/> 开发规范及帮助 </a> </td>
                </tr>
                <tr>
                  <td width="100%" height="25"><a href="../login.jsp" target="_parent" class="a" style="padding-top:4px"> <img src="../img/pub/login.jsp.gif" width="16" height="16" border="0" style="margin-bottom:-3px"/> 退出系统 </a> </td>
                </tr>
                <tr><td width="100%" height="800"></td></tr>			   
              </table></td>
            </tr>
          </table></td>
		</tr>		 
	</table>
</body>
</html:html> 