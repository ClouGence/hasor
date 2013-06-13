/* -------------------------------------------------------------------- */
/* UIButton Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIButton", "UIInput", {
	/** 引发标签的click事件。 */
	click : function(paramData, ajaxAfter, ajaxError) {
		var onclick = $(this.getElement()).attr("_onclick");
		if (WebUI.isNaN(onclick) == false) {
			WebUI.runSrcipt(onclick, this, {});
		} else if (WebUI.isNaN(this.actionEL()) == false) {
			this.doEvent('OnAction', {
				/* 携带的参数 */
				'dataMap' : paramData,
				/* 正确的回调 */
				'ajaxAfter' : ajaxAfter,
				/* 错误的回调 */
				'ajaxError' : ajaxError
			});
		}
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
		/** ActionEL（R） */
		this.defineProperty("actionEL", "R");
		this.bindEvent("click", this.onclick);
	}
});