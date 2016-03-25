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
</style>
</head>
<body>
	<%@include file="../../includes/navigation_block.jsp"%>

	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>

			<li><a href="javascript:showPool()"><i class="icon-hdd"></i>
					<span>存储池管理</span></a></li>
			<li><a href="javascript:showExport()"><i
					class="icon-upload-alt"></i> <span>导出管理</span></a></li>
			<li class="active"><a href="javascript:showBlockUser()"><i
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
					class="current">账户管理</a>
			</div>
			<h1>账户管理</h1>
		</div>

		<div class="container-fluid">
			<div id="blockUser_success" class="alert alert-success alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="blockUser_error" class="alert alert-error alert-block"
				style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<hr>

			<div id="widget-content">
				<span id="addMount"> <a href="#addAlert" data-toggle="modal"
					class="btn btn-danger" onclick=""> <i class="icon icon-plus"></i>
						添加账户
				</a>
				</span>
			</div>
			
			
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>账户列表</h5>
						</div>
						<div class="widget-content nopadding">
							<table class="table table-bordered data-table">
								<thead>
									<tr>
										<!-- <th width="80px">用户ID</th> -->
										<th width="80px">用户名</th>
										<th width="80px">密码</th>
										<th width="80px">真实姓名</th>
										<th width="80px">备注内容</th>
										<th width="150px">创建时间</th>
										<th width="200px">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="blockUser" items="${blockUserList}">
										<tr class="gradeX" id="tr_${blockUser.userId}">
											<%-- <td style="text-align: center;" id="${blockUser.userId}">${blockUser.userId}</td> --%>
											<td style="text-align: center;"><span
												id="userName_${blockUser.userId}">${blockUser.userName}</span></td>
											<td style="text-align: center;"><span
												id="passWord_${blockUser.userId}">${blockUser.passWord}</span></td>
											<td style="text-align: center;"><span
												id="realName_${blockUser.userId}">${blockUser.realName}</span></td>
											<td style="text-align: center;"><span
												id="comment_${blockUser.userId}">${blockUser.comment}</span></td>
											<td style="text-align: center;"><span><fmt:formatDate
														value="${blockUser.createTime}"
														pattern="yyyy年MM月dd日 HH:mm:ss" /></span></td>
											<td style="text-align: center;">
											<span id="editview_${blockUser.userId}"> <a href="#myAlert"
													data-toggle="modal" class="btn btn-info btn-mini"
													onclick="fillingData('${blockUser.userId}','${blockUser.createTime}','${blockUser.userName}','${blockUser.realName}','${blockUser.comment}','${blockUser.passWord}')">
														<i class="icon icon-edit"></i> 编辑
												</a>
											</span>
											 <span id="delete_${blockUser.userId}"> <a
													href="#deleteAlert" data-toggle="modal"
													class="btn btn-inverse btn-mini"
													onclick="javascript:$('#delHidden').val('tr_${blockUser.userId}');$('#userIDHidden').val('${blockUser.userId}');">
														<i class="icon icon-remove"></i> 删除
												</a>
											</span></td>
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
	<%@include file="../../includes/footer.jsp"%>
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
			
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#"
				onclick="confirmDeleteBlockUser('${path}')"><i
				class="icon icon-save"></i> 确定</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 删除账户弹窗 end -->

	<!-- 修改账户弹窗 start -->
	<div id="myAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 修改用户信息
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="get" class="form-horizontal">
						<!-- <div class="control-group">
							<label class="control-label">用户ID :</label>
							<div class="controls">
								<input type="text" class="span2" readonly="readonly"
									id="save_userId" name="userId" />
							</div>
						</div> -->
						<input type="hidden" id="save_userId" name="userId" />
						<div class="control-group">
							<label class="control-label">用户名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_userName" name="userName" onblur="saveUserName()"/>
								<span id="save_userName_error" class="help-inline"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">密码 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_passWord" name="passWord" onblur="savePsw()"/>
								<span id="save_passWord_error"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">真实姓名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_realName" name="realName" onblur="saveRealName()"/>
								<span id="save_realName_error"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">备注内容 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_comment"
									name="comment" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">创建时间 :</label>
							<div class="controls">
								<input type="text" class="span2" readonly="readonly"
									id="save_createTime" name="createTime" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#"
				onclick="saveBlockUser('${path}')"><i class="icon icon-save"></i>
				保存</a> <a data-dismiss="modal" class="btn" href="#"><i
				class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 修改账户弹窗 end -->
	
	<!-- 添加账户弹窗 start -->
	<div id="addAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 添加账户
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="post" class="form-horizontal">
						<input type="hidden" id="add_userId" name="userId" value="1043"/>
						<div class="control-group">
							<label class="control-label">用户名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_userName" name="userName" onblur="chkUserName()" />
								<span id="add_userName_error" class="help-inline"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">密码 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_passWord" name="passWord" onblur="chkPsw()"/>
								<span id="add_passWord_error" class="help-inline"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">真实姓名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_realName" name="realName" onblur="chkRealName()"/>
								<span id="add_realName_error" class="help-inline" ></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">备注内容 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_comment" name="comment" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" 
				onclick="addBlockUser('${path}')"><i class="icon icon-save"></i>
				保存</a> <a data-dismiss="modal" class="btn" href="#"><i
				class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 添加账户弹窗 end -->
	<script type="text/javascript">

	</script>
	<script src="${path}/js/surfs-mount.js"></script>
	<script src="${path}/js/surfs-web-msg.js"></script>
	<script src="${path}/js/surfs-block.js"></script>
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
	<script src="${path}/js/matrix.tables.js"></script>
</body>
</html>
