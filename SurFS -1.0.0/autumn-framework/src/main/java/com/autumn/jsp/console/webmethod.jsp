<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>执行方法</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 执行方法-<c:out value='${smweb.dirName}'/>-<c:out value='${smweb.title}'/></strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
      <c:forEach items="${smweb.serviceConfig.methods}" var="mapItem"> 
	  <table cellspacing="0" cellpadding="0" width="100%" align="center" bgcolor="#eeeeee" border="0">
      <FORM id=Form1 name=Form1 action=webs.do method=post>
        <tbody>
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">&nbsp;&nbsp;<c:out value='${mapItem.comment}'/>(<c:out value='${mapItem.methodName}'/>) </td>
          </tr>

       
          
          <c:forEach items="${mapItem.params}" var="propertys"> 
          <tr>
            <td width="25%" height="30" align="right" valign="middle" nowrap="nowrap">
            <c:out value='${propertys.comment}'/>(<c:out value='${propertys.key}'/>)
            </td>
            <td width="75%" align="left" valign="middle" nowrap="nowrap" bgcolor="#f8f8f8">&nbsp;<input name="<c:out value='${propertys.key}'/>" type="text" 
            class="textbox2" value="<c:out value='${propertys.value}'/>" /></td>
          </tr>
          </c:forEach>
   
      
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;&nbsp;&nbsp;&nbsp;
              <c:if test="${smweb.accessPermission>1}"><input name="dotype" type="submit" class="bottonbox" id="button" value="呼叫此方法" /></c:if>
              <input type="hidden" name="classname" value="<c:out value='${mapItem.methodName}'/>" />
<input type="hidden" name="dirName" id="dirName" value="<c:out value='${smweb.dirName}'/>" />
              <input class="bottonbox" type="submit" value="返回" name="Submit11" />
              </td>
            </tr>

            <tr><td height="6" colspan="2" bgcolor="#f8f8f8"> </td> </tr>
            <c:if test="${smweb.classname==mapItem.methodName}">
            <c:forEach items="${smweb.rows}" var="mysrv">
           <tr>
            <td height="20" colspan="2" align="left" valign="middle" bgcolor="#f8f8f8">           
            &nbsp;&nbsp;<c:out value='${mysrv}'/>
            </td>
            </tr>
            </c:forEach>
            </c:if>
            <tr><td height="6" colspan="2" bgcolor="#f8f8f8"> </td> </tr>   
            
        </tbody>
        </FORM>
      </table>
	  </c:forEach> 
	  </td>
    </tr>
  </tbody>
</table>

</body>
</html>