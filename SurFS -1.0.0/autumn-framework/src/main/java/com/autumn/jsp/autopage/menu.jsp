<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=gb2312"%>

<%@ page import= "com.autumn.core.autopage.AutoMenu"%>

<% 
    AutoMenu am=new AutoMenu(request,response);
%>

<link rel="stylesheet" type="text/css" href="../img/pub/menu.css">
<script type="text/javascript" src="../img/pub/sys.js"></script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html:html><head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title><c:out value="${smmenu.title}"/></title>
</head>
<body style="margin:0px" 
onLoad="document.getElementById('<c:out value="${smmenu.init}"/>').style.backgroundImage='url(../img/pub/over.gif)'">
	<table border="0" width="100%" height="100%" align="center" cellpadding="0" cellspacing="0">
		<tr>
		  <td valign="top"><table id="imgtable" border="0" cellpadding="0" align="center" cellspacing="0" width="100%">
            <tr valign="bottom">
              <td height="25" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;&nbsp;&nbsp;<span class="selWidth"><strong>管理员：
                      <c:out value="${LoginUser.realname}"/>
              </strong></span></td>
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
                  <td  width="100%" height="25"><a href="<c:out value='${mysrv.action}'/>" 
				 target="mainFrame" class="a" style="padding-top:4px" onClick="onSelectImg('${mysrv.action}');">&nbsp;<c:out value='${mysrv.showMenu}'/> </a>
				   </td>
                </tr>  
			   </c:forEach>
                <tr>
                  <td  width="100%" height="25"><a href="../login.jsp" target="_parent" class="a" style="padding-top:4px">&nbsp;退出系统 </a> </td>
                </tr>	
                <tr><td width="100%" height="800"></td></tr>					   
              </table></td>
            </tr>
          </table></td>
		</tr>		 
	</table>
</body>
</html:html> 