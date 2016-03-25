function fillingData(usersId,createTime,usersName,realName,comment,pwd) {
	$("#save_usersId").val(usersId);
	$("#save_pwd").val(pwd);
	$("#save_usersName").val(usersName);
	$("#save_realName").val(realName);
	$("#save_comment").val(comment);
	$("#save_createTime").val(createTime);
}

function confirmDeleteUsers(path) {
	var usersIDHidden = $("#usersIDHidden").val();
	$.ajax({
		type : "POST",
		url : path+"/storage/deleteUsers.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data: usersIDHidden,
        success : function(success) {
        	var delMountID = $("#delHidden").val();
			$("#"+delMountID).remove();
			showMsgOperate($("#users_success"), $("#users_error"), "删除用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#users_error"), $("#users_success"), "删除用户失败");
		}
	});
}

function saveUsers(path) {
	var usersId = $("#save_usersId").val();
	$.ajax({
		type : "POST",
		url : path+"/storage/saveUsers.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"usersId": usersId,
        	"usersName": $("#save_usersName").val(),
        	"realName": $("#save_realName").val(),
        	"pwd": $("#save_pwd").val(),
        	"comment": $("#save_comment").val()
        }),
		success : function(msg) {
			var usersName = $("#save_usersName").val();
			var realName = $("#save_realName").val();
			var pwd = $("#save_pwd").val();
			var comment = $("#save_comment").val();
			$("#usersName_"+usersId).text(usersName);
			$("#realName_"+usersId).text(realName);
			$("#pwd_"+usersId).text(pwd);
			$("#comment_"+usersId).text(comment);
			
			showMsgOperate($("#users_success"), $("#users_error"), "修改用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#users_error"), $("#users_success"), "修改用户失败");
		}
	});
}

function addUsers(path) {
	
	$.ajax({
		type : "POST",
		url : path+"/storage/addUsers.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"usersName": $("#add_usersName").val(),
        	"realName": $("#add_realName").val(),
        	"pwd": $("#add_pwd").val(),
        	"comment": $("#add_comment").val()
        }),
		success : function(msg) {
			showMsgOperate($("#users_success"), $("#users_error"), "添加用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#users_error"), $("#users_success"), "添加用户失败");
		}
	});
}