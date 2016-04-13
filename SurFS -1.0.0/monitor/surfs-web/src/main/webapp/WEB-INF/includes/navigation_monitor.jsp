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
				class="text">User profile </span><b class="caret"></b></a>
			
		</li>
		
				
		<li class=""><a title="" href="javascript:logout()"><i
				class="icon icon-share-alt"></i> <span class="text">Exit</span></a></li>
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
/* function showNodeProperties() {
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

function switchDataCenter(dataCenterKey) {
	showSubmit("${path}/storage/switchDataCenter.do?dataCenterKey="+dataCenterKey);
} */

function showDisk() {
	showSubmit("${path}/monitor/showDisk.do");
}

function showDiskLog() {
	showSubmit("${path}/monitor/showDiskLog.do");
}

function logout() {
	showSubmit("${path}/monitor/logout.do");
}

function showCluster() {
	showSubmit("${path}/monitor/showCluster.do");
}

function showPoolUsedDisks() {
	showSubmit("${path}/monitor/showPoolUsedDisks.do");
}
</script>
