<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>查看系统变量</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>

<body>
<FORM id=Form1 name=Form1 action=sysproperties.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 系统 - 查看系统变量</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="23" align="left" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#EFEFEF">
            &nbsp;&nbsp;<a href="system.do">重载类</a> 
            &nbsp;&nbsp;<span class="JiaCu">查看系统变量</span>
            &nbsp;&nbsp;<a href="systhreads.do">激活线程</a>            
            &nbsp;&nbsp;<a href="sysmemory.do">系统信息</a> 
                
                  <input name="path" type="hidden" id="path" />
                  <input name="key" type="hidden" id="key" />
                <input name="dotype" type="hidden" id="dotype" />&nbsp;&nbsp;</td>
              </tr>
            </tbody>
        </table>
	
        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
           >
            <tbody>
              <tr>
                <td height="23" colspan="8" align="left" bgcolor="#EFEFEF">&nbsp;
                <c:out value='${sysproperties.doMsg}'/></td>
              </tr>
              <tr>
                <td width="10%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">命名空间</td>
                <td width="23%"  height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">主健</td>
                <td width="15%" align="left" background="../img/pub/top1.gif" class="JiaCu">设置时间</td>
                <td width="15%" align="left" background="../img/pub/top1.gif" class="JiaCu">到期时间</td>
                <td width="7%" align="left" background="../img/pub/top1.gif" class="JiaCu">是否到期</td>
                <td width="15%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">类名</td>
                <td width="10%" align="middle" background="../img/pub/top1.gif" class="JiaCu">占用内存</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">清除</td>
              </tr>

			  <c:forEach items="${sysproperties.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:out value="${mysrv.path}"/></td>
                <td height="23" align="left" nowrap="nowrap"><c:if test="${mysrv.oldversion}"><span class="shouming"></c:if><c:out value="${mysrv.key}"/><c:if test="${mysrv.oldversion}"></span></c:if></td>
                <td align="left" title="${mysrv.info}">${mysrv.stime}</td>
                <td align="left" title="${mysrv.info}">${mysrv.etime}</td>
                <td align="left" title="${mysrv.info}">${mysrv.timeout}</td>
                <td height="23" align="left" title="${mysrv.info}">${mysrv.classname}</td>
                <td align="middle" nowrap="nowrap"><c:out value="${mysrv.size}"/></td>
                <td align="middle">
                <c:if test="${sysproperties.accessPermission>1}">
				<a href="#" onclick=deleteattribute("<c:out value='${mysrv.path}'/>","<c:out value='${mysrv.key}'/>")><img src="../img/pub/delete.gif" width="15" height="15" border="0" /></a></c:if>
                <c:if test="${sysproperties.accessPermission<=1}">
				<img src="../img/pub/deletea.gif" width="15" height="15" border="0" /></c:if>		</td>
                </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>              
              <tr>
                <td width="10%" height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp; 
                <input name="Submit"  type="submit" class="bottonbox" id="Submit" value="刷新" />
                </td>
                <td width="90%" align="left" valign="middle" background="../img/pub/top1.gif" class="redtitle"></td>
              </tr>
            </tbody>
        </table></td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>