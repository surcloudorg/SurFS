<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><c:out value="${title}"/></title>
</head>

<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../js/wdatepicker/wdatepicker.js"></script>
<SCRIPT language=JavaScript>
function selectall(bol){
    for (var i=0;i<document.Form.elements.length;i++){
        var e = document.Form.elements[i];
        if(e.name=="id_key_name"){
            e.checked = bol;
        }
    }
}

function setcheck(idvalue){
    var e=document.getElementById(idvalue);
    if(e!=null){
        e.checked = !e.checked;
    }
}

function confirmdelete(msg){
    for (var i=0;i<document.Form.elements.length;i++){
        var e = document.Form.elements[i];
        if(e.name=="id_key_name"){
            if(e.checked){
                return confirm(msg);
            }
        }
    }
    return false;
}
</SCRIPT>
<body>
<FORM id=Form name=Form action="<c:out value='${action}'/>" method=post>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> <c:out value="${title}"/></strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
	  <c:if test="${!empty fields}">
	  <table cellspacing="0" cellpadding="0" width="100%" align="center"  bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
              <td bgcolor="#999999" colspan="1" height="1"></td>
          </tr>
		  <c:forEach items="${fields}" var="field">
          <tr>
            <td height="40" align="left" valign="middle" bgcolor="#EEEEEE">&nbsp;&nbsp;&nbsp;<c:out value="${field}" escapeXml="false"/></td>
            </tr>
          <tr>
		  <tr>
             <td bgcolor="#EFEFEF" colspan="1"  height="1"></td>
          </tr>
		  </c:forEach>
        </tbody>
      </table>
	  </c:if>
        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
           >
            <tbody>
              <tr>
			  <c:forEach items="${columnhead}" var="mysrv">
			     <td width="<c:out value='${mysrv[0]}'/>"  height="23" align="<c:out value='${mysrv[1]}'/>" background="../img/pub/top1.gif" class="JiaCu"><c:out value='${mysrv[2]}' escapeXml="false"/></td>
			  </c:forEach>
			  
              </tr>

			  <c:forEach items="${columns}" var="column">
              <tr>			
			    <c:forEach items="${column}" var="col">
                <td height="23" align="<c:out value='${col[1]}'/>" valign="middle"><c:out value='${col[2]}' escapeXml="false"/></td>
				</c:forEach>
              </tr>
			  <tr>
                <td bgcolor="#EFEFEF" colspan="<c:out value='${columnsize}'/>"  height="1"></td>
              </tr>
			  </c:forEach>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            border="0">
            <tbody>
              <tr>
                <td width="20%" height="23" align="center" valign="middle" nowrap="nowrap"><c:out value='${delete}' escapeXml="false"/></td>
                <td align="left" valign="middle" nowrap="nowrap"><span class="redtitle" id="domsg"><c:out value='${domessage}'/></span></td>
              </tr>
              <tr>
                <td height="23" colspan="2" align="center" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"> <span id="Pages">
                  <c:out value='${navigation.htmltext}' escapeXml="false"/>
                </span></td>
              </tr>
            </tbody>
        </table></td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>