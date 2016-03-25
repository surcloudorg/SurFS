<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SOAP服务管理</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=soaps.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14"
                  src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> <strong>SOAP服务管理</strong> -
          <c:out value='${smsoap.dotype}'/></span></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table id="Table1" cellspacing="0" cellpadding="0" width="100%"
            align="center" bgcolor="#f1f1f1" border="0">
            <tbody>

              <tr>
                <td bgcolor="#999999" colspan="3" height="1"></td>
              </tr>
              <tr>
                <td align="right" width="20%" height="30">标题:</td>
                <td height="30" colspan="2">&nbsp;
                    <input name="title" class="textbox2" width="200"
                  id="srvId" value="<c:out value='${smsoap.title}'/>" />
                    *注明该服务的主要功能</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">服务名:</td>
                <td height="30" colspan="2" bgcolor="#f8f8f8">&nbsp;
                <input name="servicename" class="textbox2" width="200"
                  id="servicename" value="<c:out value='${smsoap.servicename}'/>" />
                *客户端访问的服务名</td>
              </tr>
              <tr>
                <td align="right" height="30">接口类:</td>
                <td height="30" colspan="2">&nbsp;
                  <input name="className" class="textbox2" width="200"
                  id="title" value="<c:out value='${smsoap.className}'/>" />
                  *包含公有函数提供给远端调用(窄接口)</td>
              </tr>
              <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">接口实现类:</td>
                <td height="30" colspan="2" bgcolor="#f8f8f8">&nbsp;
                  <input name="implClass" class="textbox2" width="200"
                  id="implClass" value="<c:out value='${smsoap.implClass}'/>" />
                  *实现接口类中的函数声明,可以不填(宽接口)</td>
              </tr>
               <tr>
                <td align="right" height="30">请求过滤器:</td>
                <td height="30" colspan="2">&nbsp;
                  <input name="infilter" class="textbox2" width="200"
                  id="infilter" value="<c:out value='${smsoap.infilter}'/>" />
                  *可以更改请求</td>
              </tr>
                <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">回应过滤器:</td>
                <td height="30" colspan="2" bgcolor="#f8f8f8">&nbsp;
                  <input name="outfilter" class="textbox2" width="200"
                  id="outfilter" value="<c:out value='${smsoap.outfilter}'/>" />
                  *可以更改回应</td>
              </tr>
              <tr>
                <td  height="30" align="right">文档style/use:</td>
                <td height="30" colspan="2">&nbsp; <select name="style" class="textbox1" id="style">

                  <option value="document" <c:if test="${smsoap.style=='document'}">selected="selected"</c:if>>document</option>
                  <option value="rpc" <c:if test="${smsoap.style=='rpc'}">selected="selected"</c:if>>rpc</option>
                  <option value="message" <c:if test="${smsoap.style=='message'}">selected="selected"</c:if>>message</option>
                  <option value="wrapped" <c:if test="${smsoap.style=='wrapped'}">selected="selected"</c:if>>wrapped</option>
                </select>
                /
                  <select name="useType" class="textbox1" id="useType">
                    <option value="encoded" <c:if test="${smsoap.useType=='encoded'}">selected="selected"</c:if>>encoded</option>
                    <option value="literal" <c:if test="${smsoap.useType=='literal'}">selected="selected"</c:if>>literal</option>
                </select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                日志输出目录:
                <select name="logname" class="textbox" id="logname">
                  <c:forEach items="${smsoap.dirlist}" var="mapItem">
                    <option value="<c:out value='${mapItem}'/>"
					<c:if test='${smsoap.logname==mapItem}'>
					selected="selected" </c:if>><c:out value='${mapItem}'/> </option>
                 </c:forEach>
                </select></td>
              </tr>
<tr>
                <td height="30" align="right" bgcolor="#f8f8f8">ip地址:</td>
                <td height="30" colspan="2" bgcolor="#f8f8f8">&nbsp;
                  <input name="ipList" class="textbox2" width="200"
                  id="defaultPage" value="<c:out value='${smsoap.ipList}'/>" />
                  *ip验证（支持192.168.*.*），不填不验证</td>
              </tr>
              <tr>
                <td height="30" align="right">&nbsp;</td>
                <td height="30" colspan="2"> &nbsp;
				<c:choose>
				<c:when test='${smsoap.authtype==0}'>
                  <input name="authtype" type="radio" value="0" id="radio1" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input name="authtype" type="radio" value="0" id="radio1" />
				</c:otherwise>
				</c:choose>
                <a href="#" onclick="document.getElementById('radio1').checked=!document.getElementById('radio1').checked">不需认证</a> 				<c:choose>

				<c:when test='${smsoap.authtype==1}'>
                  <input name="authtype" type="radio" value="1" id="radio2" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input name="authtype" type="radio" value="1" id="radio2" />
				</c:otherwise>
				</c:choose>
                <a href="#" onclick="document.getElementById('radio2').checked=!document.getElementById('radio2').checked">Basic认证</a>
                <c:choose>
				<c:when test='${smsoap.authtype==4}'>
                  <input name="authtype" type="radio" value="4" id="radio5" checked="checked" />
				</c:when>
				<c:otherwise>
				  <input name="authtype" type="radio" value="4" id="radio5" />
				</c:otherwise>
				</c:choose>
                <a href="#" onclick="document.getElementById('radio5').checked=!document.getElementById('radio5').checked">禁用此服务</a>

                </td>
              </tr>
              <tr>
                <td height="74" align="right" bgcolor="#f8f8f8">映射配置:</td>
                <td width="51%" height="74" bgcolor="#f8f8f8">&nbsp;
                <textarea name="aegis" cols="80" rows="8" class="textarea" id="aegis"
				   width="200"><c:out value='${smsoap.aegis}'/></textarea></td>
                <td width="31%" valign="middle" bgcolor="#f8f8f8"><p>*如果是WSDL优先设计,可以依此控制输入</p>
                <p>输出文档的Namespace,Prefix,name等.</p></td>
              </tr>

              <tr>
                <td height="30" align="right">配置文件:</td>
                <td height="30" colspan="2">&nbsp;
                  <textarea name="params" cols="90" rows="16" class="textarea" id="params"
				   width="200"><c:out value='${smsoap.params}'/></textarea>
                  <input name="id" type="hidden" id="id" value="<c:out value='${smsoap.id}'/>"/></td>
              </tr>



              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">备注:</td>
                <td height="30" colspan="2" bgcolor="#f8f8f8">&nbsp;
                <textarea name="memo" cols="90" rows="8" class="textarea" id="memo"
				   width="200"><c:out value='${smsoap.memo}'/></textarea></td>
              </tr>
              <tr>
                <td  height="30" align="right">修改时间:</td>
                <td height="30" colspan="2">&nbsp;<c:out value='${smsoap.createTime}'/> </td>
              </tr>
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td height="23" colspan="2" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smsoap.accessPermission>1}">
                <input class="bottonbox" id="submit" type="submit" value="<c:out value='${smsoap.dotype}'/>" name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smsoap.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>