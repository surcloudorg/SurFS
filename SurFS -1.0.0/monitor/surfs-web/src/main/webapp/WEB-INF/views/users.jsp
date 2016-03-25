<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/uniform.css" />
<link rel="stylesheet" href="${path}/css/select2.css" />
<link rel="stylesheet" href="${path}/css/matrix-style.css" />
<link rel="stylesheet" href="${path}/css/matrix-media.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" />
</head>
<body>
	<%@include file="../includes/navigation.jsp"%>

	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<li><a href="javascript:showGlobleProperties()"><i
					class="icon icon-cog"></i> <span>全局存储配置</span></a></li>
			<li><a href="javascript:showNodeProperties()"><i
					class="icon icon-sitemap"></i> <span>存储节点配置</span></a></li>
			<li><a href="javascript:showListVolumeProperties()"><i
					class="icon icon-inbox"></i> <span>存储卷配置</span></a></li>
			<li><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a></li>
			<li class="active"><a href="javascript:showUsers()"><i
					class="icon-user-md"></i> <span>存储用户配置</span></a></li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="home.jsp" title="Go to Home" class="tip-bottom"><i
					class="icon-home"></i> 主页</a> <a href="#" class="current">存储用户配置</a>
			</div>
			<h1>存储用户配置</h1>
		</div>
		<div class="container-fluid">
			<div id="users_success" class="alert alert-success alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="users_error" class="alert alert-error alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<hr>
			<div id="widget-content">
				<span id="addMount">
					<a href="#addAlert" data-toggle="modal" class="btn btn-danger"
					onclick="">
					<i class="icon icon-plus"></i> 添加用户</a>
				</span>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>存储用户列表</h5>
						</div>
						<div class="widget-content nopadding">
							<table class="table table-bordered data-table" id="mountTable">
								<thead>
									<tr>
										<th width="80px">用户ID</th>
										<th width="80px">用户名</th>
										<th width="80px">密码</th>
										<th width="80px">真实姓名</th>
										<th width="80px">备注内容</th>
										<th width="150px">创建时间</th>
										<th width="200px">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="users" items="${usersList}">
										<tr class="gradeX" id="tr_${users.usersId}">
											<td style="text-align: center;" id="${users.usersId}">${users.usersId}</td>
											<td style="text-align: center;"><span id="usersName_${users.usersId}">${users.usersName}</span></td>
											<td style="text-align: center;"><span id="pwd_${users.usersId}">${users.pwd}</span></td>
											<td style="text-align: center;"><span id="realName_${users.usersId}">${users.realName}</span></td>
											<td style="text-align: center;"><span id="comment_${users.usersId}">${users.comment}</span></td>
											<td style="text-align: center;"><span><fmt:formatDate value="${users.createTime}" pattern="yyyy年MM月dd日 HH:mm:ss"/></span></td>
											<td style="text-align: center;">
												<span id="editview_${users.usersId}">
													<a href="#myAlert" data-toggle="modal" class="btn btn-info btn-mini"
													onclick="fillingData('${users.usersId}','${users.createTime}','${users.usersName}','${users.realName}','${users.comment}','${users.pwd}')">
													<i class="icon icon-edit"></i> 编辑</a>
												</span>
												<span id="delete_${users.usersId}">
													<a href="#deleteAlert" data-toggle="modal" class="btn btn-inverse btn-mini"
													onclick="javascript:$('#delHidden').val('tr_${users.usersId}');$('#usersIDHidden').val('${users.usersId}')">
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
		</div>
	</div>

	<%@include file="../includes/footer.jsp"%>
	
	<!-- 删除用户弹窗 start -->
	<div id="deleteAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3><span class="icon"><i class="icon-align-justify"></i></span> 删除确认</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<input type="hidden" id="delHidden">
			<input type="hidden" id="usersIDHidden">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="confirmDeleteUsers('${path}')"><i class="icon icon-save"></i> 确定</a>
			<a data-dismiss="modal" class="btn" href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 删除用户弹窗 end -->
	
	<!-- 修改用户弹窗 start -->
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
						<div class="control-group">
							<label class="control-label">用户ID :</label>
							<div class="controls">
								<input type="text" class="span2"
									readonly="readonly" id="save_usersId" name="usersId"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">用户名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_usersName" name="usersName"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">密码 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_pwd" name="pwd"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">真实姓名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_realName" name="realName"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">备注内容 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_comment" name="comment"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">创建时间 :</label>
							<div class="controls">
								<input type="text" class="span2" readonly="readonly" id="save_createTime" name="createTime" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="saveUsers('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 修改用户弹窗 end -->
	
	<!-- 添加用户弹窗 start -->
	<div id="addAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 添加用户
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="post" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">用户名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_usersName" name="usersName"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">密码 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_pwd" name="pwd"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">真实姓名 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_realName" name="realName"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">备注内容 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_comment" name="comment"/>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="addUsers('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 添加用户弹窗 end -->
	
	<script type="text/javascript">
		
		
	</script>
	<script src="${path}/js/surfs-users.js"></script>
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
