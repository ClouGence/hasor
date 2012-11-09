/* -------------------------------------------------------------------- */
/* UIButton Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIButton", "UIInput", {
	/** 引发标签的click事件。 */
	click : function(params, callBackFun, errorFun) {
		this.doEvent("OnAction", params, callBackFun, errorFun);
	},
	/** 标签元素的click事件处理程序。 */
	onclick : function() {
		this.click({}, function(event) {
		/* TODO OnChange , OK CallBack. */
		}, function(event) {
		/* TODO OnChange , Error CallBack. */
		});
	},
	/** 构造方法 */
	"<init>" : function() {
		if (WebUI.isNaN($(this.getElement()).attr('onclick')) == true)
			this.bindEvent("click", this.onclick);
	}
});