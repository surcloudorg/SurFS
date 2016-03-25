<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改配置</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=logsystem.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14"
                  src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 日志管理 - <c:out value="${smlog.dotype}"/></span></td>
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
                <td align="right" width="15%" height="30">目录名称:</td>
                
                <td width="85%" height="30">&nbsp;
                <c:choose>
                <c:when test="${smlog.dotype=='修改配置'}">
                  <input name="logname" class="textbox" width="200" readonly="true"
                  id="logname" value="<c:out value='${smlog.logname}'/>" />
                *不允许修改                </c:when>
                <c:otherwise>
                  <input name="logname" class="textbox" width="200" 
                  id="logname" value="<c:out value='${smlog.logname}'/>" />
                </c:otherwise>
                </c:choose>                           </td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">日志级别:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <select name="level" class="textbox">
                    <option value="DEBUG"

                    <c:if test="${smlog.level=='DEBUG'}">selected="selected"</c:if>
                    >DEBUG
                    </option>
                    <option value="INFO"
                    <c:if test="${smlog.level=='INFO'}">selected="selected"</c:if>
                    >INFO
                    </option>
                    <option value="WARN"
                    <c:if test="${smlog.level=='WARN'}">selected="selected"</c:if>
                    >WARN
                    </option>
                    <option value="ERROR"
                    <c:if test="${smlog.level=='ERROR'}">selected="selected"</c:if>
                    >ERROR
                    </option>
                    <option value="FATAL"
                    <c:if test="${smlog.level=='FATAL'}">selected="selected"</c:if>
                    >FATAL
                    </option>
                  </select>
*只有在需要告警时使用最高级别日志Fatal			    </td>
              </tr>
              <tr>
                <td align="right" height="30">报警类名:</td>
                <td height="30"> &nbsp; <input name="warnclass" class="textbox2" width="200"
                  id="warnclass" value="<c:out value='${smlog.warnclass}'/>" />
*实现com.autumn.core.log.WarnImpl接口</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">日志时间格式:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="dateformatter" class="textbox" width="200"
                  id="hostIp" value="<c:out value='${smlog.dateformatter}'/>" />
*如MM-dd HH:mm:ss</td>
              </tr>
              <tr>
                <td align="right" height="30">&nbsp;</td>
                <td height="30">&nbsp;
				<c:choose>
				<c:when test="${smlog.outconsole==true}">
			<input name="outconsole" type="checkbox" id="outconsole" value="1" checked='checked' />
				</c:when>
				<c:otherwise>
			<input name="outconsole" type="checkbox" id="outconsole" value="1" />
				</c:otherwise>
				</c:choose>
				<a href="#" onclick="document.getElementById('outconsole').checked=!document.getElementById('outconsole').checked">在控制台输出日志信息</a></td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8"  height="30">&nbsp;</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
				<c:choose>
				<c:when test="${smlog.addclassname==true}">
		<input name="addclassname" type="checkbox" id="addclassname" value="1" checked='checked'/>
				</c:when>
				<c:otherwise>
		<input name="addclassname" type="checkbox" id="addclassname" value="1" />
				</c:otherwise>
				</c:choose>
		<a href="#" onclick="document.getElementById('addclassname').checked=!document.getElementById('addclassname').checked">为日志添加类名</a></td>
              </tr>
              <tr>
                <td align="right" height="30">&nbsp;</td>
                <td height="30">&nbsp;
				<c:choose>
				<c:when test="${smlog.addlevel==true}">
		<input name="addlevel" type="checkbox" id="addlevel" value="1" checked='checked'/>
				</c:when>
				<c:otherwise>
		<input name="addlevel" type="checkbox" id="addlevel" value="1" />
				</c:otherwise>
				</c:choose>
				<a href="#" onclick="document.getElementById('addlevel').checked=!document.getElementById('addlevel').checked">为日志添加输出级别</a></td>
              </tr>
              <tr>
                <td align="right"  bgcolor="#f8f8f8" height="30">过滤字符:</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="filter" class="textbox2" width="200"
                  id="filter" value="<c:out value='${smlog.filter}'  escapeXml='false'/>" />
*正则表达式，符合条件一定输出</td>
              </tr>
   
                   <tr>
                <td height="30" align="right">报警间隔: </td>
                <td height="30">&nbsp;
                <input name="warninteral" class="textbox" width="200"
                				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="warninteral" value="<c:out value='${smlog.warninteral}'/>" />
                *（S)如果=0持续报警</td>
              </tr>

                   <tr>
                     <td  height="23" align="right" bgcolor="#f8f8f8"><p>配置：</p>
                     </td>
                     <td height="23" bgcolor="#f8f8f8">&nbsp;
                     
                     <textarea name="params" id="params" cols="80" class="textarea" rows="8"><c:out value='${smlog.params}'/></textarea></td>
              </tr>
              <tr>
                <td align="right" background="../img/pub/top1.gif"  height="23">&nbsp;</td>
                <td  background="../img/pub/top1.gif" height="23">&nbsp;
                <c:if test="${smlog.accessPermission>1&&smlog.logname!='error'}"><input class="bottonbox" id="submit" type="submit"
				value="<c:out value='${smlog.dotype}'/>"  name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smlog.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>