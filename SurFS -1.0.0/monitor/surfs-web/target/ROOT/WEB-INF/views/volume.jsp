<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/uniform.css" />
<link rel="stylesheet" href="${path}/css/select2.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" />
</head>
<body onload="showSpanceAndThread()">
<%@include file="../includes/navigation.jsp"%>

	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<li><a href="javascript:showGlobleProperties()"><i
					class="icon icon-cog"></i> <span>全局存储配置</span></a></li>
			<li><a href="javascript:showNodeProperties()"><i
					class="icon icon-sitemap"></i> <span>存储节点配置</span></a></li>
			<li class="active"><a
				href="javascript:showListVolumeProperties()"><i
					class="icon icon-inbox"></i> <span>存储卷配置</span></a></li>
			<li><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a></li>
			<li><a href="javascript:showUsers()"><i class="icon-user-md"></i>
					<span>存储用户配置</span></a></li>
			<li class="content">总磁盘空间已使用 <span id="percent"></span>
				<div class="progress progress-mini progress-danger active progress-striped">
					<div class="bar" id="process_bar"></div>
				</div>
				<div class="stat"><span id="usage"></span> / <span id="total"></span> GB</div>
			</li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="home.jsp" title="Go to Home" class="tip-bottom"><i
					class="icon-home"></i> 主页</a> <a href="#" class="current">存储卷配置</a>
			</div>
			<h1>存储卷配置</h1>
		</div>
		<div class="container-fluid">
			<div id="volume_success" class="alert alert-success alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="volume_error" class="alert alert-error alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<hr>
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>存储卷列表</h5>
						</div>
						<div class="widget-content nopadding">
							<table class="table table-bordered data-table" id="volumeTable">
								<thead>
									<tr>
										<th width="150px">卷ID</th>
										<th width="150px">服务节点</th>
										<th width="80px">最大文件句柄数</th>
										<th width="80px">状态</th>
										<th width="80px"><a href="#" title="优先级从0-9，由低到高" class="tip-top"> 优先级 </a></th>
										<th width="80px">总空间（GB）</th>
										<th width="80px">剩余空间（GB）</th>
										<th width="80px">当前最大文件句柄数</th>
										<th width="150px">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="volumeProperties" items="${listVolumeProperties}">
										<tr class="gradeX" id="tr_${volumeProperties.volumeID}">
											<td style="text-align: center;" id="${volumeProperties.volumeID}">${volumeProperties.volumeID}</td>
											<td style="text-align: center;">${volumeProperties.serverHost}</td>
											<td style="text-align: center;"><span>${volumeProperties.fileHandleNum}</span></td>
											<td style="text-align: center;">
												<span class="label" id="status_${volumeProperties.volumeID}"></span>
											</td>
											<td style="text-align: center;"><span class="badge badge-inverse"> ${volumeProperties.priority}</span></td>
											<td style="text-align: center;"><span id="total_${volumeProperties.volumeID}" class="badge badge-info"></span></td>
											<td style="text-align: center;"><span id="free_${volumeProperties.volumeID}" class="badge"></span></td>
											<td style="text-align: center;">
												<a href="#" title="点击查看文件句柄详细信息" class="tip-top" onclick="showDetailThread('${volumeProperties.volumeID}')">
													<span id="currentThread_${volumeProperties.volumeID}" class="badge badge-success"></span>
												</a>
											</td>
											<td style="text-align: center;">
												<span id="editview_${volumeProperties.volumeID}">
													<a href="#myAlert" data-toggle="modal" class="btn btn-info btn-mini"
													onclick="fillingData('${volumeProperties.volumeID}','${volumeProperties.serverHost}','${volumeProperties.priority}','${volumeProperties.fileHandleNum}')">
													<i class="icon icon-edit"></i> 编辑</a>
												</span>	
												<span id="delete_${volumeProperties.volumeID}">
													<a href="#deleteAlert" data-toggle="modal" class="btn btn-inverse btn-mini" 
													onclick="javascript:$('#delHidden').val('tr_${volumeProperties.volumeID}');$('#volumeIDHidden').val('${volumeProperties.volumeID}')">
													<i class="icon icon-remove"></i> 删除</a>
												</span>	
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid" id="detailThread" style="display: none;">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-list"></i>
							</span>
							<h5 id="threadType">
							</h5>
						</div>
						<div class="widget-content">
							<span id="threadInfo" style="overflow-y: scroll;display: block; height: 250px"> 
							</span>
							<br>
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
			<input type="hidden" id="volumeIDHidden">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="confirmDeleteNode()"><i class="icon icon-save"></i> 确定</a>
			<a data-dismiss="modal" class="btn" href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>

	<div id="myAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 卷信息
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="get" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">卷ID :</label>
							<div class="controls">
								<input type="text" class="span2"
									readonly="readonly" id="volumeID" name="volumeID"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">服务节点 :</label>
							<div class="controls">
								<input type="text" class="span2"
									readonly="readonly" id="serverHost" name="serverHost"/>
							</div>
						</div>

						<div class="control-group">
							<label class="control-label">优先级 :</label>
							<div class="controls">
								<input type="text" class="span2" id="priority" name="priority"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">线程操作上限数 :</label>
							<div class="controls">
								<input type="text" class="span2" id="fileHandleNum" name="fileHandleNum" />
							</div>
						</div>
						<!-- <div class="control-group">
							<label class="control-label">读操作上限数 :</label>
							<div class="controls">
								<input type="text" class="span2" id="readThreadNum" name="readThreadNum" />
							</div>
						</div> -->
						<div class="control-group">
							<label class="control-label">当前并发线程数 :</label>
							<div class="controls">
								<input type="text" class="span2" id="thread" name="thread"
									readonly="readonly" />
							</div>
						</div>
						<!-- <div class="control-group">
							<label class="control-label">当前并发读线程数 :</label>
							<div class="controls">
								<input type="text" class="span2" id="read" name="read" 
									readonly="readonly" />
							</div>
						</div> -->
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="saveVolumeProperties()"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- pop-up dialogs end -->

	<script type="text/javascript">
		function fillingData(volumeID, serverHost, priority, fileHandleNum) {
			$("#volumeID").val(volumeID);
			$("#serverHost").val(serverHost);
			$("#priority").val(priority);
			$("#thread").val($("#currentThread_"+volumeID).text());
			$("#fileHandleNum").val(fileHandleNum);
		}

		function saveVolumeProperties() {
			var fileHandleNum = $("#fileHandleNum").val();
			$.ajax({
				type : "POST",
				url : "${path}/storage/saveVolumeProperties.do",
				async : false,
				cache : false,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
		        data: JSON.stringify({
		        	"volumeID": $("#volumeID").val(),
		        	"serverHost": $("#serverHost").val(),
		        	"priority": $("#priority").val(),
		        	"fileHandleNum": fileHandleNum
		        }),
				success : function(success) {
					showMsgOperate($("#volume_success"), $("#volume_error"), "修改卷成功");
				},
				error: function(error) {
					showMsgOperate($("#volume_error"), $("#volume_success"), "修改卷失败");
				}
			});
		}
		
		function showThreadNum() {
			var currentThreadNum = jQuery.parseJSON('${currentThreadNum}');
			setThreadNum(currentThreadNum);
		}
		
		function setThreadNum(currentThreadNum) {
			for(var i in currentThreadNum) {
				var volumeID = currentThreadNum[i]["volumeID"];
				var threadNum = currentThreadNum[i]["currentThreadNum"];
				
				if (threadNum == null) {
					$("#status_"+volumeID).addClass("label label-important");
        			$("#status_"+volumeID).text("未运行");
        			$("#delete_"+volumeID).show();
        			$("#editview_"+volumeID).hide();
				} else {
					$("#status_"+volumeID).addClass("label label-success");
        			$("#status_"+volumeID).text("运行中");
        			$("#currentThread_"+volumeID).text(threadNum);
    				$("#delete_"+volumeID).hide();
        			$("#editview_"+volumeID).show();
				}
			}
		}
		
		setInterval('queryVolumeSpace()', 5000);
		setInterval('queryCurrentThreadNum()', 5000);
		
		function queryCurrentThreadNum() {
			var arrayVolumeID = new Array();
			$("#volumeTable tbody tr td[id]").each(function(i) {
				arrayVolumeID.push($(this).attr("id"));
			});
	        $(document).ready(function() {
	            $.ajax({
	                type: "POST",
	                url : "${path}/storage/queryCurrentThreadNum.do",
	                dataType: "json",
	                contentType: "application/json; charset=utf-8",
	                data: JSON.stringify(arrayVolumeID),
	                success: function(msg) {
	                	setThreadNum(msg);
	                }
	            });
	        });
	    }
		
		function queryVolumeSpace() {
			var arrayVolumeID = new Array();
			$("#volumeTable tbody tr td[id]").each(function(i) {
				arrayVolumeID.push($(this).attr("id"));
			});
	        $(document).ready(function() {
	            $.ajax({
	                type: "POST",
	                url : "${path}/storage/queryVolumeSpace.do",
	                dataType: "json",
	                contentType: "application/json; charset=utf-8",
	                data: JSON.stringify(arrayVolumeID),
	                success: function(msg) {
	                	setVolumeSpace(msg, false);
	                }
	            });
	        });
	    }
		
		function showVolumeSpace() {
			var volumeSpaces = jQuery.parseJSON('${volumeSpaces}');
			setVolumeSpace(volumeSpaces, true);
		}
		
		function setTotalVolumeSpace(disktotal, diskfree) {
			$("#total").text(disktotal.toFixed(2));
			$("#usage").text((disktotal - diskfree).toFixed(2));
			var percent = ((disktotal-diskfree) / disktotal * 100).toFixed(0) + "%";
			$("#percent").text(percent);
			$("#process_bar").css("width", percent);
		}
		
		function setVolumeSpace(volumeSpaces, isShowTotalAndFree) {
			var disktotal = 0;
			var diskfree = 0;
			for(var i in volumeSpaces) {
				var volumeID = volumeSpaces[i]["volumeID"];
				var totalSpace = volumeSpaces[i]["totalSpace"];
				var freeSpace = volumeSpaces[i]["freeSpace"];
				var total = (totalSpace / 1048576 / 1024).toFixed(2);
				var free = (freeSpace / 1048576 / 1024).toFixed(2);
				$("#total_"+volumeID).text(total);
				$("#free_"+volumeID).text(free);
				disktotal = Number(disktotal) + Number(total);
				diskfree = Number(diskfree) + Number(free);;
			}
			if (isShowTotalAndFree)
				setTotalVolumeSpace(disktotal, diskfree);
		}
		
		function showSpanceAndThread() {
			showVolumeSpace();
			showThreadNum();
		}
		
		function showDetailThread(volumeID) {
			$.ajax({
				type : "POST",
				url : "${path}/storage/showDetailThread.do",
				async : false,
				cache : false,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
		        data: volumeID,
				success : function(msg) {
					var threadInfo = msg["threadInfo"];
					$("#detailThread").hide();
					if (threadInfo != null && threadInfo != "") {
						$("#detailThread").show();
						$("#threadInfo").text("");
						$("#threadInfo").append(msg["threadInfo"]);
					}
				}
			});
		}
		
		function confirmDeleteNode() {
			var volumeIDHidden = $("#volumeIDHidden").val();
			$.ajax({
				type : "POST",
				url : "${path}/storage/deleteVolume.do",
				async : false,
				cache : false,
				contentType: "application/json; charset=utf-8",
		        data:volumeIDHidden,
		        success : function(success) {
		        	var delVolumeID = $("#delHidden").val();
					$("#"+delVolumeID).remove();
				}
			});
		}
		
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
