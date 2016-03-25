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
			<li class="active"><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a></li>
			<li><a href="javascript:showUsers()"><i
					class="icon icon-user-md"></i> <span>存储用户配置</span></a></li>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="home.jsp" title="Go to Home" class="tip-bottom"><i
					class="icon-home"></i> 主页</a> <a href="#" class="current">存储挂载点配置</a>
			</div>
			<h1>存储挂载点配置</h1>
		</div>
		
		<div class="container-fluid">
		
			<!-- message start -->
			<div id="mount_success" class="alert alert-success alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作成功!</h4>
				<span></span>
			</div>
			<div id="mount_error" class="alert alert-error alert-block" style="display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">操作失败!</h4>
				<span></span>
			</div>
			<!-- message end -->
			
			<hr>
			<div id="widget-content">
				<span id="addMount">
					<a href="#addAlert" data-toggle="modal" class="btn btn-danger"
					onclick="">
					<i class="icon icon-plus"></i> 添加挂载点</a>
				</span>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>存储挂载点列表</h5>
						</div>
						<div class="widget-content nopadding">
							<table class="table table-bordered data-table" id="mountTable">
								<thead>
									<tr>
										<th width="80px">挂载点ID</th>
										<th width="80px">挂载路径</th>
										<th width="80px">总空间(GB)</th>
										<th width="80px">已用空间(GB)</th>
										<th width="150px">创建时间</th>
										<th width="280px">操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="mount" items="${mountList}">
										<tr class="gradeX" id="tr_${mount.mountId}">
											<td style="text-align: center;" id="${mount.mountId}">${mount.mountId}</td>
											<input type="hidden" id="hidden_${mount.mountId}" value="${mount.path}">
											<td style="text-align: center;"id="path_${mount.mountId}">${mount.path}</td>
											<fmt:formatNumber var="quota" type="number" value="${mount.quota/(1048576*1024)}"
												maxFractionDigits="0" pattern="#" />
											<td style="text-align: center;"><span class="badge badge-info" id="quota_${mount.mountId}">${quota}</span></td>
											<fmt:formatNumber var="usedQuota" type="number" value="${mount.usedQuota/(1048576*1024)}"
												maxFractionDigits="0" pattern="#" />
											<td style="text-align: center;"><span class="badge">${usedQuota}</span></td>
											<td style="text-align: center;"><span><fmt:formatDate value="${mount.createTime}" pattern="yyyy年MM月dd日 HH:mm:ss"/></span></td>
											<td style="text-align: center;">
												<span id="editview_${mount.mountId}">
													<a href="#myAlert" data-toggle="modal" class="btn btn-info btn-mini"
													onclick="fillingData('${mount.mountId}','${mount.createTime}')">
													<i class="icon icon-edit"></i> 编辑</a>
												</span>
												<span id="delete_${mount.mountId}">
													<a href="#deleteAlert" data-toggle="modal" class="btn btn-inverse btn-mini" 
													onclick="javascript:$('#delHidden').val('tr_${mount.mountId}');$('#mountIDHidden').val('${mount.mountId}');$('#pathHidden').val('${mount.path}')">
													<i class="icon icon-remove"></i> 删除</a>
												</span>
												<span id="addUsersMount_${mount.mountId}">
													<a href="#usersMountAlert" data-toggle="modal" class="btn btn-success btn-mini"
													onclick="addUsersMount('${mount.mountId}', '${mount.path}', '${path}')">
													<i class="icon icon-plus-sign"></i> 未关联用户</a>
												</span>
												<span id="editUsersMount_${mount.mountId}">
													<a href="#usersMountAlert" data-toggle="modal" class="btn btn-danger btn-mini"
													onclick="editUsersMount('${mount.mountId}', '${mount.path}', '${path}')">
													<i class="icon icon-edit"></i> 已关联用户</a>
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
	
	<!-- 删除挂载点弹窗 start -->
	<div id="deleteAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3><span class="icon"><i class="icon-align-justify"></i></span> 删除确认</h3>
		</div>
		<div class="modal-body">
			<p>确定继续执行删除操作吗？</p>
			<input type="hidden" id="delHidden">
			<input type="hidden" id="mountIDHidden">
			<input type="hidden" id="pathHidden">
		</div>
		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="confirmDeleteMount('${path}')"><i class="icon icon-save"></i> 确定</a>
			<a data-dismiss="modal" class="btn" href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 删除挂载点弹窗 end -->

	<!-- 修改挂载点弹窗 start -->
	<div id="myAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 修改挂载点信息
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="get" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">挂载点ID :</label>
							<div class="controls">
								<input type="text" class="span2"
									readonly="readonly" id="save_mountId" name="mountId"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">挂载路径 :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_path" name="path"/>
							</div>
						</div>

						<div class="control-group">
							<label class="control-label">总空间(GB) :</label>
							<div class="controls">
								<input type="text" class="span2" id="save_quota" name="quota"/>
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
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="saveMount('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 修改挂载点弹窗 end -->
	
	<!-- 挂载点用户弹窗 start -->
		<div id="usersMountAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i>挂载点用户</span>
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-title"> 
					<!-- <span class="icon">
            			<input type="checkbox" id="title-checkbox" name="title-checkbox"/>
            		</span> -->
            		<h5>
            			当前挂载点路径为: [ <span id="currentMountPath"></span> ]&nbsp;&nbsp;&nbsp;&nbsp;用户数: [ <span id="usersCount"></span> ]
            			<input type="hidden" id="currentMountId"/>
            		</h5>
          		</div>
	        	<div class="widget-content nopadding">
	            	<table class="table table-bordered table-striped with-check">
	              		<thead>
	                		<tr>
	                			<th style="width: 40px;">选择</th>
		                    	<th width="40px">用户ID</th>
		                  		<th width="80px">用户名</th>
		                  		<th width="80px">真实姓名</th>
		                  		<th>操作权限</th>
	                		</tr>
	              		</thead>
	              		<tbody id="usersList">
	                	</tbody>
	                </table>
                </div>
        	</div>
           	<!-- <div class="pagination alternate" align="right">
            	<ul>
                	<li><a href="#">Prev</a></li>
	                <li class="active"> <a href="#">1</a> </li>
	                <li><a href="#">Next</a></li>
              	</ul>
            </div> -->
		</div>

		<div class="modal-footer">
			<a id="relationUsersMount" data-dismiss="modal" class="btn btn-success" href="#" onclick="relationUsersMount('${path}')"><i
				class="icon icon-save"></i> 保存</a><a id="cancelRelationUsersMount" data-dismiss="modal" class="btn btn-success" href="#" onclick="cancelRelationUsersMount('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 挂载点用户弹窗 end -->
	
	<!-- 添加挂载点弹窗 start -->
	<div id="addAlert" class="modal hide fade">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>
				<span class="icon"><i class="icon-align-justify"></i></span> 添加挂载点
			</h3>
		</div>

		<div class="modal-body">
			<div class="widget-box">
				<div class="widget-content nopadding">
					<form action="#" method="get" class="form-horizontal">
						<div class="control-group">
							<label class="control-label">挂载路径 :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_path" name="path"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">总空间(GB) :</label>
							<div class="controls">
								<input type="text" class="span2" id="add_quota" name="quota"/>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div class="modal-footer">
			<a data-dismiss="modal" class="btn btn-success" href="#" onclick="addMount('${path}')"><i
				class="icon icon-save"></i> 保存</a> <a data-dismiss="modal" class="btn"
				href="#"><i class="icon icon-undo"></i> 取消</a>
		</div>
	</div>
	<!-- 添加挂载点弹窗 end -->
	
	<script type="text/javascript">
	
		
	</script>
	<script src="${path}/js/surfs-web-msg.js"></script>
	<script src="${path}/js/surfs-mount.js"></script>
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
