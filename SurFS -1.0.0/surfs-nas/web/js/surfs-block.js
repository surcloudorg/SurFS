function confirmDeleteVol(path) {
	var zpoolvolHidden = $("#zpoolvolHidden").val();
	var ipHidden = $("#ipHidden").val();
	var trHidden = $("#trHidden").val();
	
	$.ajax({
		type : "POST",
		url : path+"/block/deleteRemoteVol.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"zpoolvol": zpoolvolHidden,
        	"ip": ipHidden
        }),
        success : function(success) {
        	var zpoolvol = zpoolvolHidden.split("/");
        	
        	var free_quota_unit = $("#free_"+zpoolvol[0]).text();
        	var free_unit = free_quota_unit.substr(free_quota_unit.length - 1);
        	var free_quota = free_quota_unit.substr(0, free_quota_unit.length - 1);
        	var free_quota_gb = 0.0;
        	if (free_unit == "T")
        		free_quota_gb = free_quota * 1024;
        	else
        		free_quota_gb = free_quota;
        	
        	var del_quota_unit = $("#" + zpoolvol[0] + "_" + zpoolvol[1] + "_cap").text();
        	var del_unit = del_quota_unit.substr(del_quota_unit.length - 1);
        	var del_quota = del_quota_unit.substr(0, del_quota_unit.length - 1);
        	var del_quota_gb = 0.0;
        	if (del_unit == "T")
        		del_quota_gb = del_quota * 1024;
    		else
    			del_quota_gb = del_quota;
        	
        	var quota = 0.0;
        	
        	if (free_unit == "T")
        		quota = Number((free_quota_gb + parseFloat(del_quota_gb)) / 1024).toFixed(2);
        	else
        		quota = Number(free_quota_gb + parseFloat(del_quota_gb)).toFixed(2);
        		
        	
        	$("#free_"+zpoolvol[0]).text(quota + free_unit);
        	
			$("#"+trHidden).remove();
			showMsgOperate($("#pool_success"), $("#pool_error"), "删除卷成功");
		},
		error: function(msg) {
			showMsgOperate($("#pool_error"), $("#pool_success"), "删除卷失败");
		}
	});
}

function fillAddVol(add_zpool, add_ip, addVolHidden) {
	$("#add_zpool").val(add_zpool);
	$("#add_ip").val(add_ip);
	
	$("#addVolHidden").val(addVolHidden);
	
	var vol_error = $("#add_vol_error");
	vol_error.text("");
	var quota_error = $("#add_quota_error");
	quota_error.text("");
	$("#add_vol").val("");
	$("#add_quota").val("");
}

