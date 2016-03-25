<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>编译JAVA</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=upload.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> 编译结果</span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table id="Table1" cellspacing="0" cellpadding="0" width="100%" 
            align="center" bgcolor="#EFEFEF" border="0">
            <tbody>
              <tr>
                <td bgcolor="#999999" height="1"></td>
              </tr>
<tr>
                <td  height="30" align="left" bgcolor="#EFEFEF">
                &nbsp;编译错误信息</td>
              </tr>
              <tr>
                <td height="30" align="left" bgcolor="#EFEFEF">
                <table width="100%" border="0" cellpadding="0" cellspacing="5" bgcolor="#EFEFEF">
                <tr><td width="100%" height="50" valign="top">
                
                <textarea name="content" cols="120" rows="35" readonly="readonly" class="msg" id="content"><c:out value='${smupload.doMsg}'/>
                </textarea>
                </td></tr></table>
                
                </td>
              </tr>
              
              <tr>
                <td  height="30" align="center" bgcolor="#EFEFEF"><input name="Submit11" type="submit" class="bottonbox" value="返回" /><input name="pathStr" type="hidden" id="pathStr" value="<c:out value='${smupload.pathStr}'/>" />
    <input name="dirname" type="hidden" id="dirname" value="<c:out value='${smupload.dirname}'/>" /></td>
              </tr>
              <tr>
                <td bgcolor="#999999" colspan="1" height="1"></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>