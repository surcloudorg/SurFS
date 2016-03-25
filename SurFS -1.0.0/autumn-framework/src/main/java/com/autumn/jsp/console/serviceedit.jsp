<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>服务编辑</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/prototype/v1.6.0.js"></script>
<script type="text/javascript" src="../img/pub/smsrv.js"></script>
<body>
<FORM id=Form1 name=Form1 action=services.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14"
                  src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 服务管理 -
          <c:out value='${smsrv.dotype}'/></span></td>
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
                <td width="15%" height="30" align="right">服务名:</td>
                <td height="30">&nbsp;
                <input name="title" class="textbox2" width="200"
                  id="title" value="<c:out value='${smsrv.title}'/>" />
                <input name="id" type="hidden" id="id" value="<c:out value='${smsrv.id}'/>" /></td>
              </tr>
              <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">类名:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                  <input name="classname" class="textbox2" width="200"
                  id="classname" value="<c:out value='${smsrv.classname}'/>" /></td>
              </tr>
              <tr>
                <td align="right" height="30">日志目录:</td>
                <td height="30">&nbsp;
                <select name="logname" class="textbox" id="logname">
                <c:forEach items="${smsrv.dirlist}" var="mapItem"> 
                    <option value="<c:out value='${mapItem}'/>" 
					<c:if test='${smsrv.logname==mapItem}'>
					selected="selected" </c:if>><c:out value='${mapItem}'/> </option>
                 </c:forEach>    
                </select>                </td>
              </tr>
              <tr>
                <td align="right"  bgcolor="#f8f8f8" height="30">启动类型:</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
				<c:choose>
				<c:when test='${smsrv.status==0}'>
                  <input name="status" type="radio" value="0" id="radio1" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input name="status" type="radio" value="0" id="radio1" />
				</c:otherwise>
				</c:choose>
                  <a href="#" onclick="document.getElementById('radio1').checked=!document.getElementById('radio1').checked">
				  随服务器启动</a>
				<c:choose>
				<c:when test='${smsrv.status==1}'>
                  <input type="radio" name="status" value="1"  id="radio2" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input type="radio" name="status" value="1"  id="radio2" />
				</c:otherwise>
				</c:choose>
                  <a href="#" onclick="document.getElementById('radio2').checked=!document.getElementById('radio2').checked">手动启动</a>
				<c:choose>
				<c:when test='${smsrv.status==2}'>
                  <input type="radio" name="status" value="2"  id="radio3" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input type="radio" name="status" value="2"  id="radio3"  />
				</c:otherwise>
				</c:choose>
                  <a href="#" onclick="document.getElementById('radio3').checked=!document.getElementById('radio3').checked">禁止启动</a></td>
              </tr>


              <tr>
                <td align="right" height="267">配置:</td>
                <td height="267">&nbsp;
                <textarea name="params" cols="90" rows="20" class="textarea" id="params"
                   width="400"><c:out value='${smsrv.params}'/></textarea></td>
              </tr>

              <tr>
                <td  height="53" align="right" bgcolor="#f8f8f8">备注: </td>
                <td height="53" bgcolor="#f8f8f8"> &nbsp;
<textarea name="memo" cols="90" rows="5" class="textarea" id="memo" width="400"><c:out value='${smsrv.memo}'/></textarea></td>
              </tr>

              <tr>
                <td height="30" align="right">最后一次修改时间:</td>
                <td height="30">&nbsp; <c:out value='${smsrv.createtime}'/></td>
              </tr>
              <tr>
                <td height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smsrv.accessPermission>1}">
                <input class="bottonbox" id="submit" onclick="return(checkeditsrv())" type="submit" value="${smsrv.dotype}" name="dotype" />
                </c:if>  
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smsrv.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>