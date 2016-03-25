<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改登录资料</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=users.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> 用户管理 - <c:out value='${smuseredit.dotype}'/></span></td>
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
                <td width="15%" height="30" align="right">用户名:</td>
                <td width="85%" height="30">&nbsp;
                  <input name="username" class="textbox" width="200" 
                  id="moTask" value="<c:out value='${smuseredit.userName}'/>"  />
				   *不能包含空格，长度不小于6</td>
              </tr>
              <tr>
                <td align="right"  bgcolor="#f8f8f8" height="30">密码:</td>
                <td  bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="password" type="text" class="textbox" 
                  id="className" width="200" value="<c:out value='${smuseredit.passWord}'/>"/>
                  *不能包含空格，长度不小于6
                  <input name="id" type="hidden" id="id" 
				  value="<c:out value='${smuseredit.id}'/>" /></td>
              </tr>
              <tr>
                <td align="right" height="30">姓名:</td>
                <td height="30">&nbsp;
                  <input name="realname" type="text" class="textbox" 
                  id="timeOut" value="<c:out value='${smuseredit.realname}'/>" width="200" /> 
                  *真实性名，登录后显示的管理员名程</td>
              </tr>
              
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">用户群:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="userGroup" type="text" class="textbox" 
                  id="realname" value="<c:out value='${smuseredit.userGroup}'/>" width="200" />
                  *由具体的项目而定</td>
              </tr>
              <tr>
                <td align="right" height="30">电子信箱:</td>
                <td height="30">&nbsp;
                  <input name="email" type="text" class="textbox" 
                  id="realname2" value="<c:out value='${smuseredit.email}'/>" width="200" />
                *用户获取密码是可用</td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">手机号:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                  <input name="mobile" type="text" class="textbox" 
                  id="timeout" value="<c:out value='${smuseredit.mobile}'/>" width="200" />
                  *手机号码，备用如：程序告警</td>
              </tr>
              <tr>
                <td align="right" height="30">权限:</td>
                <td height="30">&nbsp;
                  <input name="permission" class="textbox" width="200" 
                  id="timeout2" value="<c:out value='${smuseredit.permission}'/>"				  
				   /> 
                  *控制台规则：0禁止访问，1允许访问自己，2允许查看别人，3全部允许</td>
              </tr>
              <tr>
                <td height="30" align="right" bgcolor="#f8f8f8">登录路径:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                  <select name="dirId" class="textbox" id="dirId">
				  <c:forEach items="${smuseredit.dirList}" var="mapItem"> 
                    <option value="<c:out value='${mapItem.key}'/>" 
					<c:if test='${smuseredit.dirId==mapItem.key}'>
					selected="selected" </c:if>><c:out value='${mapItem.value}'/> </option>
				   </c:forEach>   
                  </select>
                  *该账号登录后进入的目录                </td>
              </tr>
              <tr>
                <td  height="30" align="right">SOAP服务路径:</td>
                <td  height="30">&nbsp;
                <select name="soapId" class="textbox" id="soapId">
				  <c:forEach items="${smuseredit.soapList}" var="mapItem"> 
                    <option value="<c:out value='${mapItem.key}'/>" 
					<c:if test='${smuseredit.soapId==mapItem.key}'>
					selected="selected" </c:if>><c:out value='${mapItem.value}'/> </option>
				   </c:forEach>   
                  </select>
                *登录路径为services时有效                </td>
              </tr>
              <tr>
                <td  height="30" align="right">登陆ip:</td>
                <td  height="30">&nbsp;
				<input name="iplist" class="textbox" width="400" 
                  id="timeout3" value="<c:out value='${smuseredit.iplist}'/>"				
				   />
				*仅允许指定ip登陆,如:192.168.0.*,211.27.22.123</td>
              </tr>     
              
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">会话超时:</td>
                <td  height="30" bgcolor="#f8f8f8">&nbsp;
                  <input name="stimeOut" class="textbox" 
                  id="loginPath" value="<c:out value='${smuseredit.stimeOut}'/>"
				  OnKeyUp="this.value=this.value.replace(/\D/g,'')"
				  onpaste="this.value=this.value.replace(/\D/g,'')"
				   width="200" />
                  *Session失效时长</td>
              </tr>
              <tr>
                <td align="right"  height="30">&nbsp;</td>
                <td height="30">&nbsp; 
				<c:choose> 
				<c:when test='${smuseredit.isActive==false}'>	
				  <input type="radio" name="isActive" value="0" checked='checked' id="radio2" />
				</c:when>
				<c:otherwise> 
				  <input type="radio" name="isActive" value="0" id="radio2" />
				</c:otherwise> 
				</c:choose> 
                  <a href="#" onclick="document.getElementById('radio2').checked=!document.getElementById('radio2').checked">冻结账号</a> 
				<c:choose> 
				<c:when test='${smuseredit.isActive==true}'>	
				  <input name="isActive" type="radio" value="1" checked="checked" id="radio1"/>
				</c:when>
				<c:otherwise> 
				  <input name="isActive" type="radio" value="1" id="radio1"/>
				</c:otherwise> 
				</c:choose> 
                  <a href="#" onclick="document.getElementById('radio1').checked=!document.getElementById('radio1').checked">激活账号</a></td>
              </tr>
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">备注:</td>
                <td  height="30" bgcolor="#f8f8f8">&nbsp;
                <textarea name="memo" cols="80" rows="6" class="textarea" id="stimeOut" width="800"><c:out value='${smuseredit.memo}'/></textarea></td>
              </tr>
              <tr>
                <td height="30" align="right">创建时间:</td>
                <td height="30">&nbsp;<c:out value='${smuseredit.createtime}'/> </td>
              </tr>
              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">最后登陆时间:</td>
                <td  height="30" bgcolor="#f8f8f8">&nbsp;<c:out value='${smuseredit.logintime}'/> </td>
              </tr>

              <tr>
                <td  height="30" align="right">扩展字段:</td>
                <td  height="30">&nbsp;
                <input name="extParams" class="textbox" width="400" 
                  id="timeout3" value="<c:out value='${smuseredit.extParams}'/>"				
				   />
                *字符串类型</td>
              </tr>
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td  height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${smuseredit.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" 
				value="<c:out value='${smuseredit.dotype}'/>"
				 name="dotype"  onclick="return(checkedituser());"/></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" /><span class="redtitle" id="dogetmsg">
				   	<c:out value='${smuseredit.doMsg}'/>							   
				  </span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>