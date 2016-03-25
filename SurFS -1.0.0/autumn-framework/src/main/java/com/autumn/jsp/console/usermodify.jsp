<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改登录资料</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=modifyuser.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> 用户管理 - 修改登录资料</span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table id="Table1" cellspacing="0" cellpadding="0" width="100%" 
            align="center" bgcolor="#EFEFEF" border="0">
            <tbody>

              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
              <tr>
                <td width="22%" height="30" align="right">用户名:</td>
                <td width="78%" height="30">&nbsp;
                  <input name="username" class="textbox" width="200" 
                  id="moTask" value="<c:out value='${smusermodify.username}'/>">
				  
				  *需要验证账号，账号名不能更改 </td>
              </tr>
              <tr>
                <td align="right"  bgcolor="#f8f8f8" height="30">旧的密码:</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="password" type="password" class="textbox" 
                  id="className" width="200"/></td>
              </tr>

              <tr>
                <td align="right" height="30">新的密码:</td>
                <td height="30">&nbsp;
                  <input name="newpwd1" type="password" class="textbox" 
                  id="timeOut" value="" width="200" />
                  *不填不修改密码</td>
              </tr>
              
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">确认新密码:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="newpwd2" type="password" class="textbox" 
                  id="timeout" value="" width="200" /></td>
              </tr>
			            
              <tr>
                <td align="right" height="30">姓名:</td>
                <td height="30">&nbsp;
                  <input name="realname" class="textbox" width="200" 
                  id="timeout2" value="<c:out value='${smusermodify.realname}'/>" 
				   /></td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">电子信箱:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="email" class="textbox" width="200" 
                  id="timeout2" value="<c:out value='${smusermodify.email}'/>" 
				   /></td>
              </tr>
              <tr>
                <td align="right" height="30">手机号:</td>
                <td height="30">&nbsp;
                  <input name="mobile" class="textbox" width="200" 
                  id="timeout3" value="<c:out value='${smusermodify.mobile}'/>" 				  
				   /></td>
              </tr>
              
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td  height="23" background="../img/pub/top1.gif">&nbsp;
                <input class="bottonbox" id="submit" type="submit" 
				value="修改账号"
				 name="dotype"  onclick="return(checkLoginuser());"/>
&nbsp;
<input class="bottonbox" type="reset" value="重新填写" name="Submit11" /><span class="redtitle" id="dogetmsg">
				   	<c:out value='${smusermodify.doMsg}'/>			
				  </span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>