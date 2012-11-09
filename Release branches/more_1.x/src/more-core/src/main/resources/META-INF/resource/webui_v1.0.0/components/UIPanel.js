/* -------------------------------------------------------------------- */
/* UIPanel Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIPanel", "UIComponent", {
	/** 载入一个指定的页面 */
	load : function(url, paramMap, callBackFun, errorFun) {
		var $this = this;
		this.doEventTo(url, "OnLoadPage", paramMap, function(res) {
			$($this.getElement()).html(res.result);
			if (WebUI.isFun(callBackFun) == true)
				callBackFun.call($this, res);
		}, function(res) {
			$($this.getElement()).html(res.message);
			if (WebUI.isFun(errorFun) == true)
				errorFun.call($this, res);
		});
	},
	/** 载入预先定义的页面 */
	loadPage : function(paramMap, callBackFun, errorFun) {
		if (WebUI.isNaN(paramMap) == true)
			paramMap = {};
		this.load(this.pageURL(), paramMap, callBackFun, errorFun);
	},
	/** 构造方法 */
	"<init>" : function() {
		/** 要载入的页面（RW） */
		this.defineProperty("pageURL", "RW");
	}
});