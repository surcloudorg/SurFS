<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SVN在线更新</title>
</head>

<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<script type="text/javascript" src="../js/prototype/v1.6.0.js"></script>
<script type="text/javascript" src="../img/pub/smcode.js"></script>
<!-- onload="setInterval(Ajax_do(),6000)"-->
<body onload="setInterval(Ajax_do,5000);">
<FORM id=Form1 name=Form1 action=svncodes.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> SVN在线更新</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td bgcolor="#999999" colspan="2" height="1"></td>
              </tr>
              <tr>
                <td width="21%" height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smcodes.accessPermission>1}"><a href="svncodes.do?dotype=new">创建工程</a></c:if></td>
                <td width="79%" align="right" bgcolor="#EFEFEF"><input name="dotype" type="hidden" id="dotype" />
                  <input name="id" type="hidden" id="id" />
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="id" <c:if test="${smcodes.fieldName=='id'}">selected="selected"</c:if>>序号ID</option>
                    <option value="title" <c:if test="${smcodes.fieldName=='title'}">selected="selected"</c:if>>标题</option>
                    <option value="url" <c:if test="${smcodes.fieldName=='url'}">selected="selected"</c:if>>SVN地址</option>
                    <option value="webdir" <c:if test="${smcodes.fieldName=='webdir'}">selected="selected"</c:if>>类型：JSP源码</option>
					<option value="srcdir" <c:if test="${smcodes.fieldName=='srcdir'}">selected="selected"</c:if>>类型：JAVA源码</option>        		 
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${smcodes.fieldValue}"/>' size="10"	                   />
                  &nbsp;
                  <input name="Submit"  type="submit" class="bottonbox" value="检索" />
                &nbsp;</td>
              </tr>
            </tbody>
        </table>
        <table style="word-wrap: break-word; word-break: break-all;" width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
           >
            <tbody>
              <tr>
                <td width="5%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">序号ID</td>
                <td width="12%"  height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">标题</td>
                <td width="16%" align="middle" background="../img/pub/top1.gif" class="JiaCu">本地目录</td>
                <td width="37%" align="middle" background="../img/pub/top1.gif" class="JiaCu">SVN地址</td>
                <td width="12%" align="middle" background="../img/pub/top1.gif" class="JiaCu">操作</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">类型</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smcodes.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="middle" bgcolor="#EFEFEF"><c:out value="${mysrv.id}"/></td>
                <td height="23" align="left"><c:out value="${mysrv.title}"/></td>
                <td align="left"><c:out value="${mysrv.dirName}"/></td>
                <td align="left"><c:out value="${mysrv.url}"/></td>
                <td align="middle">
 <span id="content<c:out value='${mysrv.id}'/>">
 				<c:choose> 
				<c:when test="${mysrv.message=='检查更新'}"> 
                   <c:if test="${smcodes.accessPermission>1}">	   
                   <a href="svncodes.do?dotype=check&id=<c:out value='${mysrv.id}'/>">检查更新</a></c:if> 
                   <c:if test="${smcodes.accessPermission<=1}">检查更新</c:if>               </c:when>
                <c:when test="${mysrv.message=='查看状态...'}">    
                   <a href="svncodes.do?dotype=view&id=<c:out value='${mysrv.id}'/>">查看状态...</a>                </c:when>
                <c:otherwise>
                  <span class="redtitle"> <c:out value="${mysrv.message}"/></span>                </c:otherwise>
                </c:choose>                  
</span>                 </td>             
                <td align="middle">
                  <c:choose> 
                    <c:when test="${mysrv.dirType==0}">    
                      JAVA                      </c:when>
                    <c:otherwise>
                      JSP                      </c:otherwise>
                    </c:choose>                </td>
                <td align="middle">		
                    <c:if test="${smcodes.accessPermission>1}">	
                      <a href="#" onclick=deletecode("<c:out value='${mysrv.id}'/>")>
                      <img src="../img/pub/delete.gif" width="15" height="15" border="0" /></a>	</c:if>	
                   <c:if test="${smcodes.accessPermission<=1}">	
                      <img src="../img/pub/deletea.gif" width="15" height="15" border="0" /></c:if>		
                  </td>
                </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              
              <tr>
                <td height="23" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"> <span
                  id="Pages">
                  
                  共
                    <c:out value="${smcodes.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${smcodes.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${smcodes.pageSize==20}">selected="selected"</c:if>>20</option>
				    <option value="30" <c:if test="${smcodes.pageSize==30}">selected="selected"</c:if>>30</option>
				    <option value="40" <c:if test="${smcodes.pageSize==40}">selected="selected"</c:if>>40</option>
				    <option value="50" <c:if test="${smcodes.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${smcodes.pageNum}"/>/<c:out value="${smcodes.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smcodes.pageNum}'/>-1;document.Form1.submit();">上一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smcodes.pageNum}'/>+1;document.Form1.submit();">下一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smcodes.pageCount}'/>;document.Form1.submit();">尾页</a>] 
				  <c:if test="${smcodes.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${smcodes.pageCount}"> 
				    <option value="<c:out value="${i}"/>"
					<c:if test="${smcodes.pageNum==i}">selected="selected"</c:if>
					><c:out value="${i}"/></option>
				  </c:forEach> 

			      </select>
				  页 </c:if>
                </span>&nbsp;</td>
              </tr>
              <tr>
                <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;<span class="redtitle"
                  id="domsg">
                <c:out value="${smcodes.doMsg}"/></span></td>
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