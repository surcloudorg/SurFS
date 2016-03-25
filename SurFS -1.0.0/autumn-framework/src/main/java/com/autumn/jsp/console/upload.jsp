<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统文件操作</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<SCRIPT language=JavaScript>

function confirmdelete(msg){
    for (var i=0;i<document.Form1.elements.length;i++)
    {
      var e = document.Form1.elements[i];
      if(e.name=="selfilename"){
		 if(e.checked){
			 return confirm(msg);
		 }
	  }
    }
	return false;
}

function selectuser(bol){
    for (var i=0;i<document.Form1.elements.length;i++)
    {
      var e = document.Form1.elements[i];
      if(e.name=="selfilename"){
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

</SCRIPT>
<body>
<FORM action=upload.do method=post enctype="multipart/form-data" name=Form1 id=Form1>

<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 系统文件操作</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500"><br />
          <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td bgcolor="#999999" height="1"></td>
              </tr>
              <tr>
                <td height="30" align="left" nowrap="nowrap" bgcolor="#EFEFEF">&nbsp;&nbsp;地址:
				<c:forEach items="${smupload.linkList}" var="mysrv"><a href="<c:url value="upload.do"> 
                        <c:param name="pathStr" value='${mysrv.filename}'/>
                    </c:url>">${mysrv.path}</a>
                </c:forEach>  
				<input name="pathStr" type="hidden" id="pathStr" value="${smupload.pathStr}" /></td>
              </tr>
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center" border="0">
            <tbody>
              <tr>
                <td width="40%" height="23" align="middle" valign="middle" background="../img/pub/top1.gif" class="JiaCu">文件名</td>
                <td width="15%" height="23" align="right" bordercolor="#EEEEEE" background="../img/pub/top1.gif" class="JiaCu">文件大小</td>
                <td width="25%"
              height="23" align="middle" background="../img/pub/top1.gif" class="JiaCu">修改日期</td>
                <td width="10%" align="middle" background="../img/pub/top1.gif" class="JiaCu">下载</td>
                <c:if test="${smupload.accessPermission>1}">
                <td width="10%" align="middle" background="../img/pub/top1.gif" class="JiaCu"><input type="checkbox" name="checkbox" id="checkbox" onClick="selectuser(this.checked)" /><a href="javascript:selectuser(document.Form1.checkbox.checked);" onClick="setcheck('checkbox')" style="margin-bottom:-3px">选中</a></td></c:if>
              </tr>
			<c:forEach items="${smupload.fileList}" var="mysrv">
              <tr onmouseover="this.style.backgroundColor='#EFEFEF'" onmouseout="this.style.backgroundColor='#ffffff'">
                <td height="23" align="left" bgcolor="#EFEFEF">&nbsp;&nbsp;
				<c:choose> 
				<c:when test="${mysrv.dir}">
				    <img src="../img/pub/folder.gif" style="margin-bottom:-3px"/>     
                <a href="<c:url value="upload.do">    
                    <c:param name="pathStr" value='${mysrv["path"]}'/>
                </c:url>">${mysrv.filename}</a> 
                </c:when> 
				<c:otherwise>
				    <img src="../img/pub/file.gif" style="margin-bottom:-3px"/>
                    <a href="<c:url value="upload.do"> 
                    	<c:param name="dotype" value='view'/>
                        <c:param name="pathStr" value='${smupload.pathStr}'/>
                        <c:param name="dirname" value='${mysrv.filename}'/>
                    </c:url>">${mysrv.filename} </a>
				</c:otherwise>
				</c:choose>				</td>
                <td height="23" align="right"><c:out value='${mysrv.size}'/></td>
                <td height="23" align="middle"><c:out value='${mysrv.lastmodify}'/></td>
                <td align="middle"><img src="../img/pub/download.gif" width="14" height="14" style="margin-bottom:-3px"/>
                    <a href="<c:url value="upload.do"> 
                    	<c:param name="dotype" value='download'/>
                        <c:param name="pathStr" value='${smupload.pathStr}'/>
                        <c:param name="dirname" value='${mysrv.filename}'/>
                    </c:url>">下载 </a>
                  </td>
                  <c:if test="${smupload.accessPermission>1}">
                <td align="middle">
                <input type="checkbox" name="selfilename" id="<c:out value='${mysrv.filename}'/>" value="<c:out value='${mysrv.filename}'/>" /><a href="#" onClick="setcheck('<c:out value='${mysrv.filename}'/>')" style="margin-bottom:-3px">选中</a>
                </td></c:if>
                </tr>
			  </c:forEach> 
            </tbody>
        </table>
        <table cellspacing="0" cellpadding="0" width="100%" align="center"
            bgcolor="#eeeeee" border="0">
            <tbody>
              <tr>
                <td bgcolor="#E7E3E7" height="1" colspan="2"></td>
              </tr>
              <c:if test="${smupload.accessPermission>1}">
              <tr>
                <td width="18%" height="30" align="right" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">文件：</td>
                <td width="82%" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF"><input name="file" type="file" class="textarea"  size="38" />
                <input name="dotype" type="submit" class="bottonbox" id="dotype" value="上传" onclick="return(ckeckfile(Form1.file))" />
                *可上传<span class="redtitle">zip</span>压缩包,自动解压至当前目录,不支持<span class="redtitle">rar</span>格式</td>
              </tr>
              
              <tr>
                <td height="30" align="right" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF">文件夹：</td>
                <td height="30" align="left" valign="middle" nowrap="nowrap" bgcolor="#EFEFEF"><input name="dirname" type="text" class="textbox" id="dirname"  />
                  <input name="dotype" type="submit" class="bottonbox" id="dotype" value="新建" />
                <input name="dotype" type="submit" class="bottonbox" id="dotype" value="删除选中" onclick="return(confirmdelete('确认删除？'))" />
                <c:if test="${smupload.showmakebtn}">
                <input name="dotype" type="submit" class="bottonbox" id="dotype" value="编译选中文件" onclick="return(confirmdelete('确认编译？'))" />
                </c:if>
               </td>
              </tr>
              </c:if>
              <tr>
                <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" class="redtitle"> &nbsp;&nbsp;<c:out value='${smupload.doMsg}'/></td>
              </tr>
            </tbody>
        </table></td></tr>
  </tbody>
</table>
</FORM>
</body>
</html>