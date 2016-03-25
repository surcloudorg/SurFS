function showMsgOperate(showEl, hideEl, msg) {
	showEl.children().last().text(msg);
	hideEl.hide();
	showEl.show();
}