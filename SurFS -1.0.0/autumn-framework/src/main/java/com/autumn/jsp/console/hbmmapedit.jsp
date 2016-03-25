<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>Hibernate映射</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<body>
<FORM id=Form1 name=Form1 action=hbms.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<span class="JiaCu"><img height="14" 
                  src="../img/pub/icoblue.gif" width="14" 
                  align="absmiddle" /> <strong>Hibernate映射</strong> - <c:out value='${hbmmap.dotype}'/></span></td>
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
                <td align="right" width="15%" height="30">标题:</td>
                <td height="30">&nbsp;
                <input name="title" class="textbox2" id="classname"  width="200"  value="<c:out value='${hbmmap.title}'/>"/>
                </td>
              </tr>
              <tr>
                <td align="right" bgcolor="#f8f8f8" height="30">类名:</td>
                <td bgcolor="#f8f8f8" height="30">&nbsp;
                    <input name="classname" class="textbox2" id="classname" readonly="readonly" width="200"  value="<c:out value='${hbmmap.classname}'/>"/>
                    *不用填,从xml里解析</td>
              </tr>
              <tr>
                <td align="right" height="30">表名:</td>
                <td height="30">&nbsp;
                <input name="tablename" class="textbox2" readonly="readonly"
                  id="tablename" value="<c:out value='${hbmmap.tablename}'/>" width="200"  />     
                  *不用填,从xml里解析           <input name="id" type="hidden" id="id" value="<c:out value='${hbmmap.id}'/>"/>
                  <c:if test="${hbmmap.dotype=='更新映射'}">
                <input name="datasource" type="hidden" id="datasource" value="<c:out value='${hbmmap.datasource}'/>"/>
                </c:if></td>
              </tr>
              

              <tr>
                <td  height="30" align="right" bgcolor="#f8f8f8">连接池:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;
                <c:choose> 
				<c:when test="${hbmmap.dotype=='更新映射'}">
                <c:out value='${hbmmap.datasource}'/>
                </c:when> 
				<c:otherwise>   
                  <select name="datasource" class="textbox" id="datasource" >
                    <c:forEach items="${hbmmap.jdbc}" var="mapItem"> <option  value="<c:out value='${mapItem}'/>" 	
                        <c:if test='${hbmmap.datasource==mapItem}'>
					selected="selected" </c:if>
                      >
                      <c:out value='${mapItem}'/>
                        </option>
                    </c:forEach>
                  </select>
                  </c:otherwise>  
				</c:choose>                  </td>
              </tr>
              <tr>
                <td align="right" height="30">数据库目录:</td>
                <td height="30">&nbsp;
                <input name="catalogname" class="textbox2" readonly="readonly"
                  id="catalogname" value="<c:out value='${hbmmap.catalogname}'/>" width="200"  />
                *不用填,从xml里解析 </td>
              </tr>

<tr>
                <td height="30" align="right" bgcolor="#f8f8f8">映射配置:</td>
                <td height="30" bgcolor="#f8f8f8">&nbsp;<textarea name="xml" cols="90" rows="26" class="textarea" id="xml" width="200"><c:out value='${hbmmap.xml}'/></textarea></td>
              </tr>
              <tr>
                <td align="right" height="30">修改时间:</td>
                <td height="30">&nbsp;
                <c:out value='${hbmmap.createTime}'/> </td>
              </tr>
              
              <tr>
                <td  height="23" align="right" background="../img/pub/top1.gif">&nbsp;</td>
                <td height="23" background="../img/pub/top1.gif">&nbsp;
                <c:if test="${hbmmap.accessPermission>1}"><input class="bottonbox" id="submit" type="submit" 
				value="<c:out value='${hbmmap.dotype}'/>"
				 name="dotype" /></c:if>
&nbsp;
<input class="bottonbox" type="submit" value="返回" name="Submit11" />
<span class="redtitle" id="dogetmsg"><c:out value='${hbmmap.doMsg}'/></span></td>
              </tr>
            </tbody>
        </table></td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>