<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>数据库连接池</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=dbpools.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14"
                  src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> <strong>数据库连接池</strong> - <c:out value="${smlog.dotype}"/></span></td>
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
                <td align="right" width="15%" height="30">连接池名称:</td>
                
                <td width="85%" height="30">&nbsp;
                <c:choose>
                <c:when test="${smlog.dotype=='修改配置'}">
                  <input name="jndiname" class="textbox" width="200" readonly="true"
                  id="jndiname" value="<c:out value='${smlog.jndiname}'/>" />
                *不允许修改                </c:when>
                <c:otherwise>
                  <input name="jndiname" class="textbox" width="200" 
                  id="jndiname" value="<c:out value='${smlog.jndiname}'/>" />
                </c:otherwise>
                </c:choose>                           </td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">数据库驱动程序:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                <input name="driver" class="textbox2" width="200"
                  id="driver" value="<c:out value='${smlog.driver}'/>" /></td>
              </tr>
              <tr>
                <td align="right" height="30">连接地址端口:</td>
                <td height="30">&nbsp;
                <input name="dburl" class="textbox2" width="200"
                  id="dburl" value="<c:out value='${smlog.dburl}'/>" /></td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">账号:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="user" class="textbox" width="200"
                  id="user" value="<c:out value='${smlog.user}'/>" />
&nbsp;&nbsp;&nbsp;密码			   
                  <input name="pwd" class="textbox" width="200"
                  id="pwd" value="<c:out value='${smlog.pwd}'/>" /> </td>
              </tr>
              <tr>
                <td align="right" height="30">测试指令:</td>
                <td height="30"> &nbsp; <input name="testsql" class="textbox2" width="200"
                  id="testsql" value="<c:out value='${smlog.testsql}'/>" />
*不填不测试</td>
              </tr>
              <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">最大连接数:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                                  <input name="maxconnection" class="textbox" width="200"
                				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="maxconnection" value="<c:out value='${smlog.maxconnection}'/>" />
最小连接数
                                  <input name="minconnection" class="textbox" width="200"
                				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="minconnection" value="<c:out value='${smlog.minconnection}'/>" /></td>
              </tr>
   
                   <tr>
                     <td height="30" align="right">空闲回收时间:</td>
                     <td height="30">&nbsp;
                     <input name="timeoutvalue" class="textbox" width="200"
                				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="timeoutvalue" value="<c:out value='${smlog.timeoutvalue}'/>" /> 
                     *连接的最大空闲时间,  超过这个时间，释放</td>
                   </tr>
                   <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">最多创建声明数: </td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                <input name="maxstatement" class="textbox" width="200"
                				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
                  id="maxstatement" value="<c:out value='${smlog.maxstatement}'/>" />
                *允许最多创建多少Statement,=0不托管Statement</td>
              </tr>

              <tr>
                <td align="right" background="../img/pub/top1.gif"  height="23">&nbsp;</td>
                <td  background="../img/pub/top1.gif" height="23">&nbsp;
                <c:if test="${smlog.accessPermission>1&&smlog.jndiname!='SystemSource'}"><input class="bottonbox" id="submit" type="submit"
				value="<c:out value='${smlog.dotype}'/>"  name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${smlog.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>