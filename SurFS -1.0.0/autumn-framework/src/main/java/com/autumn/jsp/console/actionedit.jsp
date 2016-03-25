<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Beans控制</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=actions.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> Beans控制 - <c:out value='${smaction.dotype}'/></span></td>
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
                <td align="right" width="15%" height="30">ActionID:</td>
                <td width="85%" height="30">&nbsp;
                    <input name="actionId" class="textbox2" width="200" 				
                  id="srvId" value="<c:out value='${smaction.actionId}'/>" />
                    *可以是正则表达式</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">类名称:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                <input name="className" class="textbox2" width="200"
                  id="titleName" value="<c:out value='${smaction.className}'/>" />
                *执行函数的类名
                <input name="id" type="hidden" id="id" value="<c:out value='${smaction.id}'/>"/>
                <input name="dirName" type="hidden" id="dirName" value="<c:out value='${smaction.dirName}'/>"/></td>
              </tr>
              <tr>
                <td align="right" height="30">函数名:</td>
                <td height="30">&nbsp;
                  <input name="subdir" class="textbox2" width="200" 
                  id="subdir" value="<c:out value='${smaction.subdir}'/>" />
                  *不填默认为execute函数</td>
              </tr>
              

              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">Web目录:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;

                  <select name="dirId" class="textbox" id="dirName" >
                    <c:forEach items="${smaction.dirList}" var="mapItem"> <option  value="<c:out value='${mapItem.key}'/>" 	
                        <c:if test='${smaction.dirId==mapItem.key}'>
					selected="selected" </c:if>
                      >
                      <c:out value='${mapItem.value}'/>
                        </option>
                    </c:forEach>
                  </select>
                  *例：smconsole/smaction.do,smconsole为目录，				  smaction.do为ActionID</td>
              </tr>
              <tr>
                <td align="right" height="30">权限控制位: </td>
                <td height="30">&nbsp; <input name="permissionOrder" class="textbox" width="200" 
                  id="permissionOrder" value="<c:out value='${smaction.permissionOrder}'/>" />
                  *例：0表示由loginuser.permission的第<span class="shouming">1</span>个字符控制该action权限</td>
              </tr>
              <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">显示菜单:  </td>
                <td height="30" bgcolor="#f8f8f8">&nbsp; <input name="menu" class="textbox" width="200" 
                  id="menu" value="<c:out value='${smaction.menu}'/>" />
                *例：一级菜单.二级菜单.三级菜单...(NA不显示)</td>
              </tr>
             
              <tr>
                <td height="90" align="right">配置:</td>
                <td height="90">&nbsp; <textarea name="params" cols="90" rows="18" class="textarea"><c:out value='${smaction.params}'/></textarea></td>
              </tr>
              <tr>
                <td height="32" align="right" bgcolor="#f8f8f8">修改/创建时间: </td>
                <td height="32" bgcolor="#f8f8f8">&nbsp; <c:out value='${smaction.createTime}'/></td>
              </tr>
              <tr>
                <td height="90" align="right">备注:</td>
                <td height="90">&nbsp; <textarea name="memo" cols="90" rows="5" class="textarea"><c:out value='${smaction.memo}'/></textarea></td>
              </tr>
              <tr>
                <td height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smaction.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" 
				value="<c:out value='${smaction.dotype}'/>"
				 name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smaction.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>