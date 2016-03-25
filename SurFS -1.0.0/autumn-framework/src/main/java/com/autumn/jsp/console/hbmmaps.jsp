<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Hibernate映射</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=hbms.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> Hibernate映射</strong></td>
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
                <td width="15%" height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${hbmmap.accessPermission>1}"><a href="hbms.do?dotype=new">新建映射</a></c:if>
                <c:if test="${pathStr!=''&&dirname!=''&&hbmmap.accessPermission>1}">	  
                /<a href="upload.do?dotype=view&forward=hbms.do&pathStr=<c:out value='${pathStr}'/>&dirname=<c:out value='${dirname}'/>">编辑配置文件</a>
                </c:if>
                
                </td>
                <td width="85%" align="right" bgcolor="#EFEFEF"><input name="dotype" type="hidden" id="dotype" />
                  <input name="id" type="hidden" id="id" />
                  <input name="datasource" type="hidden" id="datasource" />
                  
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="id" <c:if test="${hbmmap.fieldName=='id'}">selected="selected"</c:if>>序号ID</option>
                    <option value="classname" <c:if test="${hbmmap.fieldName=='classname'}">selected="selected"</c:if>>类名</option>
					<option value="tablename" <c:if test="${hbmmap.fieldName=='tablename'}">selected="selected"</c:if>>表名</option>
					<option value="catalogname" <c:if test="${hbmmap.fieldName=='catalogname'}">selected="selected"</c:if>>数据库目录</option>
					<option value="datasource" <c:if test="${hbmmap.fieldName=='datasource'}">selected="selected"</c:if>>连接池</option>			 
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${hbmmap.fieldValue}"/>' size="10"	                   />
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
                <td width="5%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">序号ID</td>
                <td width="28%" height="22" align="left" background="../img/pub/top1.gif" class="JiaCu">类名</td>
                <td width="22%" height="22" align="left" background="../img/pub/top1.gif" class="JiaCu">标题</td>
                <td width="11%" align="left" background="../img/pub/top1.gif" class="JiaCu">表名</td>
                <td width="11%" align="left" background="../img/pub/top1.gif" class="JiaCu">连接池</td>
                <td width="11%" align="left" background="../img/pub/top1.gif" class="JiaCu">数据库目录</td>
                <c:if test="${hbmmap.accessPermission>1}">
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">新建</td></c:if>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${hbmmap.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="middle" bgcolor="#EFEFEF"><c:out value="${mysrv.id}"/></td>
                <td height="22" align="left"><c:out value="${mysrv.classname}"/></td>
                <td align="left" height="22"><c:out value="${mysrv.title}"/></td>
                <td align="left"><c:out value="${mysrv.tablename}"/></td>
                <td align="left"><c:out value="${mysrv.datasource}"/></td>
                <td align="left"><c:out value="${mysrv.catalogname}"/></td>
                
                <c:if test="${hbmmap.accessPermission>1}">
                <td align="middle">
				<a href="hbms.do?dotype=new&datasource=<c:out value='${mysrv.datasource}'/>">
				<img src="../img/pub/file.gif"  width="16" height="16" border="0" style="margin-bottom:-3px"/></a>				</td>
                </c:if>
                
                <td align="middle">
				<a href="hbms.do?dotype=edit&id=<c:out value='${mysrv.id}'/>">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" style="margin-bottom:-3px"/></a>	
				</td>
                
                <td align="middle">	
                <c:if test="${hbmmap.accessPermission>1}">	
				<a href="#" onclick=deletemap("${mysrv.id}","${mysrv.datasource}");>
				<img src="../img/pub/delete.gif" width="15" height="15" border="0" style="margin-bottom:-3px"/></a>	</c:if>
                <c:if test="${hbmmap.accessPermission<=1}">	
				<img src="../img/pub/deletea.gif" width="15" height="15" border="0" style="margin-bottom:-3px"/></c:if>
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
                    <c:out value="${hbmmap.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${hbmmap.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${hbmmap.pageSize==20}">selected="selected"</c:if>>20</option>
				    <option value="30" <c:if test="${hbmmap.pageSize==30}">selected="selected"</c:if>>30</option>
				    <option value="40" <c:if test="${hbmmap.pageSize==40}">selected="selected"</c:if>>40</option>
				    <option value="50" <c:if test="${hbmmap.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${hbmmap.pageNum}"/>/<c:out value="${hbmmap.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${hbmmap.pageNum}'/>-1;document.Form1.submit();">上一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${hbmmap.pageNum}'/>+1;document.Form1.submit();">下一页</a>] 
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${hbmmap.pageCount}'/>;document.Form1.submit();">尾页</a>] 
				  <c:if test="${hbmmap.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${hbmmap.pageCount}"> 
				    <option value="<c:out value="${i}"/>"
					<c:if test="${hbmmap.pageNum==i}">selected="selected"</c:if>
					><c:out value="${i}"/></option>
				  </c:forEach> 

			      </select>
				  页 </c:if>
                </span>&nbsp;</td>
              </tr>
              <tr>
                <td height="23" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;<span class="redtitle"
                  id="domsg">
                <c:out value="${hbmmap.doMsg}"/></span></td>
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