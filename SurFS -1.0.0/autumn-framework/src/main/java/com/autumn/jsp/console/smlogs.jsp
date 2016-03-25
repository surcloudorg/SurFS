<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>日志管理</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>

<body>
<FORM id=Form1 name=Form1 action=logsystem.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 日志管理</strong></td>
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
              <td width="11%" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smlogs.accessPermission>1}"><a href="logsystem.do?dotype=new">新建日志</a></c:if></td>
                <td width="89%" height="30" align="right" bgcolor="#EFEFEF">
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="logname" <c:if test="${smlogs.fieldName=='logname'}">selected="selected"</c:if>>日志目录</option>
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${smlogs.fieldValue}"/>' size="10"	                   />
                  &nbsp;
                  <input name="Submit"  type="submit" class="bottonbox" value="检索" />
                &nbsp;
				</td>
                
              </tr>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            border="0">
            <tbody>
              <tr>
                <td width="10%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">&nbsp;&nbsp;日志目录</td>
                <td width="20%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">报警类</td>
                <td width="16%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">时间格式</td>
                <td width="30%" align="left" background="../img/pub/top1.gif" class="JiaCu">过滤字符/日志路径</td>
                <td width="9%" align="middle" background="../img/pub/top1.gif" class="JiaCu">日志级别</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">查找</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smlogs.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;&nbsp;<c:out value="${mysrv.logname}"/></td>
                <td height="23" align="left"><c:out value="${mysrv.warnclass}"/></td>
                <td height="23" align="left"><c:out value="${mysrv.dateformatter}"/></td>
                <td align="left"><c:out value="${mysrv.filter}" escapeXml="false"/></td>
                <td align="middle"><c:out value='${mysrv.level}'/></td>
                <td align="middle">              
                <a href="logfind.do?logname=<c:out value='${mysrv.logname}'/>">
                <img src="../img/pub/icoyellow.gif"  width="14" height="14" border="0" /></a></td>
                <td align="middle">
                <a href="logsystem.do?dotype=edit&logname=<c:out value='${mysrv.logname}'/>">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" /></a></td>
                <td align="middle">
                <c:choose>
                <c:when test="${smlogs.accessPermission<=1||mysrv.logname=='system'||mysrv.logname=='error'}">
                  <img src="../img/pub/deletea.gif"  width="15" height="15" border="0" />
                </c:when>
                <c:otherwise>
                <a href="#" onclick=deletelog("<c:out value='${mysrv.logname}'/>")>
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
				 <span
                  id="Pages">
                  
                  共
                    <c:out value="${smlogs.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${smlogs.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${smlogs.pageSize==20}">selected="selected"</c:if>>20</option>
				    <option value="30" <c:if test="${smlogs.pageSize==30}">selected="selected"</c:if>>30</option>
				    <option value="40" <c:if test="${smlogs.pageSize==40}">selected="selected"</c:if>>40</option>
				    <option value="50" <c:if test="${smlogs.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${smlogs.pageNum}"/>/<c:out value="${smlogs.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smlogs.pageNum}'/>-1;document.Form1.submit();">上一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smlogs.pageNum}'/>+1;document.Form1.submit();">下一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smlogs.pageCount}'/>;document.Form1.submit();">尾页</a>] 
				  <c:if test="${smlogs.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${smlogs.pageCount}"> 
				    <option value="<c:out value="${i}"/>"
					<c:if test="${smlogs.pageNum==i}">selected="selected"</c:if>
					><c:out value="${i}"/></option>
				  </c:forEach> 

			      </select>
				  页 </c:if>
                </span>
				</td>
              </tr>
              <tr>
                <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;<span class="redtitle"
                  id="domsg">
                <c:out value="${smlogs.doMsg}"/>
                <input type="hidden" name="logname" id="logname" />
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