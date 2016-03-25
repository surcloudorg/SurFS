<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>数据库连接池</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>

<body>
<FORM id=Form1 name=Form1 action=dbpools.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 数据库连接池</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="1" colspan="2" bgcolor="#999999"></td>
              </tr>
              <tr>
              <td width="11%" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smlogs.accessPermission>1}"><a href="dbpools.do?dotype=new">新建连接池</a></c:if></td>
                <td width="89%" height="30" align="right" bgcolor="#EFEFEF">
               
				</td>
                
              </tr>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            border="0">
            <tbody>
              <tr>
                <td width="14%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">&nbsp;&nbsp;连接池名</td>
                <td width="20%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">驱动</td>
                <td width="35%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">地址端口</td>
                <td width="8%" align="left" background="../img/pub/top1.gif" class="JiaCu">占用连接</td>
                <td width="8%" align="middle" background="../img/pub/top1.gif" class="JiaCu">连接总数</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">详情</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smlogs.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;&nbsp;
                <c:if test="${smlogs.accessPermission>1}">
                <a href="sqltest.do?jndiname=${mysrv.jndiname}">${mysrv.jndiname}</a></c:if>
                <c:if test="${smlogs.accessPermission<=1}">${mysrv.jndiname}</c:if>
                </td>
                <td height="23" align="left">${mysrv.driver}</td>
                <td height="23" align="left"><c:out value="${mysrv.dburl}" escapeXml="false"/></td>
                <td align="left">${mysrv.ds.useConCount}</td>
                <td align="middle">${mysrv.ds.connectionCount}</td>
                <td align="middle">              
                <a href="dbpools.do?dotype=view&jndiname=${mysrv.jndiname}">
                <img src="../img/pub/icoyellow.gif"  width="14" height="14" border="0" /></a></td>
                <td align="middle">
                <a href="dbpools.do?dotype=edit&jndiname=${mysrv.jndiname}">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" /></a></td>
                <td align="middle">
                <c:choose>
                <c:when test="${smlogs.accessPermission<=1||mysrv.jndiname=='SystemSource'}">
                  <img src="../img/pub/deletea.gif"  width="15" height="15" border="0" />
                </c:when>
                <c:otherwise>
                <a href="#" onclick=deletedbpool("${mysrv.jndiname}")>
				<img src="../img/pub/delete.gif"  width="15" height="15" border="0" /></a>
                </c:otherwise>
                </c:choose>
				</td>
                </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="23" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">                
                  共
                    <c:out value="${smlogs.rowCount}"/>条记录</td>
              </tr>
              <tr>
                <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;<span class="redtitle"
                  id="domsg">
                <c:out value="${smlogs.doMsg}"/>
                <input type="hidden" name="jndiname" id="jndiname" />
                <input type="hidden" name="dotype" id="dotype" />
                </span></td>
              </tr>
              <tr>
                <td bgcolor="#999999" colspan="1" height="1"></td>
              </tr>
            </tbody>
        </table></td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>