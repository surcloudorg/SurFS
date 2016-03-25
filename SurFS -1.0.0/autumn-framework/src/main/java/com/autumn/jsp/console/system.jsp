<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=utf8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>重载类</title>
</head>
<LINK href="../img/pub/body.css" type=text/css rel=stylesheet>
<script type="text/javascript" src="../img/pub/sys.js"></script>
<body>
<FORM id=Form1 name=Form1 action=system.do method=post>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
  <tbody>
    <tr>
      <td
            height="25" valign="bottom" background="../img/pub/content_top_bg.jpg">&nbsp;&nbsp;<strong><img
                  height="14" src="../img/pub/icoblue.gif" width="14"
                  align="absmiddle" /> 系统 - 重载类</strong></td>
    </tr>
    <tr>
      <td valign="top" height="500">
	  <br/>
	  <table cellspacing="0" cellpadding="0" width="100%" align="center" bgcolor="#eeeeee" border="0">
        <tbody>
          <tr>
            <td height="23" colspan="2" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif" bgcolor="#cccccc">
            &nbsp;&nbsp;<span class="JiaCu">重载类</span>
            &nbsp;&nbsp;<a href="sysproperties.do">查看系统变量</a>            
            &nbsp;&nbsp;<a href="systhreads.do">激活线程</a>            
            &nbsp;&nbsp;<a href="sysmemory.do">系统信息</a> 
            </td>
          </tr>
          <tr>
            <td height="30" colspan="2" align="left" valign="middle">
            
            <table width="100%" border="0" cellpadding="0" cellspacing="15" bgcolor="#EEEEEE" bordercolor="#EEEEEE">
                <tbody>
                  <tr>
                    <td class="msg" width="100%" height="50" valign="top" bordercolor="#999999" bgcolor="#FFFFFF"><br/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重载类的作用是让系统加载你最新上传的class类文件，如果你没有做任何更新，请不要重载，只有在必要的时候执行重载，重载意味着系统需要把classpath下的<br />
&nbsp;&nbsp;&nbsp;&nbsp;所有类都要重新加载一次（当然是在用到时侯开始加载），注意系统不会单独对一个类文件实现重载。<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;执行重载命令后，运行于框架中的服务，只有重新启动才会使更新生效，目前框架中仅页面控制器(com.autumn.core.web.Action)支持重载后更新立刻生效！<br />
&nbsp;&nbsp;&nbsp;&nbsp;当然可以对服务进行二次封装来实现重载后更新的代码立刻生效，所以对于重载后必须重启的服务需要在备注里加入说明，一般服务运行时显示的图标可以区别当前服<br />
&nbsp;&nbsp;&nbsp;&nbsp;务运行的是否是一个过期版本。<br />
<br />
&nbsp;&nbsp;&nbsp;&nbsp;<img src="../img/pub/run.gif" width="22" height="22" align="absmiddle"/>表示服务运行的是最新版本<br />
<br />
&nbsp;&nbsp;&nbsp;&nbsp;<img src="../img/pub/runwithwarn.gif" width="22" height="22" align="absmiddle"/>表示服务运行的可能是过期版本<br />
<br/> 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;需要注意的是，如果两个服务之间需要进行数据交互，可能需要保证两个服务处于同一个类加载器中，一般做法是执行重载后重启这两个服务，更好的做法是对需<br />
&nbsp;&nbsp;&nbsp;&nbsp;要交互的数据模型进行二次封装。<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关于类加载的相关问题，请阅读开发规范！
<br/>
<br/> 	 				                   </td>
                  </tr>
                </tbody>
            </table>
            
            </td>
          </tr>
          <tr>
            <td width="33%" height="23" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif">&nbsp;&nbsp;
                <input name="isread" type="checkbox" id="isread" value="checkbox"/><a href="#" onclick="document.Form1.isread.checked=!document.Form1.isread.checked">我已经了解重载操作的注意事项</a></td>
            <td width="67%" align="left" valign="middle" nowrap="nowrap" background="../img/pub/top1.gif"><c:if test="${smsystem.accessPermission>1}"><input name="loadclass" type="submit" class="bottonbox" id="loadclass" value="重载" onclick="return(checkloadclass());" /></c:if>
              &nbsp;&nbsp;<span class="redtitle" id="loadcalssmsg"><c:out value="${smsystem.loadclassmsg}"/></span></td>
          </tr>
        </tbody>
      </table>
	  </td>
    </tr>
  </tbody>
</table>
</FORM>
</body>
</html>