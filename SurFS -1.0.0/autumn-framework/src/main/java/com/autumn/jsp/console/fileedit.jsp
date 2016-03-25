<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>编辑文件</title>
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
                  align="absmiddle" /> 编辑文件</span></td>
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
                &nbsp;地址:
                  <c:out value='${smupload.pathStr}'/><c:out value='${smupload.dirname}'/></td>
              </tr>
              <tr>
                <td height="30" align="left" bgcolor="#EFEFEF">
                <table width="100%" border="0" cellpadding="0" cellspacing="5" bgcolor="#EFEFEF">
                <tr><td width="100%" height="50" valign="top">
                
                <textarea name="content" rows="25" class="msg" id="content"><c:out value='${smupload.content}'/></textarea>
                  <input name="pathStr" type="hidden" id="pathStr" value="<c:out value='${smupload.pathStr}'/>" />
    <input name="dirname" type="hidden" id="dirname" value="<c:out value='${smupload.dirname}'/>" />
    <input type="hidden" name="forward" id="noforward" value="<c:out value='${smupload.forward}'/>" />
    
    			</td></tr>
    			</table>                </td>
              </tr>
              
              <tr>
                <td  height="30" align="center" bgcolor="#EFEFEF"><c:if test="${smupload.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" onclick="return(confirm('确认更改?'))" 
				value="保存" name="dotype" /></c:if>
  &nbsp;
  <input name="dotype" type="submit" class="bottonbox" value="返回" />
  <span class="redtitle">
  <c:out value='${smupload.doMsg}'/></span></td>
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