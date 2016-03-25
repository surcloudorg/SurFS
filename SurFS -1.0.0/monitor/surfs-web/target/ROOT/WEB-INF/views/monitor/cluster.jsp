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
<link rel="stylesheet" href="${path}/css/select2.css" />
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
			<li><a href="javascript:showDisk()"><i class="icon-hdd"></i>
					<span>磁盘监控管理</span></a></li>
			<li><a href="javascript:showDiskLog()"><i class="icon-book"></i>
					<span>磁盘日志管理</span></a></li>
			<li class="active"><a href="javascript:showCluster()"><i
					class="icon-sitemap"></i> <span>集群监控管理</span></a></li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="${path}/views/home.jsp" title="Go to Home"
					class="tip-bottom"><i class="icon-home"></i> 主页</a> <a href="#"
					class="current">集群监控管理</a>
			</div>
			<h1>集群监控管理</h1>
		</div>

		<div class="container-fluid">
			<hr>
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-list"></i>
							</span>
							<h5>集群监控管理</h5>
						</div>
						<div class="widget-content">
						
						
							<div class="row-fluid">
								<div class="span6">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th colspan="3">ip</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td rowspan="2" style="width: 150px; vertical-align: middle; text-align: center;">网络状态</td>
												<td style="vertical-align: middle; text-align: center;">eth0</td>
												<td style="vertical-align: middle; text-align: center;">良好</td>
											</tr>
											<tr>
												<td style="vertical-align: middle; text-align: center;">eth0</td>
												<td style="vertical-align: middle; text-align: center;">良好</td>
											</tr>
											<tr>
												<td rowspan="2" style="width: 150px; vertical-align: middle; text-align: center;">存储状态</td>
												<td style="vertical-align: middle; text-align: center;">zpool</td>
												<td style="vertical-align: middle; text-align: center;">良好</td>
											</tr>
											<tr>
												<td style="vertical-align: middle; text-align: center;">zpool</td>
												<td style="vertical-align: middle; text-align: center;">良好</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="span6">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th>test</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>test</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
							
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@include file="../../includes/footer.jsp"%>

	<script type="text/javascript">
		
	</script>

	<script src="${path}/js/surfs-web-msg.js"></script>
	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/jquery.uniform.js"></script>
	<script src="${path}/js/select2.min.js"></script>
	<script src="${path}/js/jquery.dataTables.min.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<script src="${path}/js/matrix.tables.js"></script>
</body>
</html>
