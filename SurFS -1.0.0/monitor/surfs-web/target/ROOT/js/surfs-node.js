function fillingData(serverHost, port, backupList) {
	$("#serverHost").val(serverHost);
	$("#port").val(port);
	$("#backupList").val(backupList);
	$("#status").val($("#"+serverHost).text());
}

function confirmDeleteNode(path) {
	var serverHostHidden = $("#serverHostHidden").val();
	$.ajax({
		type : "POST",
		url : path+"/storage/deleteNode.do",
		async : false,
		cache : false,
		contentType: "application/json; charset=utf-8",
        data:serverHostHidden,
        success : function(success) {
        	var delServerHost = $("#delHidden").val();
			$("#"+delServerHost).remove();
			showMsgOperate($("#node_success"), $("#node_error"), "删除服务节点成功");
		}
	});
}

function saveNodeProperties(path) {
	$.ajax({
		type : "POST",
		url : path+"/storage/saveNodeProperties.do",
		async : false,
		cache : false,
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
        	"serverHost": $("#serverHost").val(),
        	"port": $("#port").val(),
        	"backupList": $("#backupList").val()
        }),
        success : function(success) {
        	showMsgOperate($("#node_success"), $("#node_error"), "修改服务节点成功");
		},
		error: function(error) {
			showMsgOperate($($("#node_error"), "#node_success"), "修改服务节点失败");
		}
	});
}


function showButton(show, hide) {
	$("#"+hide).hide();
	$("#"+show).show();
}

function refreshVolume(serverHost, path) {
	showSubmit(path+"/storage/scanNodeVolume.do?serverHost="+serverHost);
}