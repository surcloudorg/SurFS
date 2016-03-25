<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/uniform.css" />
<link rel="stylesheet" href="${path}/css/select2.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" />
</head>
<body onload="isReadyNodes()">
	<%@include file="../includes/navigation.jsp"%>
	
	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<li><a href="javascript:showGlobleProperties()"><i
					class="icon icon-cog"></i> <span>全局存储配置</span></a></li>
			<li class="active"><a href="javascript:showNodeProperties()"><i
					class="icon icon-sitemap"></i> <span>存储节点配置</span></a></li>
			<li><a href="javascript:showListVolumeProperties()"><i
					class="icon icon-inbox"></i> <span>存储卷配置</span></a></li>
			<li><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a></li>
			<li><a href="javascript:showUsers()"><i class="icon-user-md"></i>
					<span>存储用户配置</span></a></li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->
	
	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="${path}/views/home.jsp" title="Go to Home" class="tip-bottom"><i
					class="icon-home"></i> 主页</a> <a href="#" class="current">存储节点配置</a>
			</div>
			<h1>存储节点配置</h1>
		</div>
		<div class="container-fluid">
			<div id="node_success" class="alert alert-success alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="node_error" class="alert alert-error alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<hr>
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"><i class="icon-th"></i></span>
							<h5>存储节点列表</h5>
						</div>
						<div class="widget-content nopadding">
							<table class="table table-bordered data-table">
								<thead>
									<tr>
										<th width="200px">服务节点</th>
										<th width="150px">端口号</th>
										<th width="200px">BackupList</th>
										<th width="150px">状态</th>
										<th width="300px">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="nodeProperties" items="${listNodeProperties}">
										<tr class="gradeX" id="tr_${nodeProperties.serverHost}">
											<td style="text-align: center;">${nodeProperties.serverHost}</td>
											<td style="text-align: center;">${nodeProperties.port}</td>
											<td style="text-align: center;">${nodeProperties.backupList}</td>
											<td style="text-align: center;">
												<span class="label" id="${nodeProperties.serverHost}"></span>
											</td>
											<td style="text-align: center;">
												<span id="editview_${nodeProperties.serverHost}" style="display: none;">
													<a href="#editAlert" data-toggle="modal" class="btn btn-info btn-mini"
													onclick="fillingData('${nodeProperties.serverHost}','${nodeProperties.port}','${nodeProperties.backupList}')">
													<i class="icon icon-edit"></i> 编辑</a> <a href="${path}/storage/showVolumeProperties.do?serverHost=${nodeProperties.serverHost}"
													class="btn btn-info btn-mini"><i class="icon icon-search"></i> 查看卷</a>
												</span>
												<span id="delete_${nodeProperties.serverHost}" style="display: none;">
													<a href="#deleteAlert" data-toggle="modal" class="btn btn-inverse btn-mini" 
													onclick="javascript:$('#delHidden').val('tr_${nodeProperties.serverHost}');$('#serverHostHidden').val('${nodeProperties.serverHost}')">
													<i class="icon icon-remove"></i> 删除</a>
												</span>
												<a href="javascript:refreshVolume('${nodeProperties.serverHost}', '${path}')"
													class="btn btn-warning btn-mini"><i class="icon icon-refresh"></i> 重新扫描卷</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%@include file="../includes/footer.jsp"%>

	<!-- pop-up dialogs start -->
	<div id="deleteAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3><span class="icon"><i class="icon-align-justify"></i></span> 删除确认</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<input type="hidden" id="delHidden">
			<input type="hidden" id="serverHostHidden">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="confirmDeleteNode('${path}')"><i class="icon icon-save"></i> 确定</a>
			<a data-dismiss="modal" class="btn" href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- pop-up dialogs end -->

	<!-- pop-up dialogs start -->
	<div id="editAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 服务节点信息
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="post" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">服务节点 :</label>
							<div class="controls">
								<input type="text" class="span2" value=""
									readonly="readonly" id="serverHost"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">端口号 :</label>
							<div class="controls">
								<input type="text" class="span2" value=""
									readonly="readonly" id="port"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">BackupList :</label>
							<div class="controls">
								<input type="text" class="span2" value="" id="backupList"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">状态 :</label>
							<div class="controls">
								<input id="status" type="text" class="span2" readonly="readonly" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="saveNodeProperties('${path}')"><i
				class="icon icon-save"></i> 保存</a>
			<a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- pop-up dialogs end -->

	<script type="text/javascript">
		function isReadyNodes() {
		    $(document).ready(function() {
		        $.ajax({
		            type: "POST",
		            url : "${path}/storage/isReadyNodes.do",
		            dataType: "json",
		            contentType: "application/json; charset=utf-8",
		            success: function(msg) {
		            	for(var key in msg) {
		            		$("#"+key).removeClass();
		            		if (msg[key] == true) {
		            			$("#"+key).addClass("label label-success");
		            			$("#"+key).text("运行中");
		            			showButton("editview_"+key, "delete_"+key);
		            		} else {
		            			$("#"+key).addClass("label label-important");
		            			$("#"+key).text("未运行");
		            			showButton("delete_"+key, "editview_"+key);
		            		}
		            	}
		            }
		        });
		    });
		}
		
		setInterval('isReadyNodes()', 5000);
	</script>
	<script src="${path}/js/surfs-node.js"></script>
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
