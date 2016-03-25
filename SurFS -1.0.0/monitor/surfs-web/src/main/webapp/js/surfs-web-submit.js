function showSubmit(url) {
	var board  = document.getElementById("sidebar");
	var form = document.createElement("form");
	var obj = board.appendChild(form);
	obj.checked = true;
	form.action = url;
	form.method = "post";
	form.submit();
}
