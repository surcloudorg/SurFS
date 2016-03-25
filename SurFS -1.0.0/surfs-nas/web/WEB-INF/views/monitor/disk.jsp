<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<%-- <link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" /> --%>

<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link rel="stylesheet" href="${path}/css/jquery.gritter.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" />
</head>
<body>
	<%@include file="../../includes/navigation_monitor.jsp"%>

	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<!-- <li><a href="javascript:showGlobleProperties()"><i
					class="icon-cog"></i> <span>全局存储配置</span></a></li>
			<li><a href="javascript:showNodeProperties()"><i
					class="icon-sitemap"></i> <span>存储节点配置</span></a></li>
			<li><a href="javascript:showListVolumeProperties()"><i
					class="icon-inbox"></i> <span>存储卷配置</span></a></li>
			<li><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a>
			<li><a href="javascript:showUsers()"><i class="icon-user-md"></i>
					<span>存储用户配置</span></a></li> -->
			<li class="active"><a href="javascript:showDisk()"><i
					class="icon-hdd"></i> <span>磁盘监控管理</span></a></li>
			<li><a href="javascript:showDiskLog()"><i
					class="icon-book"></i> <span>磁盘日志管理</span></a></li>
			<li><a href="javascript:showCluster()"><i
					class="icon-sitemap"></i> <span>集群监控管理</span></a></li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="${path}/views/home.jsp" title="Go to Home"
					class="tip-bottom"><i class="icon-home"></i> 主页</a> <a href="#"
					class="current">磁盘监控管理</a>
			</div>
			<h1>磁盘监控管理</h1>
		</div>
		
		<c:forEach var="jbodMaps" items="${mapDisks}" varStatus="status">
			<!-- start -->
			<div class="container-fluid">
				<c:if test="${status.index eq 0}">
					<hr>
				</c:if>
				<div class="row-fluid">
					<c:forEach var="panelMaps" items="${jbodMaps.value}">
						<c:if test="${panelMaps.key == 'f'}">
							<div class="span6">
								<div class="widget-box collapsible">
									<div class="widget-title">
										<a href="#${jbodMaps.key}_f" data-toggle="collapse"><span
											class="icon"><i class="icon-arrow-right"> </i></span>
											<h5>JBOD:${jbodMaps.key} 前面板</h5> </a> <span
											class="label label-inverse">未知</span><span
											class="label label-warning">未连接</span> <span
											class="label label-success">使用中</span> <span
											class="label label-important">故障中</span> <span
											class="label label-info">未使用</span>
									</div>
									<div class="${status.index eq 0 ? 'collapse in' : 'collapse'}" id="${jbodMaps.key}_f">
										<div class="widget-content">
											<table align="center" border="0" cellpadding="10">
												<tr style="text-align: center;">
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '17' || diskMaps.value.disk == '23' || diskMaps.value.disk == '29' || diskMaps.value.disk == '35'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '16' || diskMaps.value.disk == '22' || diskMaps.value.disk == '28' || diskMaps.value.disk == '34'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr style="text-align: center;">
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '15' || diskMaps.value.disk == '21' || diskMaps.value.disk == '27' || diskMaps.value.disk == '33'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '14' || diskMaps.value.disk == '20' || diskMaps.value.disk == '26' || diskMaps.value.disk == '32'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr style="text-align: center;">
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '13' || diskMaps.value.disk == '19' || diskMaps.value.disk == '25' || diskMaps.value.disk == '31'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '12' || diskMaps.value.disk == '18' || diskMaps.value.disk == '24' || diskMaps.value.disk == '30'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
											</table>
										</div>
									</div>
								</div>
							</div>
						</c:if>
						<c:if test="${panelMaps.key == 'b'}">
							<div class="span6">
								<div class="widget-box collapsible">
									<div class="widget-title">
										<a href="#${jbodMaps.key}_b" data-toggle="collapse"> <span
											class="icon"><i class="icon-arrow-right"></i></span>
											<h5>JBOD:${jbodMaps.key} 后面板</h5>
										</a> <span class="label">未连接</span> <span
											class="label label-success">使用中</span> <span
											class="label label-important">故障中</span> <span
											class="label label-info">未使用</span>
									</div>
									<div class="${status.index eq 0 ? 'collapse in' : 'collapse'}" id="${jbodMaps.key}_b">
										<div class="widget-content">
											<table align="center" border="0" cellpadding="10">
												<tr style="text-align: center;">
													<td rowspan="3"
														style="width: 80px;; font-size: large; font-weight: bolder;"></td>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '16' || diskMaps.value.disk == '22' || diskMaps.value.disk == '28'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>	
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '15' || diskMaps.value.disk == '21' || diskMaps.value.disk == '27'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr style="text-align: center;">
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '14' || diskMaps.value.disk == '20' || diskMaps.value.disk == '26'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="bottom" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '10' || diskMaps.value.disk == '13' || diskMaps.value.disk == '19' || diskMaps.value.disk == '25'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr style="text-align: center;">
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '9' || diskMaps.value.disk == '12' || diskMaps.value.disk == '18' || diskMaps.value.disk == '24'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
												<tr>
													<c:forEach var="diskMaps" items="${panelMaps.value}">
														<c:if test="${diskMaps.value.disk == '8' || diskMaps.value.disk == '11' || diskMaps.value.disk == '17' || diskMaps.value.disk == '23'}">
															<td><a title="" id="popover_example"
																data-content="状态:${diskMaps.value.status}<br> 设备名:${diskMaps.value.devName}<br> zpool名称:${diskMaps.value.zpoolName}"
																data-placement="top" data-toggle="popover"
																class="${diskMaps.value.statusCss}" href="#"
																data-original-title="${diskMaps.value.disk}"
																style="width: 55px;">${diskMaps.value.disk}</a></td>
														</c:if>
													</c:forEach>
												</tr>
											</table>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</div>
			</div>
		</c:forEach>
		<!-- end -->
	</div>

	<%@include file="../../includes/footer.jsp"%>

	<script type="text/javascript">
		
	</script>
	<script src="${path}/js/surfs-web-msg.js"></script>

	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<%-- <script src="${path}/js/matrix.interface.js"></script>  --%>
	<script src="${path}/js/matrix.popover.js"></script>
	<script src="${path}/js/jquery.gritter.min.js"></script>

</body>
</html>
