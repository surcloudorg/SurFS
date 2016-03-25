<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
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

<style type="text/css">
/* a:link { 
	color:green; 
} */
</style>
</head>
<body>
	<%@include file="../../includes/navigation_block.jsp"%>
	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<li><a href="javascript:showPool()"><i class="icon-hdd"></i>
					<span>存储池管理</span></a></li>
			<li class="active"><a href="javascript:showExport()"><i
					class="icon-upload-alt"></i> <span>导出管理</span></a></li>
			<li><a href="javascript:showBlockUser()"><i
					class="icon-user-md"></i> <span>账户管理</span></a></li>
			<!-- <li><a href="javascript:showBlockTarget()"><i
					class="icon-user-md"></i> <span>BlockTarget测试</span></a></li>
			<li><a href="javascript:showTarget()"><i
					class="icon-user-md"></i> <span>Target管理</span></a></li> -->
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="${path}/views/home.jsp" title="Go to Home"
					class="tip-bottom"><i class="icon-home"></i> 主页</a> <a href="#"
					class="current">导出管理</a>
			</div>
			<h1>导出管理</h1>
		</div>
		<div class="container-fluid">
			<div id="export_success" class="alert alert-success alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="export_error" class="alert alert-error alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<hr>

			<div id="widget-content">
				<c:forEach var="exportInfo" items="${exportInfos}"
					varStatus="status">
				<div class="row-fluid">
					<div class="span10">
						<div class="widget-box collapsible">
							<div class="widget-title">
								<a href="#${status.index}" data-toggle="collapse"> <span
									class="icon"><i class="icon-chevron-down"></i> </span> <h5>Hostname:${exportInfo.hostname} [${exportInfo.ip}]</h5>
								</a> <span class="label label-success">服务状态正常</span> <a
									href="#addTargetAlert" data-toggle="modal" class="tip"
									onclick="fillAddTarget('${exportInfo.ip}')"><h5
										style="color: green;">[+添加target]</h5> </a>
							</div>

							<div class="${status.index eq 0 ? 'collapse in' : 'collapse'}"
									id="${status.index}">
							<div>
								<div class="widget-content">
									<c:forEach var="info" items="${exportInfo.info}"
											varStatus="infoStatus">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th style="height: 28px; vertical-align: middle;">
													target名称：<span id="target_${exportInfo.ip}_${info.target}">${info.target}</span>
												</th>
												<th
													style="vertical-align: middle; text-align: center; width: 100px; border-left-style: none;">
													<a href="#deleteTargetAlert" data-toggle="modal"
													data-original-title="删除target" class="tip"
													onclick="javascript:$('#targetHidden').val('${info.target}');$('#ipHidden').val('${exportInfo.ip}');">
														<i class="icon icon-remove"
														style="font-size: large; color: red;"></i>
												</a>
												</th>
												<!-- <th style="border-left-style: none; text-align: right; width: 100px; vertical-align: middle;">
															<a href="#addTargetAlert" data-toggle="modal" class="tip" onclick="">
																<span style="color: green;">[+添加target]</span>
															</a>
														</th> -->
											</tr>
										</thead>
									</table>
									<div class="row-fluid">
										<div class="span4">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th style="vertical-align: middle;" colspan="2">
															客户端login信息</th>
													</tr>
												</thead>
												<tbody id="loginList_${status.index}_${infoStatus.index}">
													<c:forEach var="login" items="${info.login}">
														<tr>
															<td style="width: 250px;">login ip：${login.ip}</td>
															<td>${login.initiator}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
										<div class="span3">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th style="vertical-align: middle;" colspan="3">
																已授权用户
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
															<span id="addBlockUserTarget_'${info.target}'">
																<a href="#blockUserTargetAlert" data-toggle="modal" data-original-title="取消授权"
																	class="tip" onclick="addBlockUserTarget('${info.target}','${path}', '${exportInfo.ip}')">
																	<span style="color: green;">[+添加授权]</span>
																</a>
															 </span> 
															 
														</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="blockUser" items="${info.blockUser}" varStatus="blockUserStatus">
												 	<tr>
														<td style="width: 180px;">用户名：${blockUser.userName}</td>
														<td style="width: 125px; vertical-align: middle; text-align: center;">
															<a href="#deleteAlert" data-toggle="modal"
																data-original-title="删除用户" class="tip"
																onclick="javascript:$('#delHidden').val('tr_${blockUser.userId}');$('#userIDHidden').val('${blockUser.userId}');$('#userTargetHidden').val('${info.target}');">
																<i class="icon icon-remove"
																style="font-size: large; color: red;"></i>
															</a>
														</td>
													</tr> 
													</c:forEach>
												</tbody>
											</table>
										</div>
										<div class="span5">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th style="vertical-align: middle;" colspan="2">设备信息</th>
														<th
															style="width: 130px; text-align: center; border-left-style: none;">
															<a href="#addDeviceAlert" data-toggle="modal" class="tip"
															onclick="fillAddDevice('${path}', '${info.target}', '${exportInfo.ip}', '${status.index}', '${infoStatus.index}')"><span
																style="color: green;">[+添加设备]</span> </a>
																
														</th>
													</tr>
												</thead>
												<tbody id="deviceList_${status.index}_${infoStatus.index}">
													<c:forEach var="device" items="${info.device}" varStatus="devStatus">
													<tr
														id="${status.index}_${infoStatus.index}_${devStatus.index}">
														<td style="width: 250px;">device名称：${device.vol}</td>
														<!-- <td style="width: 250px;">device名称：/dev/zvol/pool2/test2</td> -->
														<td style="width: 150px;">容量：${device.cap}</td>
														<!-- <td style="width: 130px;">容量：85899MB</td> -->
														<td style="vertical-align: middle; text-align: center;">

															<a href="#deleteDeviceAlert" data-toggle="modal"
															data-original-title="删除设备" class="tip"
															onclick="javascript:$('#removeDeviceHidden').val('${status.index}_${infoStatus.index}_${devStatus.index}');$('#deleteDeviceError').hide();">
																<i class="icon icon-remove"
																style="font-size: large; color: red;"></i>
														</a>

														</td>
														<input type="hidden"
															id="device_${status.index}_${infoStatus.index}_${devStatus.index}"
															value="${device.vol}" />
														<input type="hidden"
															id="target_${status.index}_${infoStatus.index}_${devStatus.index}"
															value="${info.target}">
														<input type="hidden"
															id="ip_${status.index}_${infoStatus.index}_${devStatus.index}"
															value="${exportInfo.ip}">
														<input type="hidden"
															id="remove_${status.index}_${infoStatus.index}_${devStatus.index}"
															value="${status.index}_${infoStatus.index}_${devStatus.index}">
													</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>

									</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</div>
				</c:forEach>
			</div>

		</div>
	</div>

	<!-- pop-up dialogs start -->
	<div id="deleteTargetAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 删除确认
			</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<input type="hidden" id="targetHidden"> 
			<input type="hidden" id="ipHidden"> 
			<input type="hidden" id="devStatus">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#"
				onclick="confirmDeleteTarget('${path}')"><i
				class="icon icon-save"></i> 确定</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- pop-up dialogs end -->

	<!-- pop-up dialogs start -->
	<div id="deleteDeviceAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 删除确认
			</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<p id="deleteDeviceError"
				style="display: none; font-weight: bold; color: red;">该设备已经在客户端login，不能删除</p>
			<!-- <input type="hidden" id="deviceHidden">
			<input type="hidden" id="deviceTargetHidden">
			<input type="hidden" id="ipHidden"> -->
			<input type="hidden" id="removeDeviceHidden">
		</div>
		<div class="modal-footer">
			<a class="btn btn-success" href="#"
				onclick="confirmDeleteDevice('${path}')"><i
				class="icon icon-save"></i> 确定</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- pop-up dialogs end -->
	
	<!-- 删除账户弹窗 start -->
	<div id="deleteAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 删除确认
			</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<input type="hidden" id="delHidden"> 
			<input type="hidden" id="userIDHidden">
			<input type="hidden" id="userTargetHidden">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#"
				onclick="confirmDeleteBlockUserTarget('${path}')">
				<i class="icon icon-save"></i> 确定</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 删除账户弹窗 end -->
	
	<!-- 添加卷弹窗 start -->
	<div id="addTargetAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span>
				添加target
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="get" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">target名称 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_target"
									name="add_target" /> <span id="add_target_error"
									class="help-inline" style="font-size: small; color: red;"></span>
							</div>
						</div>
						<input type="hidden" class="span2" id="add_target_ip"
							name="add_target_ip" />
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a class="btn btn-success" href="#" onclick="addTarget('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 添加卷弹窗 end -->

	<!-- 挂载点用户弹窗 start -->
	<div id="addDeviceAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i>添加设备</span>
			</h3>
		</div>
		<div class="modal-body">
			<center>
				<span id="addDeviceError"
					style="font-weight: bold; color: red; display: none;">
					请选中需要添加的设备 </span>
			</center>
			<div class="widget-box">
				<div class="widget-title">
					<!-- <span class="icon">
            			<input type="checkbox" id="title-checkbox" name="title-checkbox"/>
            		</span> -->
					<h5>
						当前target为: [ <span id="add_device_target"></span>
						]&nbsp;&nbsp;&nbsp;&nbsp; <input type="hidden" id="add_device_ip" />
						<input type="hidden" id="index" />
					</h5>
				</div>
				<div class="widget-content nopadding">
					<table class="table table-bordered table-striped with-check">
						<thead>
							<tr>
								<th style="width: 40px;">选择</th>
								<th width="80px">设备名</th>
								<th width="80px">容量</th>
							</tr>
						</thead>
						<tbody id="volDevList">
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a class="btn btn-success" href="#" onclick="addDevice('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 挂载点用户弹窗 end -->

	<!-- 添加账户权限弹窗 start -->
		<div id="blockUserTargetAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i>target用户</span>
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-title"> 
					<!-- <span class="icon">
            			<input type="checkbox" id="title-checkbox" name="title-checkbox"/>
            		</span> -->
            		<h5>
            			当前可授权用户数: [ <span id="blockUserCount"></span> ]
            			<input type="hidden" id="currentTarget"/>
            			<input type="hidden" id="currentIp"/>
            		</h5>
          		</div>
	        	<div class="widget-content nopadding">
	            	<table class="table table-bordered table-striped with-check">
	              		<thead>
	                		<tr>
	                			<th style="width: 40px;">选择</th>
		                    	<th width="40px">用户ID</th>
		                  		<th width="80px">用户名</th>
		                  		<th width="80px">密码</th>
		                  		<th width="80px">真实姓名</th>
	                		</tr>
	              		</thead>
	              		<tbody id="blockUserList">
	                	</tbody>
	                </table>
                </div>
        	</div>
		</div>

		<div class="modal-footer">
			<a id="relationBlockUserTarget" data-dismiss="modal" class="btn btn-success" href="#" onclick="relationBlockUserTarget('${path}')"><i
				class="icon icon-save"></i> 保存</a><a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 添加账户权限弹窗 end -->

	<%@include file="../../includes/footer.jsp"%>

	<script type="text/javascript">
		
	</script>
	<%-- <script src="${path}/js/surfs-target.js"></script> --%>
	<script src="${path}/js/surfs-web-msg.js"></script>
	<script src="${path}/js/surfs-block.js"></script>
	<%-- <script src="${path}/js/surfs-target.js"></script> --%>
	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script>
	<script src="${path}/js/bootstrap.min.js"></script>
	<script src="${path}/js/bootstrap-colorpicker.js"></script>
	<script src="${path}/js/bootstrap-datepicker.js"></script>
	<script src="${path}/js/masked.js"></script>
	<script src="${path}/js/jquery.uniform.js"></script>
	<script src="${path}/js/select2.min.js"></script>
	<script src="${path}/js/matrix.js"></script>
	<script src="${path}/js/jquery.peity.min.js"></script>
	<script src="${path}/js/bootstrap-wysihtml5.js"></script>
	<script src="${path}/js/jquery.dataTables.min.js"></script>
</body>
</html>
