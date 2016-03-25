<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>工程源码</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/prototype/v1.6.0.js"></script>
<script type="text/javascript" src="../img/pub/smcode.js"></script>

<body>
<FORM action=svncodes.do method=post name=Form1 id=Form1>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> SVN在线更新 - 状态
        <input type="hidden" name="id" id="id" value="<c:out value='${smcode.id}'/>" />
      </strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
      
      
      
      
<c:if test="${actionmessage.message!=null}">	       
<table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">&nbsp;&nbsp;信息</td>
          </tr>
          <tr>
            <td height="30" colspan="2" align="left" valign="middle">
            
            <table  width="100%" border="0" cellpadding="0" cellspacing="10" bgcolor="#EEEEEE" bordercolor="#EEEEEE">
              <tbody>
                <tr>
                  <td  class="msg" width="100%"  valign="top" bordercolor="#999999" bgcolor="#FFFFFF"><p><br/> 
                    &nbsp;&nbsp; <c:out value="${actionmessage.message}"/>                                      </p>
                    <p><br/>
                    </p></td>
                  </tr>
                </tbody>
              </table>
              
              </td>
          </tr>
          <tr>
            <td width="12%" height="23" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"></td>
            <td width="88%" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"><c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="重新检查更新" /></c:if>
              <input name="button" type="submit" class="bottonbox" id="button" value="返回" /></td>
          </tr>
		
        </tbody>
      </table>
	  <br/>
</c:if>


<c:if test="${actionmessage.updated!=null}">	
<table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">&nbsp;&nbsp;检测到以下文件在服务器端被改动</td>
          </tr>
          <tr>
            <td height="30" colspan="2" align="left" valign="middle">
            
            <table  width="100%" border="0" cellpadding="0" cellspacing="10" bgcolor="#EEEEEE" bordercolor="#EEEEEE">
              <tbody>
                <tr>
                  <td class="msg" width="100%"  valign="top" bordercolor="#999999" bgcolor="#FFFFFF"><br/> 
                  
                  <c:forEach items="${actionmessage.updated}" var="updatefile" varStatus="uid">
                    <c:if test="${uid.count<=100}">	 
                    &nbsp;&nbsp;
                    <input name="updated" type="checkbox" id="u<c:out value='${uid.count}'/>" value="<c:out value='${updatefile}'/>" />
                    <a href="#" onClick="setcheck('u<c:out value='${uid.count}'/>')"><c:out value='${updatefile}'/></a><br/> 
                    </c:if>	 
                   </c:forEach>
                    <c:if test="${actionmessage.updatedSize>100}">	 
                    &nbsp;&nbsp;
                    <span class="redtitle">还有<c:out value='${actionmessage.updatedSize-100}'/>行未显示。。。。。。。。。。。。</span><br/> 
                   </c:if>	
                    <br/> 
                    </td>
                  </tr>
                </tbody>
              </table></td>
          </tr>
          <tr>
            <td width="12%" height="23" align="left" valign="top" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp; 
            &nbsp;&nbsp;
            <input type="checkbox" name="updatedAll" id="updatedAll"  onClick='selectupdated(this.checked)' />
            <a href="javascript:selectupdated(Form1.updatedAll.checked);" onClick="updatedAll.checked=!updatedAll.checked">全选</a></td>
            <td width="88%" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="更新" onClick="return(confirmupdated('确认更新选中文件？'))" /> </c:if>
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="更新全部" onClick="return(confirm('确认更新全部文件？'))" /></c:if>
            </td>
          </tr>
		
        </tbody>
      </table>
	  <br/>
</c:if>	
      
<c:if test="${actionmessage.modified!=null}">	 
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">&nbsp;&nbsp;检测到以下文件在本地被改动</td>
          </tr>
          <tr>
            <td height="30" colspan="2" align="left" valign="middle">
            <table  width="100%" border="0" cellpadding="0" cellspacing="10" bgcolor="#EEEEEE" bordercolor="#EEEEEE">
              <tbody>
                <tr>
                  <td class="msg" width="100%"  valign="top" bordercolor="#999999" bgcolor="#FFFFFF"><br/> 
                  <c:forEach items="${actionmessage.modified}" var="modifyfile" varStatus="mid">
                    <c:if test="${mid.count<=100}">	 
                    &nbsp;&nbsp;
                    <input type="checkbox" name="modified" id="m<c:out value='${mid.count}'/>" value="<c:out value='${modifyfile}'/>" />
                    <a href="#" onClick="setcheck('m<c:out value='${mid.count}'/>')"><c:out value='${modifyfile}'/></a><br/> 
                    </c:if>	 
                   </c:forEach>
                    <c:if test="${actionmessage.modifiedSize>100}">	 
                    &nbsp;&nbsp;
                    <span class="redtitle">还有<c:out value='${actionmessage.modifiedSize-100}'/>行未显示。。。。。。。。。。。。</span><br/> 
                   </c:if>	
                    <br/> 
                    </td>
                  </tr>
                </tbody>
              </table></td>
          </tr>  
          <tr>
            <td width="12%" height="23" align="left" valign="top" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp; 
            &nbsp;&nbsp;
            <input type="checkbox" name="modifiedAll" id="modifiedAll" onClick='selectmodified(this.checked)' />
            <a href="javascript:selectmodified(Form1.modifiedAll.checked);" onClick="modifiedAll.checked=!modifiedAll.checked">全选</a></td>
            <td width="88%" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="提交"  onClick="return(confirmmodified('确认将选中改动提交到SVN服务器？'))" /> </c:if>
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="提交全部" onClick="return(confirm('确认将全部改动提交到SVN服务器？'))"/></c:if>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="还原"  onClick="return(confirmmodified('确认将选中改动还原到最新版本？'))"/></c:if>
            <c:if test="${smcode.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="dotype" value="还原全部"  onClick="return(confirm('确认将全部改动还原到最新版本？'))"/>  </c:if>          
            </td>
          </tr>
		
        </tbody>
      </table>
	  <br/>
</c:if>	      
     
      </td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>