function addVol(path) {
	var add_zpool = $("#add_zpool").val();
	var add_vol = $("#add_vol").val();
	var add_quota = $("#add_quota").val();
	var add_ip = $("#add_ip").val();
	var free = $("#free_"+add_zpool).text();
	var vol_error = $("#add_vol_error");
	var quota_error = $("#add_quota_error");
	
	var addVolHidden = $("#addVolHidden").val();
	
	if (add_vol == "") {
		vol_error.text("卷名称不能为空");
		return;
	} else {
		if (add_vol.indexOf("/") >= 0) {
			vol_error.text("卷名称不能包含/");
			return;
		}
	}
	
	if (add_quota == "") {
		quota_error.text("卷空间不能为空");
		return;
	} else {
		if (isNaN(add_quota) || add_quota.indexOf(".") >= 0) {
			quota_error.text("卷空间不是整数");
			return;
		}
		
		var quotaUnit = free.substr(free.length - 1);
		if (quotaUnit == "T") {
			var free_quota = free.substr(0, free.length - 1) * 1024;
			if (free_quota < add_quota) {
				quota_error.text("卷空间不能大于可用容量");
				return;
			}
		}
	}
	
	var existVol = $("#"+add_zpool+"_vol_"+add_vol).text();
	if (existVol !== "") {
		vol_error.text("卷名称已经存在");
		return;
	}
	
	$("#addAlert").modal("hide");
	
	$.ajax({
		type : "POST",
		url : path+"/block/addRemoteVol.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"zpool": add_zpool,
        	"vol": add_vol,
        	"ip": add_ip,
        	"size": add_quota+"G"
        }),
        success : function(success) {
        	var tbody = $("#"+addVolHidden);
        	var trLength = tbody.children().length;
        	if (trLength != 0)
        		trLength = trLength + 100;
        	
        	// 添加vol
        	var addTr = "<tr id='"+add_zpool+"_"+trLength+"'>";
        	addTr += "<td></td>";
        	addTr += "<td id='"+add_zpool+"_vol_"+add_vol+"'>卷名称："+add_zpool+"/"+add_vol;
        	addTr += " ( 容量：<span id='"+add_zpool+"_"+add_vol+"_cap'>"+add_quota+"G</span> )</td>";
        	addTr += "<td style='width: 250px;'>状态：<span class='date badge badge-important' id=''>未导出</span>";
        	addTr += "<td style='vertical-align: middle; text-align: center;'>";
        	addTr += "<span id='delete_"+add_zpool+"'> <a href='#deleteAlert' data-toggle='modal' data-original-title='删除卷' class='tip' onclick=''>";
        	addTr += "<i class='icon icon-remove' style='font-size: large; color: red;'></i></a></span></td></tr>";
        	
        	$("#ipHidden").val(add_ip);
        	$("#zpoolvolHidden").val(add_zpool+"/"+add_vol);
        	$("#trHidden").val(add_zpool+"_"+trLength);
        	
        	// change quota
        	var free_quota_unit = $("#free_"+add_zpool).text();
        	var free_unit = free_quota_unit.substr(free_quota_unit.length - 1);
        	var free_quota = free_quota_unit.substr(0, free_quota_unit.length - 1);
        	var free_quota_gb = 0.0;
        	if (free_unit == "T")
        		free_quota_gb = free_quota * 1024;
        	else
        		free_quota_gb = free_quota;
        	var quota = 0.0;
        	
        	if (free_unit == "T")
        		quota = Number((free_quota_gb - parseFloat(add_quota)) / 1024).toFixed(2);
        	else
        		quota = Number(free_quota_gb - parseFloat(add_quota)).toFixed(2);
        		
        	
        	$("#free_"+add_zpool).text(quota + free_unit);
        	
        	// append tr
        	tbody.append(addTr);
			showMsgOperate($("#pool_success"), $("#pool_error"), "添加卷成功");
		},
		error: function(msg) {
			showMsgOperate($("#pool_error"), $("#pool_success"), "添加卷失败");
		}
	});
}

function fillAddTarget(add_target_ip) {
	$("#add_target_ip").val(add_target_ip);
	
	var target_error = $("#add_target_error");
	target_error.text("");
	$("#add_target").val("");
}

function addTarget(path) {
	var add_target_ip = $("#add_target_ip").val();
	var add_target = $("#add_target").val();
	var target_error = $("#add_target_error");
	
	if (add_target == "") {
		target_error.text("target名称不能为空");
		return;
	}
	
	var existTarget = $("#target_"+add_target_ip+"_"+add_target).text();
	if (existTarget !== "") {
		target_error.text("target名称已经存在");
		return;
	}
	
	$("#addTargetAlert").modal("hide");
	
	$.ajax({
		type : "POST",
		url : path+"/block/addRemoteTarget.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"target": add_target,
        	"ip": add_target_ip
        }),
        success : function(success) {
			showMsgOperate($("#export_success"), $("#pool_error"), "添加target成功");
		},
		error: function(msg) {
			showMsgOperate($("#export_error"), $("#pool_success"), "添加target失败");
		}
	});
}

function confirmDeleteTarget(path) {
	var targetHidden = $("#targetHidden").val();
	var ipHidden = $("#ipHidden").val();
	/*var trHidden = $("#trHidden").val();*/
	
	$("#deleteTargetAlert").modal("hide");
	
	$.ajax({
		type : "POST",
		url : path+"/block/delRemoteTarget.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"target": targetHidden,
        	"ip": ipHidden
        }),
        success : function(success) {
        	showMsgOperate($("#export_success"), $("#pool_error"), "删除target成功");
        }, error: function(msg) {
        	showMsgOperate($("#export_error"), $("#pool_success"), "删除target失败");
        }
	});
}

