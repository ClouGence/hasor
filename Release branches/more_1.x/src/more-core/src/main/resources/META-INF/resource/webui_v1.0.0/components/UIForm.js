/* -------------------------------------------------------------------- */
/* UIForm Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIForm", "UIComponent", {
	/** 处理UIForm的OnSubmit动作 */
	onsubmit : function() {
		var onsubmit = $(this.getElement()).attr("_onsubmit");
		if (WebUI.isNaN(onsubmit) == false) {
			WebUI.runSrcipt(onsubmit, this, {
				value : this.getFormData()
			});
		} else {
			if (this.submitAction() != null) {
				$(this.getElement()).attr("action", this.submitAction());
				$(this.getElement()).attr("method", "post");
				this.getElement().submit();
				return;
			} else
				this.doSubmit({}, function(event) {
				/* TODO OnSubmit , OK CallBack. */
				}, function(event) {
				/* TODO OnSubmit , Error CallBack. */
				});
		}
		return false;
	},
	/** 处理UIForm的OnSubmit动作 */
	doSubmit : function(paramData, ajaxAfter, ajaxError) {
		// A.准备数据
		paramData = (WebUI.isObject(paramData) == false) ? {} : paramData;
		$("#" + this.clientID + ' [sMode]').each(function() {
			if (WebUI.isNaN(this.uiObject) == true)
				return;
			var uio = this.uiObject;
			if (uio.isForm() == false)
				return;
			paramData[uio.componentPath + ":value"] = uio.value();
		});
		// B.引发OnSubmit事件
		var $this = this;
		this.doEvent('OnSubmit', {
			/* 携带的参数 */
			'dataMap' : paramData,
			/* 正确的回调 */
			'ajaxAfter' : function(event) {
				if (WebUI.isFun(ajaxAfter) == true)
					ajaxAfter.call($this, event);
			},
			/* 错误的回调 */
			'ajaxError' : function(event) {
				if (WebUI.isFun(ajaxError) == true)
					ajaxError.call($this, event);
			}
		});
	},
	/** 获取到表单的值Map */
	getFormData : function() {
		// A.准备数据
		var paramData = {};
		$("#" + this.clientID + ' [sMode]').each(function() {
			if (WebUI.isNaN(this.uiObject) == true)
				return;
			var uio = this.uiObject;
			if (uio.isForm() == false)
				return;
			paramData[uio.getName()] = uio.value();
		});
		return paramData;
	},
	/** 构造方法 */
	"<init>" : function() {
		this.bindEvent("submit", this.onsubmit);
		/** 值字段（RW） */
		this.defineProperty("submitAction", "RW");
	}
});