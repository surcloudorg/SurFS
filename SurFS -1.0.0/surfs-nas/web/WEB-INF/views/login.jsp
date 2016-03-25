<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../includes/taglib.jsp"%>
<title>Sursen Admin</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${path}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${path}/css/matrix-login.css" />
<link href="${path}/font-awesome/css/font-awesome.css" rel="stylesheet" />
<%-- <link rel="stylesheet" href="${path}/css/uniform.css" />
<link rel="stylesheet" href="${path}/css/select2.css" /> --%>
</head>
<body>
	<div id="loginbox">
		<form id="loginform" class="form-vertical" method="post"
			action="${path}/login.do">
			<div class="control-group normal_text">
				<h3>
					<img src="${path}/img/logo.png" alt="Logo" />
				</h3>
			</div>
			<div id="result"></div>
			<%-- <c:if test="${param['status'] eq 'login_error'}"> --%>
				<div class="controls">
					<div class="main_input_box">
						<!-- <h4 style="color: red;">用  户  名  或  密  码  错  误</h4> -->
						<h4 style="color: red;">${error}</h4>
						<h4 style="color: red;">${param['error']}</h4>
					</div>
				</div>
			<%-- </c:if> --%>
			<c:if test="${param['status'] eq 'access_error'}">
				<div class="controls">
					<div class="main_input_box">
						<h4 style="color: red;">没有登陆系统的用户没有访问权限</h4>
					</div>
				</div>
			</c:if>
			<div class="controls" id="dataCenterError" style="display: none;">
					<div class="main_input_box">
						<h4 style="color: red;">请  选  择  数  据  中  心</h4>
					</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<div class="main_input_box">
						<span class="add-on bg_lg"><i class="icon-user"></i></span><input
							type="text" placeholder="用户名" name="username" onkeypress="enterSubmit()"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<div class="main_input_box">
						<span class="add-on bg_ly"><i class="icon-lock"></i></span><input
							type="password" placeholder="密码" name="passWord" onkeypress="enterSubmit()"/>
					</div>
				</div>
			</div>
			<div class="form-actions" style="height: 200px">
				<!-- <span class="pull-left"><a href="#"
					class="flip-link btn btn-info" id="to-recover">忘记密码?</a></span>  -->
					<!-- <span class="pull-left">
				    	<select style="width: 150px;" id="dataCenter" name="dataCenter" onchange="setHidden()">
	         				<option value="0">请选择数据中心</option>
	                	</select>
					</span>
					<input type="hidden" id="dataCenterName" name="dataCenterName"> -->
					<span class="pull-right" style="height: 28px"><a type="submit"
					href="javascript:void(0)" class="btn btn-success"
					onclick="submit()" /> 登录</a></span>
			</div>
		</form>
		<form id="recoverform" action="#" class="form-vertical">
			<p class="normal_text">Enter your e-mail address below and we
				will send you instructions how to recover a password.</p>

			<div class="controls">
				<div class="main_input_box">
					<span class="add-on bg_lo"><i class="icon-envelope"></i></span><input
						type="text" placeholder="E-mail address" />
				</div>
			</div>

			<div class="form-actions">
				<span class="pull-left"><a href="#"
					class="flip-link btn btn-success" id="to-login">&laquo; Back to
						login</a></span> <span class="pull-right"><a class="btn btn-info" />Reecover</a></span>
			</div>
		</form>
	</div>

	<script src="${path}/js/jquery.min.js"></script>
	<script src="${path}/js/matrix.login.js"></script>
	
	<%-- <script src="${path}/js/jquery.uniform.js"></script> 
	<script src="${path}/js/select2.min.js"></script> 
	<script src="${path}/js/matrix.form_common.js"></script> --%>
	<script type="text/javascript">
		
		/* if (typeof (EventSource) !== "undefined") {
			var es = new EventSource("${path}/views/admin/test.do");
			es.onmessage = function(event) {
				document.getElementById("result").innerHTML = event.data
						+ "<br />";
			};
		} else {
			document.getElementById("result").innerHTML = "Sorry, your browser does not support server-sent events...";
		} */

		function submit() {
			$("#loginform").submit();
		}
		
		function enterSubmit() {
			if (event.keyCode == 13) {
				submit();
			}
		}
		
	</script>
</body>

</html>
