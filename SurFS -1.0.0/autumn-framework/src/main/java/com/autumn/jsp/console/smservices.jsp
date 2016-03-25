<%@ page language="java" contentType="text/html; charset=utf8"%>
<%@ page import= "org.codehaus.xfire.*"%>
<%@ page import= "com.autumn.core.soap.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>查看Soap服务</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /><strong> 查看Soap服务</strong></span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table style="word-wrap: break-word; word-break: break-all;" id="Table1" cellspacing="0" cellpadding="0" width="100%" 
            align="center"  border="0">
            <tbody>        
              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
              <tr>
                <td  height="86" colspan="2" align="center">
                <% 
				XFire xfire =XFireFactory.newInstance().getXFire();
				if(xfire.getServiceRegistry().getServices().isEmpty()){	%>		
                <span class="redtitle">没有注册服务</span>
                <%}else{%>
<iframe width="100%" height="500" frameborder="0" name="letter_body" id="content_frame" src="../services/">
</iframe>		
<%}%>
				</td>
              </tr>
              <tr>
                <td width="8%"  height="23" align="left" background="../img/pub/top1.gif"></td>
                <td width="92%"  height="23" align="left" background="../img/pub/top1.gif"><a href="soaps.do">返回</a></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</body>
</html>