function fillAddDevice(path, add_device_target, add_device_ip, hostIndex, infoIndex) {
	$("#addDeviceError").hide();
	$("#add_device_target").text(add_device_target);
	$("#add_device_ip").val(add_device_ip);
	$("#index").val(hostIndex+"_"+infoIndex);
	
	$.ajax({
		type : "POST",
		url : path+"/block/getVolDevs.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
		data: add_device_ip,
		success : function(data) {
			$("#volDevList").children().remove();
			$.each(data, function(index, obj) {
				var addTr="<tr class='gradeX'>";
				addTr+="<td style='text-align: center;width: 40px;'><input type='checkbox' id='check_'></td>";
				addTr+="<td style='text-align: left;'>"+obj.name+"</td>";
				addTr+="<td style='text-align: center;width: 60px;'>"+obj.cap+"</td>";
		        $("#volDevList").append(addTr);
			});
			
			if (jQuery.isEmptyObject(data)) {
				var addTr="<tr class='gradeX'>";
				addTr+="<td colspan='3' style='text-align: center;' id='noResult'>没有查询到可用的数据</td></tr>";
				$("#volDevList").append(addTr);
			}
		}
	});
}

function addDevice(path) {
	var volDevList = new Array();
	var capList = new Array();
	$("#volDevList tr").each(function(trindex,tritem) {
		var tditem = $(tritem).find("td");
		var isChecked = $(tditem).eq(0).find("input").attr("checked");
		if (isChecked) {
			/*var volDev = new Object();
			volDev["name"] = $(tditem).eq(1).text();
			volDevList.push(volDev);*/
			volDevList.push($(tditem).eq(1).text());
			capList.push($(tditem).eq(2).text());
		}
	});
	
	if (volDevList.length <= 0) {
		$("#addDeviceError").show();
		return;
	}
	
	var add_device_ip = $("#add_device_ip").val();
	var add_device_target = $("#add_device_target").text();
	
	var maps = new Object();
	maps["volDevList"] = volDevList;
	maps["ip"] = add_device_ip;
	maps["target"] = add_device_target;
	
	$("#addDeviceAlert").modal("hide");
	
	$.ajax({
		type : "POST",
		url : path+"/block/addRemoteDevice.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
		data: JSON.stringify(maps),
        success : function(success) {
        	
        	var index = $("#index").val();
        	var deviceList = $("#deviceList_"+index);
        	var trLength = deviceList.children().length;
        	if (trLength != 0)
        		trLength = trLength + 100;
        	/*var infoIndex = $("#infoIndex").val();*/
        	
        	
        	for (var i = 0; i < volDevList.length; i++) {
        	
	        	var addTr = "<tr id='"+index+"_"+trLength+"'>";
	        	addTr += "<td style='width: 250px;'>device名称："+volDevList[i]+"</td><td>容量："+capList[i]+"</td>";
	        	addTr += "<td style='width: 150px; vertical-align: middle; text-align: center;'>";
	        	addTr += "<a href='#deleteDeviceAlert' data-toggle='modal' data-original-title='删除设备' class='tip' onclick=javascript:$('#removeDeviceHidden').val('"+index+"_"+trLength+"');$('#deleteDeviceError').hide()>";
	        	addTr += "<i class='icon icon-remove' style='font-size: large; color: red;'></i></a></td>";
	        	addTr += "<input type='hidden' id='device_"+index+"_"+trLength+"' value='"+volDevList[i]+"'>";
	        	addTr += "<input type='hidden' id='target_"+index+"_"+trLength+"' value='"+add_device_target+"'>";
	        	addTr += "<input type='hidden' id='ip_"+index+"_"+trLength+"' value='"+add_device_ip+"'>";
	        	addTr += "<input type='hidden' id='remove_"+index+"_"+trLength+"' value='"+index+"_"+trLength+"'></tr>";
	        	trLength++;
	        	deviceList.append(addTr);
        	}
        	
			showMsgOperate($("#export_success"), $("#pool_error"), "添加设备成功");
		},
		error: function(msg) {
			showMsgOperate($("#export_error"), $("#pool_success"), "添加设备失败");
		}
	});
}

