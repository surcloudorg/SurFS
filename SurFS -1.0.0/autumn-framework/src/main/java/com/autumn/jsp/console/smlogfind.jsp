<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>日志查找</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/wdatepicker/wdatepicker.js"></script>
<body>
<FORM id=Form1 name=Form1 action=logfind.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 日志管理 - 日志查找</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>

          <tr>
            <td height="23" align="left" valign="middle" background="../img/pub/top1.gif" bgcolor="#cccccc" class="JiaCu">&nbsp;日志目录：
              <c:out value="${smlogfind.logname}"/></td>
          </tr>
          <tr>
            <td height="30" align="left" valign="middle" bgcolor="#EEEEEE">&nbsp;日志日期:
			  <input name="datestr" type="text" class="textbox1" id="datestr" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="<c:out value='${smlogfind.datestr}'/>" size="12" readonly="readonly" />
              &nbsp;
		
                <c:choose>
				<c:when test="${smlogfind.showWarnline}">
<input type="checkbox" name="showWarnline" id="showWarnline"  value="true" checked="checked" onclick="document.Form1.findKey.disabled=!document.Form1.findKey.disabled;document.Form1.lineNumber.value='1000000'" />
              				</c:when>
				<c:otherwise>
<input type="checkbox" name="showWarnline" id="showWarnline" value="true" onclick="document.Form1.findKey.disabled=!document.Form1.findKey.disabled;document.Form1.lineNumber.value='1000000'" />
              				</c:otherwise>
				</c:choose> 
               <a href="#" onclick="document.Form1.showWarnline.checked=!document.Form1.showWarnline.checked;document.Form1.findKey.disabled=!document.Form1.findKey.disabled;document.Form1.lineNumber.value='1000000'">搜索告警日志</a>
<input name="logname" type="hidden" id="srvname" value="<c:out value='${smlogfind.logname}'/>"/>
              </td>
            </tr>
          <tr>
            <td height="30" align="left" valign="middle" bgcolor="#EEEEEE">&nbsp;搜索方向:
				<c:choose> 
				<c:when test="${smlogfind.direct==0}">
              <input type="radio" name="direct" id="direct1" checked="checked"  value="0" />
				</c:when>
				<c:otherwise>
			  <input type="radio" name="direct" id="direct1" value="0" />
				</c:otherwise>
				</c:choose> 
              <a href="#" onclick="document.getElementById('direct1').checked=!document.getElementById('direct1').checked">向上</a>
				<c:choose> 
				<c:when test="${smlogfind.direct==1}">
			  <input type="radio" name="direct" id="direct2" checked="checked" value="1" />
				</c:when>
				<c:otherwise>
			  <input type="radio" name="direct" id="direct2"  value="1" />
				</c:otherwise>
				</c:choose> 
              <a href="#" onclick="document.getElementById('direct2').checked=!document.getElementById('direct2').checked">向下</a></td>
          </tr>
          <tr>
            <td height="30" align="left" valign="middle" bgcolor="#EEEEEE">&nbsp;显示行数:
              <select name="stepSize" class="textbox1">
                <option value="100" <c:if test="${smlogfind.stepSize==100}">selected="selected"</c:if>>100</option>
                <option value="200" <c:if test="${smlogfind.stepSize==200}">selected="selected"</c:if>>200</option>
                <option value="300" <c:if test="${smlogfind.stepSize==300}">selected="selected"</c:if>>300</option>
                <option value="400" <c:if test="${smlogfind.stepSize==400}">selected="selected"</c:if>>400</option>
                <option value="500" <c:if test="${smlogfind.stepSize==500}">selected="selected"</c:if>>500</option>
              </select>&nbsp;
              <label></label><label>&nbsp;搜索的起始行位置:
<input class="textbox1" id="lineNumber" size="10" name="lineNumber"
				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
 value="<c:out value='${smlogfind.lineNumber}'/>" />
              /<c:out value='${smlogfind.lineCount}'/>行</label></td>
          </tr>
          <tr>
            <td height="30" align="left" valign="middle" bgcolor="#EEEEEE">&nbsp;搜索内容:
              <input name="findKey" type="text" <c:if test="${smlogfind.showWarnline}">disabled="disabled"</c:if> class="textbox2" title="不能小于2字符，否则显示全部" value="${smlogfind.findKey}"/>
              <input name="Submit"  type="submit" class="bottonbox" value="搜索" /> 
              <input name="Submit2" type="button" class="bottonbox" value="清空" onclick="document.Form1.findKey.value=''"/>
              <input name="Submit3" type="submit" class="bottonbox" value="返回" onclick="document.Form1.action='logsystem.do'" /></td>
          </tr>
        </tbody>
      </table>
	  
	  <table cellspacing="0" cellpadding="0" width="100%" align="center" bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="30" colspan="2" align="left" valign="middle">
                
                <table style="word-wrap: break-word; word-break: break-all;" width="100%" border="0" cellpadding="0" cellspacing="5" bordercolor="#EEEEEE">
                  <tbody>
                    <tr>
                      <td class="msg" width="100%" height="50" valign="top" bordercolor="#999999" bgcolor="#FFFFFF">
					 <c:forEach items="${smlogfind.findResult}" var="result"> 
					 <c:out value='${result}'/> <br/>
					 </c:forEach>					  </td>
                    </tr>
                  </tbody>
                </table>
                
                </td>
              </tr>
              <tr>
                <td width="99%" height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;
                <c:out value='${smlogfind.filename}'/><c:out value='${fileinfo}'/></td>
                <td width="1%" align="left" valign="left" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;</td>
              </tr>
            </tbody>
        </table>
</td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>