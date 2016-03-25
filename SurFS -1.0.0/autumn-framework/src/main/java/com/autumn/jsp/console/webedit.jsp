<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Web目录管理</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=webs.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> <strong>Web目录管理</strong> - <c:out value='${smweb.dotype}'/></span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table id="Table1" cellspacing="0" cellpadding="0" width="100%" 
            align="center" bgcolor="#f1f1f1" border="0">
            <tbody>

              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
              <tr>
                <td align="right" width="15%" height="30">标题:</td>
                <td width="85%" height="30">&nbsp;
                    <input name="title" class="textbox2" width="200" 				
                  id="srvId" value="<c:out value='${smweb.title}'/>" />
                    *注明该目录的主要功能</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">目录名:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="dirName" class="textbox" width="200" 				
                  id="title" value="<c:out value='${smweb.dirName}'/>" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  首页:
                  <input name="defaultPage" class="textbox" width="200" 				
                  id="title2" value="<c:out value='${smweb.defaultPage}'/>" />
                  *登录后访问的默认页</td>
              </tr>
              <tr>
                <td align="right" height="30">日志目录:</td>
                <td height="30">&nbsp; 
                
                <select name="logname" class="textbox2" id="logname">
                <c:forEach items="${smweb.dirlist}" var="mapItem"> 
                    <option value="<c:out value='${mapItem}'/>" 
					<c:if test='${smweb.logname==mapItem}'>
					selected="selected" </c:if>><c:out value='${mapItem}'/> </option>
                 </c:forEach>    
                </select>
                  *指定web目录日志输出目录</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">ip地址:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="ipList" class="textbox2" width="200" 				
                  id="defaultPage" value="<c:out value='${smweb.ipList}'/>" />
                  *ip验证（支持192.168.*.*），不填不验证</td>
              </tr>
              <tr>
                <td align="right" height="30">&nbsp;</td>
                <td height="30"> &nbsp;
				<c:choose> 
				<c:when test='${smweb.logintype==0}'>	
                  <input name="logintype" type="radio" value="0" id="radio1" checked="checked" />
				</c:when>
				<c:otherwise> 
				  <input name="logintype" type="radio" value="0" id="radio1" />
				</c:otherwise> 
				</c:choose> 
                <a href="#" onclick="document.getElementById('radio1').checked=!document.getElementById('radio1').checked">禁用此目录</a> 				<c:choose> 
				<c:when test='${smweb.logintype==1}'>	
                  <input name="logintype" type="radio" value="1" id="radio2" checked="checked" />
				</c:when>
				<c:otherwise> 
				  <input name="logintype" type="radio" value="1" id="radio2" />
				</c:otherwise> 
				</c:choose> 
                <a href="#" onclick="document.getElementById('radio2').checked=!document.getElementById('radio2').checked">此目录需要登录</a>                
 				<c:choose> 
				<c:when test='${smweb.logintype==2}'>	
                  <input name="logintype" type="radio" value="2" id="radio3" checked="checked" />
				</c:when>
				<c:otherwise> 
				  <input name="logintype" type="radio" value="2" id="radio3" />
				</c:otherwise> 
				</c:choose> 
                <a href="#" onclick="document.getElementById('radio3').checked=!document.getElementById('radio3').checked">此目录不需登录 </a> 
 				<c:choose> 
				<c:when test='${smweb.logintype==3}'>	
                  <input name="logintype" type="radio" value="3" id="radio4" checked="checked" />
				</c:when>
				<c:otherwise> 
				  <input name="logintype" type="radio" value="3" id="radio4" />
				</c:otherwise> 
				</c:choose> 
                <a href="#" onclick="document.getElementById('radio4').checked=!document.getElementById('radio4').checked">公共目录，任意用户可访问</a>
                <c:choose> 
				<c:when test='${smweb.logintype==4}'>	
                  <input name="logintype" type="radio" value="4" id="radio5" checked="checked" />
				</c:when>
				<c:otherwise> 
				  <input name="logintype" type="radio" value="4" id="radio5" />
				</c:otherwise> 
				</c:choose> 
                <a href="#" onclick="document.getElementById('radio5').checked=!document.getElementById('radio5').checked">需要Basic验证</a>                </td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">配置文件:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                <textarea name="params" cols="90" rows="16" class="textarea" id="params"
				   width="200"><c:out value='${smweb.params}'/></textarea></td>
              </tr>
              

              <tr>
                <td align="right"  height="30">系统监听器:</td>
                <td height="30">&nbsp;
                <input name="classname" class="textbox2" width="200" 
                  id="classname" value="<c:out value='${smweb.classname}'/>" />
                *实现ConfigListener,或Filter</td>
              </tr>
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">字符集编码:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                <input name="charset" class="textbox" width="200" 
                  id="charset" value="<c:out value='${smweb.charset}'/>" />
                </td>
              </tr>
              <tr>
                <td  height="30" align="right">备注:</td>
                <td height="30">&nbsp;
                <textarea name="memo" cols="90" rows="9" class="textarea" id="memo"
				   width="200"><c:out value='${smweb.memo}'/></textarea></td>
              </tr>
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">修改时间:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;<c:out value='${smweb.createTime}'/> <input name="id" type="hidden" id="id" value="<c:out value='${smweb.id}'/>"/>				  	</td>
              </tr>
              <tr>
                <td  height="23" background="../img/pub/top1.gif" align="right">&nbsp;</td>
                <td height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smweb.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" 
				value="<c:out value='${smweb.dotype}'/>"
				 name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smweb.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>