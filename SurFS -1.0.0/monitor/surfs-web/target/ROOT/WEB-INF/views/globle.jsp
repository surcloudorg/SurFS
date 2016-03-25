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

	<%@include file="../includes/navigation.jsp"%>

	<!--start sidebar-menu-->
	<div id="sidebar">
		<ul>
			<li class="active"><a href="javascript:showGlobleProperties()"><i
					class="icon-cog"></i> <span>全局存储配置</span></a></li>
			<li><a href="javascript:showNodeProperties()"><i
					class="icon-sitemap"></i> <span>存储节点配置</span></a></li>
			<li><a href="javascript:showListVolumeProperties()"><i
					class="icon-inbox"></i> <span>存储卷配置</span></a></li>
			<li><a href="javascript:showMountPoint()"><i
					class="icon-folder-close"></i> <span>存储挂载点配置</span></a>
			<li><a href="javascript:showUsers()"><i class="icon-user-md"></i>
					<span>存储用户配置</span></a>
		</ul>
	</div>
	<!--close-left-menu-stats-sidebar-->

	<div id="content">
		<div id="content-header">
			<div id="breadcrumb">
				<a href="home.jsp" title="Go to Home" class="tip-bottom"><i
					class="icon-home"></i> 主页</a> <a href="#" class="current">全局存储配置</a>
			</div>
			<h1>全局存储配置</h1>
		</div>
		<div class="container-fluid">
			<c:if test="${save_success}">
				<div id="success_alert" class="alert alert-success alert-block" style="width: 1024px;">
					<a class="close" data-dismiss="alert" href="#">×</a>
					<h4 class="alert-heading">保存成功!</h4>
					全局配置信息保存成功，系统会在您设置的“加载服务参数间隔”后，加载配置信息。
				</div>
			</c:if>
			<div id="error_alert" class="alert alert-error alert-block" style="width: 1024px;display: none;">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4 class="alert-heading">保存失败!</h4>
				全局配置信息保存失败，请查看系统错误日志信息。
			</div>
			<hr>
			<div class="row-fluid">
				<div class="span8">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>全局存储信息</h5>
						</div>
						<div class="widget-content nopadding">
							<c:set var="globleProperties" value="${globleProperties}" />
							<form id="globle_form" action="${path}/storage/saveGlobleProperties.do"
								method="post" class="form-horizontal">
								<div class="control-group">
									<label class="control-label">IO操作数据块长度 :</label>
									<%-- <fmt:formatNumber var="blocksize" type="number"
											value="${globleProperties.blocksize/1024}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="blocksize"
											name="blocksize" value="${globleProperties.blocksize}" />
										<span><b>&nbsp;KB</b>&nbsp;每次IO操作数据块大小（字节），上传时块越大，访问磁盘频率越低</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">操作失败再试间隔 :</label>
									<%-- <fmt:formatNumber var="errRetryInterval" type="number"
											value="${globleProperties.errRetryInterval/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="errRetryInterval"
											name="errRetryInterval"
											value="${globleProperties.errRetryInterval}" />
										<span><b>&nbsp;秒</b>&nbsp;失败再试间隔，一般是服务节点错误或卷错误</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">失败重试次数 :</label>
									<div class="controls">
										<input type="text" class="span4" id="errRetryTimes"
											name="errRetryTimes" value="${globleProperties.errRetryTimes}" />
										<span><b>&nbsp;次</b>&nbsp;失败重试次数，一般是服务节点错误或卷错误</span>
									</div>
								</div>
								<%-- <div class="control-group">
									<label class="control-label">等待并发锁超时时间 :</label>
									<fmt:formatNumber var="lockTimeout" type="number"
											value="${globleProperties.lockTimeout/1000}"
											maxFractionDigits="0" pattern="#" />
									<div class="controls">
										<input type="text" class="span4" id="lockTimeout"
											name="lockTimeout" value="${lockTimeout}" />
										<span><b>&nbsp;秒</b>&nbsp;等待并发锁超时时间，排队超时，包括读写并发锁</span>
									</div>
								</div> --%>
								<div class="control-group">
									<label class="control-label">存储服务扫描目录 :</label>
									<div class="controls">
										<input type="text" class="span4" id="mntPoint" name="mntPoint"
											value="${globleProperties.mntPoint}"/> 
										<span>&nbsp;存储服务需要在本机该目录下扫描卷</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">更新目录配额间隔时间 :</label>
									<%-- <fmt:formatNumber var="checkSpaceInterval" type="number"
											value="${globleProperties.checkSpaceInterval/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="checkSpaceInterval"
											name="checkSpaceInterval"
											value="${globleProperties.checkSpaceInterval}" />
										<span><b>&nbsp;秒</b>&nbsp;存储服务需要定期更新目录配额，来获得文件及目录的用量</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">网络访问超时时间 :</label>
									<%-- <fmt:formatNumber var="readTimeout" type="number"
											value="${globleProperties.readTimeout/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="readTimeout"
											name="readTimeout" value="${globleProperties.readTimeout}" />
										<span><b>&nbsp;秒</b>&nbsp;网络超时，客户端访问存储节点超时时长</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">网络连接超时时间 :</label>
									<%-- <fmt:formatNumber var="connectTimeout" type="number"
											value="${globleProperties.connectTimeout/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="connectTimeout"
											name="connectTimeout" value="${globleProperties.connectTimeout}" />
										<span><b>&nbsp;秒</b>&nbsp;网络超时，客户端与存储节点建立连接超时时长</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">文件句柄超时关闭时间 :</label>
									<%-- <fmt:formatNumber var="sessionTimeout" type="number"
											value="${globleProperties.sessionTimeout/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="sessionTimeout"
											name="sessionTimeout" value="${globleProperties.sessionTimeout}" />
										<span><b>&nbsp;秒</b>&nbsp;文件句柄超时关闭时间</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">异步队列长度 :</label>
									<div class="controls">
										<input type="text" class="span4" id="writeQueue"
											name="writeQueue" value="${globleProperties.writeQueue}" />
										<span><b>&nbsp;长度</b>&nbsp;异步写入队列长度(0-20)</span>
									</div>
								</div>
								<div class="control-group">
					            	<label class="control-label">负载均衡策略 :</label>
					             	<div class="controls">
					                	<select style="width: 278px" id="balanceRule" name="balanceRule">
					                  		<option value="0">就近原则</option>
					                  		<option value="1">轮询节点</option>
					                  		<option value="2">节点并发线程数</option>
					                	</select>
					                	<span>&nbsp;存储节点访问的负载均衡策略，0就近原则、 1轮询节点 、2以节点并发线程数</span>
					              	</div>
					            </div>
					            <div class="control-group">
					            	<label class="control-label">缓存策略 :</label>
					             	<div class="controls">
					                	<select style="width: 278px" id="usecache" name="usecache">
					                  		<option value="0">使用缓存</option>
					                  		<option value="1">同步数据</option>
					                  		<option value="2">同步元数据</option>
					                	</select>
					                	<span>&nbsp;存储节点访问的缓存策略，0使用缓存、 1同步数据 、2同步元数据</span>
					              	</div>
					            </div>
								<div class="control-group">
									<label class="control-label">加载服务参数间隔 :</label>
									<%-- <fmt:formatNumber var="reloadInterval" type="number"
											value="${globleProperties.reloadInterval/1000}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4" id="reloadInterval"
											name="reloadInterval" value="${globleProperties.reloadInterval}" />
										<span><b>&nbsp;秒</b>&nbsp;客户端，服务端重新加载服务参数的时间间隔</span>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">卷空间少于多少系统报警 :</label>
									<%-- <fmt:formatNumber var="spaceThresholdSize" type="number"
											value="${globleProperties.spaceThresholdSize/1073741824}"
											maxFractionDigits="0" pattern="#" /> --%>
									<div class="controls">
										<input type="text" class="span4 required" id="spaceThresholdSize"
											name="spaceThresholdSize" value="${globleProperties.spaceThresholdSize}" />
										<span><b>&nbsp;GB</b>&nbsp;服务器卷空间少于该值，系统将发出警报</span>
									</div>
								</div>
								<div class="form-actions">
									<input type="submit" class="btn btn-success" value="保存配置" />
									<!-- <button type="submit" class="btn btn-success" id="save_globle">
										<i class="icon icon-save" onclick="ajaxSaveGlobleProperties()"></i> 保存配置
									</button> -->
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<%@include file="../includes/footer.jsp"%>
	<%-- <script src="${path}/js/surfs-globle.js"></script> --%>
	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/jquery.ui.custom.js"></script> 
	<script src="${path}/js/bootstrap.min.js"></script> 
	<script src="${path}/js/bootstrap-colorpicker.js"></script> 
	<script src="${path}/js/bootstrap-datepicker.js"></script> 
	<script src="${path}/js/masked.js"></script> 
	<script src="${path}/js/jquery.uniform.js"></script> 
	<script src="${path}/js/select2.min.js"></script> 
	<script src="${path}/js/matrix.js"></script> 
	<%-- <script src="${path}/js/matrix.form_common.js"></script> --%>
	<script src="${path}/js/wysihtml5-0.3.0.js"></script> 
	<script src="${path}/js/jquery.peity.min.js"></script> 
	<script src="${path}/js/bootstrap-wysihtml5.js"></script> 
	<script src="${path}/js/jquery.validate.js"></script> 
	<script type="text/javascript">
		$("#balanceRule").val("${globleProperties.balanceRule}");
		$("#usecache").val("${globleProperties.usecache}");
		$().ready(function() {
			$("#globle_form").validate({
				rules : {
					blocksize: {
						required: true,
						digits: true
					},
					errRetryInterval: {
						required: true,
						digits: true
					},
					errRetryTimes: {
						required: true,
						digits: true
					},
					/* lockTimeout: {
						required: true,
						digits: true
					}, */
					checkSpaceInterval: {
						required: true,
						digits: true
					},
					readTimeout: {
						required: true,
						digits: true
					},
					connectTimeout: {
						required: true,
						digits: true
					},
					/* connectIdleTime: {
						required: true,
						digits: true
					},
					maxConnectNum: {
						required: true,
						digits: true
					}, */
					reloadInterval: {
						required: true,
						digits: true
					},
					spaceThresholdSize: {
						required: true,
						digits: true
					}
				},
				messages : {
					blocksize: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					errRetryInterval: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					errRetryTimes: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					/* lockTimeout: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					}, */
					checkSpaceInterval: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					readTimeout: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					connectTimeout: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					/* connectIdleTime: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					maxConnectNum: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					}, */
					reloadInterval: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					},
					spaceThresholdSize: {
						required: "这项配置必须填写，不能为空",
						digits: "这项必须输入整数"
					}
				},
				errorClass: "help-inline",
				errorElement: "span",
				highlight:function(element, errorClass, validClass) {
					if ($(element).next().next().is('span'))
						$(element).next().next().hide();
					else
						$(element).next().hide();
					$(element).parents('.control-group').removeClass('success');
					$(element).parents('.control-group').addClass('error');
				},
				unhighlight: function(element, errorClass, validClass) {
					$(element).next().next().show();
					$(element).parents('.control-group').removeClass('error');
					$(element).parents('.control-group').addClass('success');
				}
			});
			
			// 路径验证   
			/* jQuery.validator.addMethod("mntPoint", function(value, element) {   
			    var lnxPath  = /^\/([/w]+\/?)+$/i;
			    return this.optional(element) || (lnxPath.test(value));
			}, "请正确填写您的扫描目录"); */
		});
	</script>
</body>
</html>