function confirmDeleteDevice(path) {
	var removeDeviceHidden = $("#removeDeviceHidden").val();
	var indexs = removeDeviceHidden.split("_");
	var loginList = $("#loginList_"+indexs[0]+"_"+indexs[1]);
	var trLength = loginList.children().length;
	
	if (trLength > 0) {
		$("#deleteDeviceError").show();
		return;
	}
	$("#deleteDeviceAlert").modal("hide");
	var deviceTargetHidden = $("#target_"+removeDeviceHidden).val();
	var deviceHidden = $("#device_"+removeDeviceHidden).val();
	var ipHidden = $("#ip_"+removeDeviceHidden).val();
	$.ajax({
		type : "POST",
		url : path+"/block/delRemoteDevice.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"device": deviceHidden,
        	"target": deviceTargetHidden,
        	"ip": ipHidden
        }),
        success : function(success) {
        	$("#"+removeDeviceHidden).remove();
			showMsgOperate($("#export_success"), $("#pool_error"), "删除卷成功");
		},
		error: function(msg) {
			showMsgOperate($("#export_error"), $("#pool_success"), "删除卷失败");
		}
	});
}

//账户管理
function confirmDeleteBlockUser(path){
	var userId = $("#userIDHidden").val();
	$.ajax({
		type : "POST",
		url : path+"/block/deleteBlockUser.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
		data: userId,
        success : function(success) {
			if(success == false){
				showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "此用户已被授权，不能被删除");
			}else{
				var delUserID = $("#delHidden").val();
				$("#"+delUserID).remove();
				showMsgOperate($("#blockUser_success"), $("#blockUser_error"), "删除用户成功");
			}
		}
	});
}

function fillingData(userId,createTime,userName,realName,comment,passWord){
	$("#save_userId").val(userId);
	$("#save_userName").val(userName);
	$("#save_passWord").val(passWord);
	$("#save_realName").val(realName);
	$("#save_comment").val(comment);
	$("#save_createTime").val(createTime);
}
function saveBlockUser(path){
	var save_userName = $("#save_userName").val();
	if (save_userName.replace(/\s+/,'') == "" || save_userName.replace(/\s+/,'').length ==0) {
		showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "用户名不能为空");
		return;
	}
	var save_passWord = $("#save_passWord").val();
	if(save_passWord.replace(/\s+/,'').length < 12 || save_passWord.replace(/\s+/,'').length > 16){
		showMsgOperate($("#blockUser_error"),$("#blockUser_success"),"密码长度不能少于12个字符或大于16个");
		return;
	}
	var save_realName = $("#save_realName").val();
	if(save_realName.replace(/\s+/,'') == "" || save_realName.replace(/\s+/,'').length == 0){
		showMsgOperate($("#blockUser_error"),$("#blockUser_success"),"用户姓名不能为空");
		return;
	}
	var userId = $("#save_userId").val();
	$.ajax({
		type : "POST",
		url : path+"/block/saveBlockUser.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"userId": userId,
        	"userName": $("#save_userName").val(),
        	"realName": $("#save_realName").val(),
        	"passWord": $("#save_passWord").val(),
        	"comment": $("#save_comment").val()
        }),
		success : function(success) {
			var userName = $("#save_userName").val();
			var realName = $("#save_realName").val();
			var passWord = $("#save_passWord").val();
			var comment = $("#save_comment").val();
			$("#userName_"+userId).text(userName);
			$("#realName_"+userId).text(realName);
			$("#passWord"+userId).text(passWord);
			$("#comment_"+userId).text(comment);
			showMsgOperate($("#blockUser_success"), $("#blockUser_error"), "修改用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "该用户已经被占用");
		}
	});
}

