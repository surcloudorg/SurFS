<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@include file="../includes/taglib.jsp"%>
<script src="${path}/js/surfs-web-submit.js"></script>
<!--Header-part-->
<div id="header">
	<h1>
		<!-- <a href="dashboard.jsp">Matrix Admin</a> -->
	</h1>
</div>
<!--close-Header-part-->

<!--top-Header-menu-->
<div id="user-nav" class="navbar navbar-inverse">
	<ul class="nav">
		<li class="dropdown" id="user-menu"><a title="" href="#"
			data-toggle="dropdown" data-target="#user-menu"
			class="dropdown-toggle"><i class="icon icon-user"></i> <span
				class="text">欢迎用户 </span><b class="caret"></b></a>
			<ul class="dropdown-menu">
				<li><a href="javascript:showGlobleProperties()"><i
						class="icon icon-cog"></i> 全局存储配置</a></li>
				<li class="divider"></li>
				<li><a href="javascript:showNodeProperties()"><i
						class="icon icon-sitemap"></i> 存储节点配置</a></li>
				<li class="divider"></li>
				<li><a href="javascript:showListVolumeProperties()"><i
						class="icon icon-inbox"></i> 存储卷配置</a></li>
				<li class="divider"></li>
				<li><a href="javascript:showMountPoint()"><i
						class="icon-folder-close"></i> 存储挂载点配置</a></li>
				<li class="divider"></li>
				<li><a href="javascript:showUsers()"><i
						class="icon-user-md"></i> 存储用户配置</a></li>
				<!-- <li class="divider"></li>
				<li><a href="javascript:showDisk()"><i class="icon-hdd"></i>
						磁盘监控管理</a></li>
				<li class="divider"></li>
				<li><a href="javascript:showDiskLog()"><i class="icon-book"></i>
						磁盘日志管理</a></li> -->
			</ul>
		</li>
		<li class="dropdown" id="data-center"><a title="" href="#"
			data-toggle="dropdown" data-target="#data-center"
			class="dropdown-toggle"><i class="icon icon-cloud"></i> <span
				class="text">${dataCenterName} [更换]</span><b class="caret"></b></a>
			
			<ul class="dropdown-menu">
				<c:forEach items="${dataCenterMap}" var="item" varStatus="data" >
					<li><a href='javascript:switchDataCenter("${item.key}")'><i
						class="icon icon-cloud"></i> ${item.value}</a></li>
					<c:if test="${!data.last}">
						<li class="divider"></li>
					</c:if>
				</c:forEach>
			</ul>
		</li>
				
		<li class=""><a title="" href="javascript:logout()"><i
				class="icon icon-share-alt"></i> <span class="text">退出系统</span></a></li>
	</ul>
</div>

<!--start-top-serch-->
<!-- <div id="search">
		<input type="text" placeholder="Search here..." />
		<button type="submit" class="tip-bottom" title="Search">
			<i class="icon-search icon-white"></i>
		</button>
	</div> -->
<!--close-top-serch-->

<script type="text/javascript">
function showNodeProperties() {
	showSubmit("${path}/storage/showNodeProperties.do");
}

function showListVolumeProperties() {
	showSubmit("${path}/storage/showListVolumeProperties.do");
}

function showGlobleProperties() {
	showSubmit("${path}/storage/globleProperties.do");
}

function showMountPoint() {
	showSubmit("${path}/storage/showMountPoint.do");
}

function showUsers() {
	showSubmit("${path}/storage/showUsers.do");
}

/* function showDisk() {
	showSubmit("${path}/storage/showDisk.do");
}

function showDiskLog() {
	showSubmit("${path}/storage/showDiskLog.do");
} */

function switchDataCenter(dataCenterKey) {
	showSubmit("${path}/storage/switchDataCenter.do?dataCenterKey="+dataCenterKey);
}

function logout() {
	showSubmit("${path}/storage/logout.do");
}
</script>