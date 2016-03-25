<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>查看激活线程</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=systhreads.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 系统 - 激活线程</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">
            &nbsp;&nbsp;<a href="system.do">重载类</a> 
            &nbsp;&nbsp;<a href="sysproperties.do">查看系统变量</a>           
            &nbsp;&nbsp;<span class="JiaCu">激活线程</span>    
            &nbsp;&nbsp;<a href="sysmemory.do">系统信息</a> 
			</td>
          </tr>
          <tr>
            <td height="30" align="left" valign="middle">
            
            <table width="100%" border="0" cellpadding="0" cellspacing="15" bgcolor="#EEEEEE" bordercolor="#EEEEEE">
                <tbody>
                  <tr>
                    <td class="msg" width="100%" height="50" valign="top" bordercolor="#999999" bgcolor="#FFFFFF"><br/> 
                    <c:forEach items="${menwatch.rows}" var="mysrv">
                    &nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${mysrv}"/><br/>
                    </c:forEach>
                    
<br/> 	 				                   </td>
                  </tr>
                </tbody>
            </table>
            
            </td>
          </tr>
          <tr>
            <td height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">
			&nbsp;&nbsp;
			<input name="loadclass" type="submit" class="bottonbox" id="loadclass" value="刷新" />	
</td>
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