function saveUserName(){
	var userName = $("#save_userName").val();
	var userName_error = document.getElementById("save_userName_error");
	if(userName.replace(/\s+/,'') == "" || userName.replace(/\s+/,'').length == 0){
		userName_error.innerHTML = "* 用户名不能为空";
		userName_error.style.color = "red";
		return false;
	}else{
		userName_error.innerHTML = "* 用户名输入正确";
		userName_error.style.color = "green";
		return true;
	}
}
function savePsw(){
	var passWord = $("#save_passWord").val();
	var passWord_error = document.getElementById("save_passWord_error");
	if(passWord.replace(/\s+/,'').length < 12 || passWord.replace(/\s+/,'').length > 16){
		passWord_error.innerHTML = "* 密码长度在12-16字符";
		passWord_error.style.color = "red";
		return false;
	}else{
		passWord_error.innerHTML = "* 密码长度正确";
		passWord_error.style.color = "green";
		return true;
	}
}
function saveRealName(){
	var realName = $("#save_realName").val();
	var realName_error = document.getElementById("save_realName_error");
	if(realName.replace(/\s+/,'') == "" || realName.replace(/\s+/,'').length == 0){
		realName_error.innerHTML = "* 用户姓名不能为空";
		realName_error.style.color = "red";
		return false;
	}else{
		realName_error.innerHTML = "* 用户姓名输入正确";
		realName_error.style.color = "green";
		return true;
	}
}

function addBlockUser(path) {
	//document.getElementById("add_userName").focus();
	var add_userName = $("#add_userName").val();
	var add_passWord = $("#add_passWord").val();
	var add_realName = $("#add_realName").val();
	if (add_userName.replace(/\s+/,'') == "" || add_userName.replace(/\s+/,'').length == 0) {
		showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "用户名不能为空");
		return ;
	}
	if(add_passWord.replace(/\s+/,'').length<12 || add_passWord.replace(/\s+/,'').length>16){
		showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "密码长度不能少于12个字符或大于16个");
		return;
	}
	if(add_realName.replace(/\s+/,'') == "" || add_realName.replace(/\s+/,'').length == 0){
		showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "用户姓名不能为空");
		return ;
	}
	$.ajax({
		type : "POST",
		url : path+"/block/addBlockUser.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"userName": $("#add_userName").val(),
        	"realName": $("#add_realName").val(),
        	"passWord": $("#add_passWord").val(),
        	"comment": $("#add_comment").val()
        }),
		success : function(msg) {
			showMsgOperate($("#blockUser_success"), $("#blockUser_error"), "添加用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#blockUser_error"), $("#blockUser_success"), "该用户已经被占用");
		}
	});
}
 
function chkUserName(){
	var userName = $("#add_userName").val();
	var add_userName_error = document.getElementById("add_userName_error");
	if(userName.replace(/\s+/,'') != "" || userName.replace(/\s+/,'').length != 0){
		add_userName_error.innerHTML = "*用户名输入正确";
		add_userName_error.style.color="green";
		return true;
	}else{
		add_userName_error.innerHTML = "*用户名不能为空";
		add_userName_error.style.color="red";
		return false;
	}
}

//添加用户时，判断密码长度
function chkPsw(){
	//var passWord = document.forms[0].elements['passWord'].value;
	var passWord = $("#add_passWord").val();
	var add_passWord_error = document.getElementById("add_passWord_error");
	if(passWord.length<12 || passWord.length>16){
		add_passWord_error.innerHTML = "* 密码长度在12-16字符";
		add_passWord_error.style.color = "red";
		return false;
	}else{
		add_passWord_error.innerHTML = "* 密码长度正确";
		add_passWord_error.style.color = "green";
		return true;
	}
}
//添加用户时，判断真实姓名是否为空
function chkRealName(){
	//var realName = document.forms[0].elements['realName'].value;
	var realName = $("#add_realName").val();
	var add_realName_error = document.getElementById("add_realName_error");
	if(realName == "" || realName.length == 0){
		add_realName_error.innerHTML = "*用户姓名不能为空";
		add_realName_error.style.color="red";
		return false;
	}
	if(realName.replace(/\s+/,'') != "" || realName.replace(/\s+/,'').length != 0){
		add_realName_error.innerHTML = "*用户姓名格式正确";
		add_realName_error.style.color="green";
		return true;
	}
}
 

//账户权限
function addBlockUserTarget(target, url, ip){
	queryBlockUserTarget(target, url+"/block/queryListBlockUserNotTarget.do", ip);
	$("#relationBlockUserTarget").show();
}

