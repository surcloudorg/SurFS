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
			
			<li><a href="javascript:showDisk()"><i
					class="icon-hdd"></i> <span>Disk monitor management</span></a></li>
			
			<li class="active"><a href="javascript:showPoolUsedDisks()"><i
					class="icon-hdd"></i> <span>Node management</span></a></li>
			
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="${path}/views/home.jsp" title="Go to Home"
					class="tip-bottom"><i class="icon-home"></i> Home</a> <a href="#"
					class="current">Node management</a>
			</div>
			<h1>Node management</h1>
		</div>

		<div class="container-fluid">
			<div id="pool_success" class="alert alert-success alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">SUCCESS!</h4>
				<span></span>
			</div>
			<div id="pool_error" class="alert alert-error alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">FAILUER!</h4>
				<span></span>
			</div>
			<hr>
			<div id="widget-content">

				<c:forEach var="mmList" items="${mmList}" varStatus="status">
					<div class="row-fluid">
						<div class="span10">
							<div class="widget-box collapsible">
								<div class="widget-title">
									<a href="#${status.index}" data-toggle="collapse"> <span
										class="icon"><i class="icon-chevron-down"></i> </span>
										<h5>Node:${mmList.key}</h5>
									</a>
								</div>

								<div class="${status.index eq 0 ? 'collapse in' : 'collapse'}"
									id="${status.index}">
									<div class="widget-content">
										<c:forEach var="mapList" items="${mmList.value}">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th colspan="4">pool：${mapList.key}</th>
													</tr>
													<tr>
														<th style="width: 400px;">JbodId</th>
														<th style="width: 300px;">Panel</th>
														<th style="width: 200px;">Bay</th>
														<th>Device</th>
													</tr>
												</thead>
												<c:forEach var="disk" items="${mapList.value}">
													<tr>
														<td>${disk.jbodId}</td>
														<td>
															<c:if test="${disk.panel == 'f'}">Front</c:if>
															<c:if test="${disk.panel == 'b'}">Rear</c:if>
														</td>
														<td>${disk.disk}</td>
														<td>${disk.devName}</td>
													</tr>
												</c:forEach>
											</c:forEach>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>

			</div>
		</div>
	</div>



	

	<%@include file="../../includes/footer.jsp"%>

	<script type="text/javascript">
		
	</script>
	<script src="${path}/js/surfs-web-msg.js"></script>
	<script src="${path}/js/surfs-block.js"></script>

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
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<%-- <script src="${path}/js/matrix.interface.js"></script>  --%>
	<script src="${path}/js/matrix.popover.js"></script>
	<script src="${path}/js/jquery.gritter.min.js"></script>
</body>
</html>
