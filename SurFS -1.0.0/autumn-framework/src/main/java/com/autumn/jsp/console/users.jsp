<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>账号管理</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>

<body>
<FORM id=Form1 name=Form1 action=users.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 账号管理</strong></td>
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
                <td width="4%" height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smusers.accessPermission>1}"><a href="users.do?dotype=new">添加新账号</a></c:if></td>
                <td width="96%" align="right" bgcolor="#EFEFEF"><input name="dotype" type="hidden" id="dotype" />
                  <input name="id" type="hidden" id="id" />
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="id" <c:if test="${smusers.fieldName=='id'}">selected="selected"</c:if>>账号ID</option>
                    <option value="userName" <c:if test="${smusers.fieldName=='userName'}">selected="selected"</c:if>>用户名</option>
                    <option value="realname" <c:if test="${smusers.fieldName=='realname'}">selected="selected"</c:if>>姓名</option>	
					<option value="mobile" <c:if test="${smusers.fieldName=='mobile'}">selected="selected"</c:if>>手机号</option>
					<option value="dirname" <c:if test="${smusers.fieldName=='dirname'}">selected="selected"</c:if>>登录路径</option>
					<option value="active" <c:if test="${smusers.fieldName=='active'}">selected="selected"</c:if>>激活账户</option>			
					<option value="noactive" <c:if test="${smusers.fieldName=='noactive'}">selected="selected"</c:if>>冻结账户</option>		 
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${smusers.fieldValue}"/>' size="10"	                   />
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
                <td width="8%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">账号ID</td>
                <td width="17%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">用户名</td>
                <td width="20%" height="23" align="left" background="../img/pub/top1.gif" class="JiaCu">姓名</td>
                <td width="27%" align="left" background="../img/pub/top1.gif" class="JiaCu">路径</td>
                <td width="8%" align="middle" background="../img/pub/top1.gif" class="JiaCu">激活状态</td>
                <td width="12%" align="left" background="../img/pub/top1.gif" class="JiaCu">权限</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smusers.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="middle" bgcolor="#EFEFEF"><c:out value="${mysrv.id}"/></td>
                <td height="23" align="left"><c:out value="${mysrv.userName}"/></td>
                <td align="left" height="23"><c:out value="${mysrv.realname}"/></td>
                <td align="left"><c:out value="${mysrv.loginPath}"/></td>
                <td align="middle">
				<c:choose> 
				<c:when test="${mysrv.isActive==true}">
				   	激活				</c:when> 
				<c:otherwise>
				    冻结				</c:otherwise>
				</c:choose>      			</td>
                <td align="left"><c:out value="${mysrv.permission}"/></td>
                <td align="middle">
				<a href="users.do?dotype=edit&id=<c:out value='${mysrv.id}'/>">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" /></a>				</td>
                <td align="middle">	
                <c:if test="${smusers.accessPermission>1}">		
				<a href="#" onclick="deleteuser(<c:out value='${mysrv.id}'/>);">
				<img src="../img/pub/delete.gif" width="15" height="15" border="0" /></a></c:if>
                <c:if test="${smusers.accessPermission<=1}">		
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
                    <c:out value="${smusers.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${smusers.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${smusers.pageSize==20}">selected="selected"</c:if>>20</option>				  
				    <option value="30" <c:if test="${smusers.pageSize==30}">selected="selected"</c:if>>30</option>
					<option value="40" <c:if test="${smusers.pageSize==40}">selected="selected"</c:if>>40</option>
					<option value="50" <c:if test="${smusers.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${smusers.pageNum}"/>/<c:out value="${smusers.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smusers.pageNum}'/>-1;document.Form1.submit();">上一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smusers.pageNum}'/>+1;document.Form1.submit();">下一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smusers.pageCount}'/>;document.Form1.submit();">尾页</a>] 
				  <c:if test="${smusers.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${smusers.pageCount}"> 
				    <option value="<c:out value="${i}"/>"
					<c:if test="${smusers.pageNum==i}">selected="selected"</c:if>
					><c:out value="${i}"/></option>
				  </c:forEach> 

			      </select>
				  页 </c:if>
                </span>&nbsp;</td>
              </tr>
              <tr>
                <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;<span class="redtitle"
                  id="domsg">
                <c:out value="${smusers.doMsg}"/></span></td>
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