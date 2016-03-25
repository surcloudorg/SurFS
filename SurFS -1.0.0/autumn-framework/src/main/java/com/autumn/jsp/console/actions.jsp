<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Beans控制</title>
</head>

<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>

<body>
<FORM id=Form1 name=Form1 action=actions.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> Beans控制</strong></td>
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
                <td width="6%" height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smactions.accessPermission>1}"><a href="actions.do?dotype=new">新建类控制</a></c:if></td>
                <td width="94%" align="right" bgcolor="#EFEFEF"><input name="dotype" type="hidden" id="dotype" />
                  <input name="id" type="hidden" id="id" />
                  <input name="dirName" type="hidden" id="dirName" />
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="id" <c:if test="${smactions.fieldName=='id'}">selected="selected"</c:if>>序号ID</option>
                    <option value="actionId" <c:if test="${smactions.fieldName=='actionId'}">selected="selected"</c:if>>ActionId</option>
					<option value="dirId" <c:if test="${smactions.fieldName=='dirId'}">selected="selected"</c:if>>目录ID</option>
					<option value="dirName" <c:if test="${smactions.fieldName=='dirName'}">selected="selected"</c:if>>目录名</option>
					<option value="className" <c:if test="${smactions.fieldName=='className'}">selected="selected"</c:if>>类名</option>
					<option value="menu" <c:if test="${smactions.fieldName=='menu'}">selected="selected"</c:if>>显示菜单</option>
                    <option value="subdir" <c:if test="${smactions.fieldName=='subdir'}">selected="selected"</c:if>>函数名</option>				 
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${smactions.fieldValue}"/>' size="10"	                   />
                  &nbsp;
                  <input name="Submit"  type="submit" class="bottonbox" value="检索" />
                &nbsp;</td>
              </tr>
            </tbody>
        </table>
		
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            border="0">
            <tbody>
              <tr>
                <td width="7%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">序号ID</td>
                <td width="9%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">ActionID</td>
                <td width="30%" align="left" background="../img/pub/top1.gif" class="JiaCu">类名</td>
                <td width="14%" align="left" background="../img/pub/top1.gif" class="JiaCu">显示菜单</td>
                <td width="10%" align="left" background="../img/pub/top1.gif" class="JiaCu">目录名</td>
                <td width="10%" align="middle" background="../img/pub/top1.gif" class="JiaCu">函数名</td>
                <td width="6%" align="middle" background="../img/pub/top1.gif" class="JiaCu">权限位</td>
                <td width="5%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="9%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smactions.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="middle" bgcolor="#EFEFEF"><c:out value="${mysrv.id}"/></td>
                <td height="23" align="left"><c:out value="${mysrv.actionId}"/></td>
                <td align="left">&nbsp;<c:out value="${mysrv.className}"/></td>
                <td align="left"><c:out value="${mysrv.menu}"/></td>
                <td align="left"><c:out value="${mysrv.dirName}"/></td>
                <td align="left"><c:out value="${mysrv.subdir}"/></td>
                <td align="middle"><c:out value="${mysrv.permissionOrder}"/></td>
                <td align="middle">
				<a href="actions.do?dotype=edit&id=<c:out value='${mysrv.id}'/>">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" /></a>
				</td>
                <td align="middle">		
                <c:if test="${smactions.accessPermission>1}">
				<a href="#" onclick=deleteaction("<c:out value='${mysrv.id}'/>","<c:out value='${mysrv.dirName}'/>");>
				<img src="../img/pub/delete.gif" width="15" height="15" border="0" /></a></c:if>
                <c:if test="${smactions.accessPermission<=1}">
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
                <td height="23" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">
				<span
                  id="Pages">
                  
                  共
                    <c:out value="${smactions.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${smactions.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${smactions.pageSize==20}">selected="selected"</c:if>>20</option>
				    <option value="30" <c:if test="${smactions.pageSize==30}">selected="selected"</c:if>>30</option>
				    <option value="40" <c:if test="${smactions.pageSize==40}">selected="selected"</c:if>>40</option>
				    <option value="50" <c:if test="${smactions.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${smactions.pageNum}"/>/<c:out value="${smactions.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smactions.pageNum}'/>-1;document.Form1.submit();">上一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smactions.pageNum}'/>+1;document.Form1.submit();">下一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smactions.pageCount}'/>;document.Form1.submit();">尾页</a>] 
				  <c:if test="${smactions.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${smactions.pageCount}"> 
				    <option value="<c:out value="${i}"/>"
					<c:if test="${smactions.pageNum==i}">selected="selected"</c:if>
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
                <c:out value="${smactions.doMsg}"/></span></td>
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