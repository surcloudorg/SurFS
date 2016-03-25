<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>插入数据</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=test method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> 插入数据</span></td>
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
                <td width="22%" height="30" align="right">姓名:</td>
                <td width="78%" height="30">&nbsp;
                  <input name="name" class="textbox" id="moTask" value='<c:out value="${demo.name}"/>' width="200"> </td>
              </tr>
              <tr>
                <td align="right"  bgcolor="#f8f8f8" height="30">年龄:</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="age" type="text" class="textbox"  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="className" value='<c:out value="${demo.age}"/>' width="200"/></td>
              </tr>

              <tr>
                <td align="right" height="30">性别:</td>
                <td height="30">&nbsp;
                  <input name="sex" type="text" class="textbox"   OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="timeOut" value='<c:out value="${demo.sex}"/>' width="200" /></td>
              </tr>
              
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">电话:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="mobile" type="text" class="textbox" 
                  id="timeout" value='<c:out value="${demo.mobile}"/>' width="200" /></td>
              </tr>
              
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td  height="23" background="../img/pub/top1.gif">&nbsp;
                <input class="bottonbox" id="submit" type="submit" value="插入" name="button"   />
&nbsp;
<input class="bottonbox" type="reset" value="重新填写" name="Submit11" /><span class="redtitle" id="dogetmsg">
				   	<c:out value='${error}'/>			
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