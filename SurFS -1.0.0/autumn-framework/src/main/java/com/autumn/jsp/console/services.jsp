<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>服务管理</title>
</head>

<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/prototype/v1.6.0.js"></script>
<script type="text/javascript" src="../img/pub/smsrv.js"></script>

<body>
<FORM id=Form1 name=Form1 action=services.do method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 服务管理</strong></td>
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
                <td width="17%" height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;<c:if test="${smsrvs.accessPermission>1}"><a href="services.do?dotype=new">配置新服务</a></c:if></td>
                <td width="83%" align="right" bgcolor="#EFEFEF"><input name="dotype" type="hidden" id="dotype" />
                  <input name="id" type="hidden" id="id" />
                  查询字段
                  <select name="fieldName" class="textbox" id="fieldName" onchange="fieldValue.value=''">
				    <option value="" selected="selected">查找全部记录</option>
                    <option value="id" <c:if test="${smsrvs.fieldName=='id'}">selected="selected"</c:if>>服务ID</option>
                    <option value="title" <c:if test="${smsrvs.fieldName=='title'}">selected="selected"</c:if>>服务名称</option>
                    <option value="classname" <c:if test="${smsrvs.fieldName=='classname'}">selected="selected"</c:if>>类名</option>
                  </select>
                  查询值
                  <input name="fieldValue" class="textbox" id="fieldValue" value='<c:out value="${smsrvs.fieldValue}"/>' size="10"	                   />
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
                <td width="8%" height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">服务ID</td>
                <td width="24%" height="22" align="left" background="../img/pub/top1.gif" class="JiaCu">&nbsp;&nbsp;服务名称</td>
                <td width="11%" height="22" align="left" background="../img/pub/top1.gif" class="JiaCu">日志目录</td>
                <td width="30%" align="left" background="../img/pub/top1.gif" class="JiaCu">加载类</td>
                <td width="10%" align="middle" background="../img/pub/top1.gif" class="JiaCu">当前状态</td>
                <td width="8%" align="middle" background="../img/pub/top1.gif" class="JiaCu">设置</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">编辑</td>
                <td width="4%" align="middle" background="../img/pub/top1.gif" class="JiaCu">删除</td>
              </tr>

			  <c:forEach items="${smsrvs.rows}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="middle" bgcolor="#EFEFEF"><c:out value="${mysrv.id}"/></td>
                <td align="left"><img id="imgname${mysrv.id}" src="../img/pub/${mysrv.imgname}.gif"  width="16" height="16" border="0" style="margin-bottom:-3px"/><c:out value="${mysrv.title}"/></td>
                <td align="left"><c:out value="${mysrv.logname}"/></td>
                <td align="left"><c:out value="${mysrv.classname}"/></td>
                <td align="middle">
				<c:choose>
				<c:when test="${mysrv.statuMsg=='关闭服务'||mysrv.statuMsg=='启动服务'}">
                	<c:if test="${smsrvs.accessPermission>1}">
					<a href="#" onclick="Ajax_doSrv(<c:out value='${mysrv.id}'/>);">
                    <span id="dolinkmsg<c:out value='${mysrv.id}'/>"><c:out value='${mysrv.statuMsg}'/></span>
                    </a></c:if>
                    <c:if test="${smsrvs.accessPermission<=1}"><c:out value='${mysrv.statuMsg}'/></c:if>
				</c:when>
				<c:otherwise>
				  <strike><c:out value='${mysrv.statuMsg}'/></strike>
                                </c:otherwise>
				</c:choose>      			</td>

                <td align="middle">
         <c:if test="${!empty mysrv.serviceConfig}">
      
                <a href="services.do?dotype=getproperty&id=<c:out value='${mysrv.id}'/>">属性</a>
                |
				<a href="services.do?dotype=getmethod&id=<c:out value='${mysrv.id}'/>">方法</a>
           </c:if>
				</td>
                <td align="middle">
				<a href="services.do?dotype=edit&id=<c:out value='${mysrv.id}'/>">
				<img src="../img/pub/edit.gif"  width="15" height="15" border="0" /></a>
                </td>
                <td align="middle">
                <c:if test="${smsrvs.accessPermission>1}">
				<a href="#" onclick="deleteconfirm(<c:out value='${mysrv.id}'/>);">
				<img src="../img/pub/delete.gif" width="15" height="15" border="0" /></a></c:if>     
                <c:if test="${smsrvs.accessPermission<=1}"><img src="../img/pub/deletea.gif"  width="15" height="15" border="0" /></c:if>	
				</td>
              </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td height="23" align="center" nowrap="nowrap" background="../img/pub/top1.gif"> 
				<span  id="Pages">
                  共
                    <c:out value="${smsrvs.rowCount}"/>条记录，
				  <select name="pageSize" class="textbox1" id="pageSize" onchange="document.Form1.submit();">
				    <option value="10" <c:if test="${smsrvs.pageSize==10}">selected="selected"</c:if>>10</option>
				    <option value="20" <c:if test="${smsrvs.pageSize==20}">selected="selected"</c:if>>20</option>
				    <option value="30" <c:if test="${smsrvs.pageSize==30}">selected="selected"</c:if>>30</option>
				    <option value="40" <c:if test="${smsrvs.pageSize==40}">selected="selected"</c:if>>40</option>
				    <option value="50" <c:if test="${smsrvs.pageSize==50}">selected="selected"</c:if>>50</option>
			      </select>
				  条/页，
				  当前<c:out value="${smsrvs.pageNum}"/>/<c:out value="${smsrvs.pageCount}"/>页，
				  [<a href="#" onclick="document.Form1.pageNum.value='1';document.Form1.submit();">首页</a>]
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smsrvs.pageNum}'/>-1;document.Form1.submit();">上一页</a>]
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smsrvs.pageNum}'/>+1;document.Form1.submit();">下一页</a>]
				  [<a href="#" onclick="document.Form1.pageNum.value=<c:out value='${smsrvs.pageCount}'/>;document.Form1.submit();">尾页</a>]
				  <c:if test="${smsrvs.pageCount>0}">
				  跳至<select name="pageNum" class="textbox1" id="pageNum" onchange="document.Form1.submit();">
				  <c:forEach var="i" begin="1" end="${smsrvs.pageCount}">
				    <option value="<c:out value="${i}"/>"
					<c:if test="${smsrvs.pageNum==i}">selected="selected"</c:if>
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
                <c:out value="${smsrvs.doMsg}"/></span></td>
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