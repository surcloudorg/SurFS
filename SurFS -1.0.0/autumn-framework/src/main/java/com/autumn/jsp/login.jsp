<%@ page session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html; charset=utf8" %>
<html>
<head>
<title>autumn-地球最好的java开发框架</title>

<style type="text/css">
<!--
.errtext {color: #FF0000}
-->
</style>
</head>
<body>
<style>
<!--
a:link       { font-size: 9pt; color: #000000;text-decoration: none; }
a:visited    { color: #000000; font-size: 9pt;text-decoration: none; }
a:hover      { font-size: 9pt; color: #000000;TEXT-DECORATION: underline; }
body         { color: #000000; font-size: 9pt }
p            { color: #000000; font-size: 9pt }
td           { font-size: 9pt; color: #000000 }
input        { font-size: 9pt; color: #000000 }
-->
</style>
<div style="position: absolute; width: 16px; height: 13px; z-index: 3; left: 738px; top: 217px" id="layer6">
	<a title="关闭窗口" style="text-decoration: none; font-family: AdobeSm; color: #000000" href="javascript:window.close()">
	<span style="BACKGROUND-COLOR: #ffffff">×</span></a></div>
<script language=javascript>
<!--
function CheckForm(){
	if(document.form.username.value==""){
		document.getElementById("errorInfo").innerHTML="请输入用户名！";
		document.form.username.focus();
		return false;
	}
	if(document.form.password.value == ""){
		document.getElementById("errorInfo").innerHTML="请输入密码！";
		document.form.password.focus();
		return false;
	}
}
//-->
</script>
<div style="position: absolute; width: 140px; height: 15px; z-index: 1; left: 301px; top: 251px" id="layer1">
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <form name="form" onSubmit="return CheckForm()" method="post" action="login.do">
		<tr>
			<td><img border="0" src="img/icon.png"></td>
		</tr>
	</table>
</div>
<div style="position: absolute; width: 493px; height: 204px; z-index: 2; left: 272px; top: 203px" id="layer2">
	<table border="0" width="493" cellspacing="0" cellpadding="0" height="194">
		<tr>
			<td width="2" height="8" background="img/table_bg_t.gif">
			<img border="0" src="img/table_corner_t_l.gif" width="8" height="8"></td>
		  <td height="8" width="477" background="img/table_bg_t.gif"></td>
			<td height="8" width="8">
			<img border="0" src="img/table_corner_t_r.gif" width="8" height="8"></td>
		</tr>
		<tr>
			<td width="8" height="186" background="img/table_bg_l.gif">　</td>
		  <td height="186" width="477">
			<div style="position: absolute; width: 100px; height: 100px; z-index: 1; left: 12px; top: 15px" id="layer3">
				<img border="0" src="img/title_fast_deposit_channel.gif" width="200" height="30"></div>
			<div style="position: absolute; width: 288px; height: 120px; z-index: 2; left: 214px; top: 52px" id="layer4">
			  <table border="0" width="100%" cellspacing="0" cellpadding="0" height="125">
					<tr>
						<td valign="top">
						<table border="0" width="100%" cellspacing="0" cellpadding="0" height="122">
							<tr>
							  <td colspan="2"><span class="errtext"><span id="errorInfo" style="font-size: 9pt"><c:out value="${error}"/></span></span></td>
						  </tr>
							<tr>
								<td width="21%"><font color="#006699"><span style="font-size: 9pt">
								登录帐号：</span></font></td>
								<td width="79%">
								&nbsp;<input type="text" name="username" maxlength="20" style="border: 1px solid #006699;width: 150;" value="${param.username}"> </td>
							</tr>
							<tr>
								<td width="21%"><font color="#006699"><span style="font-size: 9pt">
								登录密码：</span></font></td>
								<td width="79%">
								&nbsp;<input type="password" name="password" maxlength="20" style="border: 1px solid #006699;width: 150;"> </td>
							</tr>
							
							
							<tr>
                                <td width="21%"></td>
								<td width="79%">
						 		&nbsp;<input type="submit" value="确定" name="B1" style="border: 1px solid #006699; background-color: #FFFFFF">&nbsp;&nbsp;
								<input type="reset" value="重置" name="B2" style="border: 1px solid #006699; background-color: #FFFFFF">
							  </td>
							</tr>
						</table>
						</td>
					</tr>
			  </table>
			</div>
			<p></td>
			<td height="186" width="8" background="img/table_bg_r.gif">
			<p align="right"></td>
		</tr>
		<tr>
			<td width="2" height="1" background="img/table_bg_b.gif">
			<img border="0" src="img/table_corner_b_l.gif" width="8" height="8"></td>
		  <td height="1" width="477" background="img/table_bg_b.gif"></td>
			<td height="1" width="8" background="img/table_corner_b_r.gif"></td>
		</tr>
	</table>
</div>
<div align="center"><span style="position: absolute; width: 360px; height: 14px; z-index: 1; left: 335px; top: 407px"><font color="#B5B5B5"><span style="font-size: 8pt">JAVA工程管理框架1.0版 QQ:109825486 刘社朋 2012年5月</span></font></span></div>
</body>
</form>
</html>