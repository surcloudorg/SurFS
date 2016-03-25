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

<%-- <link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link rel="stylesheet" href="${path}/css/jquery.gritter.css" />
<link rel="stylesheet" href="${path}/css/datepicker.css" />
<link rel="stylesheet" href="${path}/css/uniform.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" /> --%>


<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/colorpicker.css" />
<link rel="stylesheet" href="${path}/css/datepicker.css" />
<link rel="stylesheet" href="${path}/css/uniform.css" />
<link rel="stylesheet" href="${path}/css/select2.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-wysihtml5.css" />
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
			<li class="active"><a href="javascript:showDiskLog()"><i
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
					class="current">磁盘日志管理</a>
			</div>
			<h1>磁盘日志管理</h1>
		</div>

		<div class="container-fluid">
			<hr>
			<div id="widget-content">
				<div class="row-fluid">
					<div class="span12">
						<div class="widget-box">
							<div class="widget-title">
								<span class="icon"><i class="icon-th"></i></span>
								<h5>磁盘日志列表</h5>
							</div>
							<div class="widget-content nopadding">
								<form id="globle_form"
									action="${path}/storage/saveGlobleProperties.do" method="post"
									class="form-horizontal">
									<div class="control-group">
										<label class="control-label" style="width: 300px;">日志开始时间（yyyy-mm-dd）：</label>
										<div class="controls controls-row">
											<input type="text" data-date="2015-01-01"
												data-date-format="yyyy-mm-dd" id="dateSearch"
												class="datepicker span2" readonly="readonly">&nbsp;&nbsp;
											<span id="addMount"> <a data-toggle="modal"
												class="btn btn-danger" onclick="showDiskLogs()"> <i
													class="icon icon-search"></i> 查询日志
											</a>
											</span>
										</div>
									</div>
								</form>
								<hr style="margin: 0px 0px 10px 0px;">
								<div class="widget-content">
									<span id="logInfo"
										style="overflow-y: scroll; display: block; height: 550px; font: bold; font-size: medium;">
										选择日期查询日志详情！ </span> <br>
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
		function showDiskLogs() {
			var val = $("#dateSearch").val();
			if (val == "") {
				return;
			}
			$.ajax({
				type : "POST",
				url : "${path}/monitor/showDiskLogDetail.do",
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				data : val,
				success : function(msg) {
					var logInfo = msg["logInfo"];
					if (logInfo == "" || logInfo == null) {
						$("#logInfo").text("没有符合条件的日志信息！");
					} else {
						$("#logInfo").text("");
						$("#logInfo").append(logInfo);
					}
				}
			});
		}
	</script>
	<script src="${path}/js/surfs-web-msg.js"></script>

	<%-- <script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/jquery.gritter.min.js"></script>
	<script src="${path}/js/jquery.uniform.js"></script> 
	<script src="${path}/js/jquery.peity.min.js"></script> 
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/bootstrap-datepicker.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<script src="${path}/js/matrix.popover.js"></script>
	<script src="${path}/js/masked.js"></script>
	<script src="${path}/js/matrix.form_common.js"></script> 
	<script src="${path}/js/wysihtml5-0.3.0.js"></script>  --%>

	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/bootstrap-colorpicker.js"></script>
	<script src="${path}/js/bootstrap-datepicker.js"></script>
	<script src="${path}/js/masked.js"></script>
	<script src="${path}/js/jquery.uniform.js"></script>
	<script src="${path}/js/select2.min.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<script src="${path}/js/matrix.form_common.js"></script>
	<script src="${path}/js/jquery.peity.min.js"></script>
	<script src="${path}/js/bootstrap-wysihtml5.js"></script>

</body>
</html>
