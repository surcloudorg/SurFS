function fillingData(mountId, createTime) {
	var path = $("#path_"+mountId).html();
	var quota = $("#quota_"+mountId).text();
	
	$("#save_mountId").val(mountId);
	$("#save_path").val(path);
	$("#save_quota").val(quota);
	$("#save_createTime").val(createTime);
}

function confirmDeleteMount(path) {
	var mountIDHidden = $("#mountIDHidden").val();
	var pathHidden = $("#pathHidden").val();
	$.ajax({
		type : "POST",
		url : path+"/storage/deleteMount.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"mountId": mountIDHidden,
        	"path": pathHidden
        }),
        success : function(success) {
        	var delMountID = $("#delHidden").val();
			$("#"+delMountID).remove();
			showMsgOperate($("#mount_success"), $("#mount_error"), "挂载路径删除成功");
		},
		error: function(msg) {
			showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径删除失败，只有挂载路径无其它关联时才能删除");
		}
	});
}

function saveMount(path) {
	
	var mountId = $("#save_mountId").val();
	var checkPath = $("#save_path").val();
	
	if (!checkMountPath(checkPath)) {
		showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径必须是一级目录");
		return;
	}
	$.ajax({
		type : "POST",
		url : path+"/storage/saveMount.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"mountId": mountId,
        	"path": checkPath,
        	"quota": $("#save_quota").val(),
        	"oldPath": $("#hidden_"+mountId).val()
        }),
		success : function(msg) {
			var quota = $("#save_quota").val();
			$("#path_"+mountId).html(checkPath);
			$("#quota_"+mountId).text(quota);
			showMsgOperate($("#mount_success"), $("#mount_error"), "挂载路径修改成功");
		},
		error: function(msg) {
			showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径修改失败");
		}
	});
}

function checkMountPath(checkPath) {
	var path = checkPath.replace(/\\/g , "/");
	var isCheck = false;
	if(path.indexOf("/") == 0) {
		if (path.lastIndexOf("/") == 0) {
			isCheck = true;
		} else {
			if (path.lastIndexOf("/") == path.length - 1) {
				isCheck = true;
			}
		}
	} else {
		if (path.lastIndexOf("/") == -1) {
			isCheck = true;
		} else {
			if (path.lastIndexOf("/") == path.length - 1) {
				isCheck = true;
			}
		}
	}
	return isCheck;
}

function addMount(path) {
	var checkPath = $("#add_path").val();
	if (!checkMountPath(checkPath)) {
		showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径必须是一级目录");
		return;
	}
	$.ajax({
		type : "POST",
		url : path+"/storage/addMount.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"path": checkPath,
        	"quota": $("#add_quota").val()
        }),
		success : function(msg) {
			showMsgOperate($("#mount_success"), $("#mount_error"), "挂载路径添加成功");
		},
		error: function(msg) {
			showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径添加失败");
		}
	});
}

function addUsersMount(mountId, path, url) {
	queryUsersMount(mountId, path, url+"/storage/queryListUsersNotMount.do");
	$("#cancelRelationUsersMount").hide();
	$("#relationUsersMount").show();
}

function editUsersMount(mountId, path, url) {
	queryUsersMount(mountId, path, url+"/storage/queryListUsersMount.do");
	$("#relationUsersMount").hide();
	$("#cancelRelationUsersMount").show();
}

function queryUsersMount(mountId, path, url) {
	$.ajax({
		type : "POST",
		url : url,
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: mountId,
		success : function(data) {
			$("#usersList").children().remove();
			$("#currentMountPath").text(path);
			$("#currentMountId").val(mountId);
			var usersCount = -1;
			$.each(data, function(index, obj) {
				var addTr="<tr class='gradeX'>";
				if (obj.permission != null)
					addTr+="<td style='text-align: center;width: 40px;'><input type='checkbox' checked='checked' id='check_"+obj.usersId+"'></td>";
				else 
					addTr+="<td style='text-align: center;width: 40px;'><input type='checkbox' id='check_"+obj.usersId+"'></td>";
				addTr+="<td style='text-align: center;'>"+obj.usersId+"</td>";
				addTr+="<td style='text-align: center;'>"+obj.usersName+"</td>";
				addTr+="<td style='text-align: center;'>"+obj.realName+"</td>";
				addTr+="<td class='center' style='text-align: center;'>";
				if (obj.permission == "r")
					addTr+="只读 <input type='radio' checked='checked' name='permission_"+obj.usersId+"' value='r' id='r_check_"+obj.usersId+"'>";
				else
					addTr+="只读 <input type='radio' name='permission_"+obj.usersId+"' value='r' id='r_check_"+obj.usersId+"'>";
				if (obj.permission == "rw")
					addTr+="&nbsp;读写 <input type='radio' checked='checked' value='rw' name='permission_"+obj.usersId+"' id='w_check_"+obj.usersId+"'></td></tr>";
				else
					addTr+="&nbsp;读写 <input type='radio' value='rw' name='permission_"+obj.usersId+"' id='w_check_"+obj.usersId+"'></td></tr>";	
		        $("#usersList").append(addTr);
		        usersCount = index;
			});
			$("#usersCount").text(usersCount+1);
			if (jQuery.isEmptyObject(data)) {
				var addTr="<tr class='gradeX'>";
				addTr+="<td colspan='5' style='text-align: center;' id='noResult'>没有查询到可用的数据</td></tr>";
				$("#usersList").append(addTr);
			}
		}
	});
}

function getCheckUsersMount() {
	var usersMountList = new Array();
	$("#usersList tr").each(function(trindex,tritem) {
		var tditem = $(tritem).find("td");
		var isChecked = $(tditem).eq(0).find("input").attr("checked");
		if (isChecked) {
			var usersMount = new Object();
			usersMount["usersId"] = $(tditem).eq(1).text();
			usersMount["mountId"] = $("#currentMountId").val();
			
			$(tditem).eq(4).find("input").each(function(index,input) {
				if ($(input).attr("checked")) {
					usersMount["permission"] = $(input).val();
				} else {
					usersMount["permission"] = "rw";
				}
			});
			usersMountList.push(usersMount);
		}
	});
	return usersMountList;
}

function relationUsersMount(path) {
	var usersMountList = getCheckUsersMount();
	if (usersMountList.length == 0) {
		showMsgOperate($("#mount_error"), $("#mount_success"), "没有进行关联所需的数据");
		return;
	}
	usersMountSubmit(usersMountList, path+"/storage/addUsersMount.do");
}

function cancelRelationUsersMount(path) {
	var usersMountList = getCheckUsersMount();
	if ($("#noResult").text() != "") {
		showMsgOperate($("#mount_error"), $("#mount_success"), "没有取消关联所需的数据");
		return;
	} 
	if (usersMountList.length == 0) {
		var usersMount = new Object();
		usersMount["mountId"] = $("#currentMountId").val();
		usersMountList.push(usersMount);
	}
	usersMountSubmit(usersMountList, path+"/storage/editUsersMount.do");
}

function usersMountSubmit(usersMountList, url) {
	$.ajax({
		type : "POST",
		url : url,
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify(usersMountList),
		success : function(msg) {
			showMsgOperate($("#mount_success"), $("#mount_error"), "挂载路径关联成功");
		},
		error: function(msg) {
			showMsgOperate($("#mount_error"), $("#mount_success"), "挂载路径关联失败");
		}
	});
}