function getCheckBlockUserTarget() {
	var blockUserTargetList = new Array();
	$("#blockUserList tr").each(function(trindex,tritem) {
		var tditem = $(tritem).find("td");
		var isChecked = $(tditem).eq(0).find("input").attr("checked");
		if (isChecked) { 
			var blockUserTarget = new Object();
			blockUserTarget["userId"] = $(tditem).eq(1).text();
			blockUserTarget["userName"] = $(tditem).eq(2).text();
			blockUserTarget["passWord"] = $(tditem).eq(3).text();
			blockUserTarget["target"] = $("#currentTarget").val();
			blockUserTargetList.push(blockUserTarget);
		}
	});
	return blockUserTargetList;
}
function relationBlockUserTarget(path) {
	var blockUserTargetList = getCheckBlockUserTarget();
	if (blockUserTargetList.length == 0) {
		showMsgOperate($("#target_error"), $("#target_success"), "没有进行关联所需的数据");
		return;
	}
	var currentIp = $("#currentIp").val();
	var map = new Object();
	map["currentIp"] = currentIp;
	map["blockUserTargetList"] = blockUserTargetList;
	blockUserTargetSubmit(map, path+"/block/addBlockUserTarget.do");
}
/*function relationBlockUserTarget(path){
	var blockUserTargetList = getCheckBlockUserTarget();
	if(blockUserTargetList.length == 0){
		showMsgOperate($("#export_error"),$("#export_success"),"没有进行关联所需的数据");
		return;
	}
	blockUserTargetSubmit(blockUserTargetList,path+"/block/addBlockUserTarget.do");
}
*/

function confirmDeleteBlockUserTarget(path) {
	var userIDHidden = $("#userIDHidden").val();
	var targetHidden = $("#userTargetHidden").val();
	$.ajax({
		type : "POST",
		url : path+"/block/deleteBlockUserTarget.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:JSON.stringify({
        	"target": targetHidden,
        	"userId": userIDHidden
        }),
        success : function(success) {
        	var delUserID = $("#delHidden").val();
			$("#"+delUserID).remove();
			showMsgOperate($("#export_success"), $("#export_error"), "删除用户成功");
		},
		error: function(msg) {
			showMsgOperate($("#export_error"), $("#export_success"), "删除用户失败");
		}
	});
}

function queryBlockUserTarget(target, url, ip) {
	$.ajax({
		type : "POST",
		url : url,
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: target,
		success : function(data) {
			$("#blockUserList").children().remove();
			$("#currentTarget").val(target);
			$("#currentIp").val(ip);
			var blockUserCount = -1;
			$.each(data, function(index, obj) {
				var addTr="<tr class='gradeX'>";
				if (obj.permission != null)
					addTr+="<td style='text-align: center;width: 40px;'><input type='checkbox' checked='checked' id='check_"+obj.userId+"'></td>";
				else 
					addTr+="<td style='text-align: center;width: 50px;'><input type='checkbox' id='check_"+obj.userId+"'></td>";
			/*	addTr+="<td style='text-align: center;'>"+obj.userId+"</td>";*/
				addTr+="<td style='text-align: center;'>"+obj.userName+"</td>";
				addTr+="<td style='text-align: center;'>"+obj.passWord+"</td>";
				addTr+="<td style='text-align: center;'>"+obj.realName+"</td>";
		        $("#blockUserList").append(addTr);
		        blockUserCount = index;
			});
			$("#blockUserCount").text(blockUserCount+1);
			if (jQuery.isEmptyObject(data)) {
				var addTr="<tr class='gradeX'>";
				addTr+="<td colspan='5' style='text-align: center;' id='noResult'>没有查询到可用的数据</td></tr>";
				$("#blockUserList").append(addTr);
			}
		}
	});
}

function blockUserTargetSubmit(blockUserTargetList, url) {
	$.ajax({
		type : "POST",
		url : url,
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify(blockUserTargetList),
		success : function(msg) {
			showMsgOperate($("#export_success"), $("#export_error"), "添加账户授权成功");
		},
		error: function(msg) {
			showMsgOperate($("#export_error"), $("#export_success"), "添加账户授权失败");
		}
	});
}
