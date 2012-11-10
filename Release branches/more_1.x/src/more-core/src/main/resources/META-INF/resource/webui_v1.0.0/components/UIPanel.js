/* -------------------------------------------------------------------- */
/* UIPanel Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIPanel", "UIComponent", {
	/** 载入一个指定的页面 */
	load : function(url, paramMap, ajaxAfter, ajaxError) {
		if (WebUI.isNaN(url) == true)
			throw 'url is null.'
		var $this = this;
		var dialogPanel = $.dialog.list[$this.clientID];
		if (WebUI.isNaN(dialogPanel) == false) {
			/** 面板作为对话框 */
			var iframe = dialogPanel.iframe;
			if (WebUI.isNaN(iframe) == false) {
				// 对话框工作在iframe模式
				if (url.indexOf("?") > 0)
					iframe.src = url + "&" + WebUI.mapToURI(paramMap);
				else
					iframe.src = url + "?" + WebUI.mapToURI(paramMap);
				return;
			} else {
				// 对话框工作在div模式
				WebUI.call(null, {
					'url' : url,
					/* 事件 */
					'event' : 'OnLoadPage',
					/* 携带的参数 */
					'dataMap' : paramMap,
					/* 渲染类型 */
					'renderType' : 'ALL',
					/* 正确的回调 */
					'ajaxAfter' : function(res) {
						dialogPanel.content(res.result, false, false)
						if (WebUI.isFun(ajaxAfter) == true)
							ajaxAfter.call($this, res);
					},
					/* 错误的回调 */
					'ajaxError' : function(res) {
						dialogPanel.content(res.message, false, false)
						if (WebUI.isFun(ajaxError) == true)
							ajaxError.call($this, res);
					}
				});
			}
		} else {
			/** 面板作为div */
			WebUI.call(null, {
				'url' : url,
				/* 事件 */
				'event' : 'OnLoadPage',
				/* 携带的参数 */
				'dataMap' : paramMap,
				/* 渲染类型 */
				'renderType' : 'ALL',
				/* 正确的回调 */
				'ajaxAfter' : function(res) {
					$($this.getElement()).html(res.result);
					if (WebUI.isFun(ajaxAfter) == true)
						ajaxAfter.call($this, res);
				},
				/* 错误的回调 */
				'ajaxError' : function(res) {
					$($this.getElement()).html(res.message);
					if (WebUI.isFun(ajaxError) == true)
						ajaxError.call($this, res);
				}
			});
		}
	},
	/** 第一个参数是固定值，第二个参数是默认值，第三个是用户的配置。 */
	showDialog : function(fixedParamMap, defaultParamMap, dialogParamMap) {
		/* url, paramMap, callBackFun, errorFun */
		if (WebUI.isNaN(dialogParamMap) == true)
			dialogParamMap = {};
		// 固定参数
		for ( var v in fixedParamMap)
			dialogParamMap[v] = fixedParamMap[v];
		dialogParamMap["id"] = this.clientID;
		var varContent = dialogParamMap['content'];
		if (WebUI.isNaN(varContent) == true)
			dialogParamMap["content"] = $(this.getElement()).html();
		// 默认值
		for ( var v in defaultParamMap)
			if (WebUI.isNaN(dialogParamMap[v]) == true)
				dialogParamMap[v] = defaultParamMap[v];
		// dialog
		$.dialog(dialogParamMap);
	},
	/** 将面板的内容作为对话框弹出（公告版）。 如下几个属性被锁定不可设置：id、content、fixed、drag、resize、max、min */
	dialogNotice : function(dialogParamMap) {
		// 固定参数
		var fixedParamMap = {};
		fixedParamMap["fixed"] = true;
		fixedParamMap["drag"] = false;
		fixedParamMap["resize"] = false;
		fixedParamMap["max"] = false;
		fixedParamMap["min"] = false;
		fixedParamMap["lock"] = false;
		// 默认配置
		var defaultParamMap = {};
		defaultParamMap["left"] = "100%";
		defaultParamMap["top"] = "100%";
		defaultParamMap["width"] = 200;
		defaultParamMap["height"] = 100;
		defaultParamMap["title"] = "公告";
		// dialog
		this.showDialog(fixedParamMap, defaultParamMap, dialogParamMap);
	},
	/** 将面板的内容作为对话框弹出（面板）。 如下几个属性被锁定不可设置：id、content、fixed、drag、resize、max、min */
	dialogPanel : function(dialogParamMap) {
		// 固定参数
		var fixedParamMap = {};
		// 默认配置
		var defaultParamMap = {};
		defaultParamMap["max"] = true;
		defaultParamMap["min"] = true;
		defaultParamMap["fixed"] = true;
		defaultParamMap["drag"] = true;
		defaultParamMap["resize"] = true;
		defaultParamMap["lock"] = false;
		defaultParamMap["left"] = "60%";
		defaultParamMap["top"] = "60%";
		defaultParamMap["width"] = "60%";
		defaultParamMap["height"] = "60%";
		defaultParamMap["title"] = "Panel";
		// dialog
		this.showDialog(fixedParamMap, defaultParamMap, dialogParamMap);
	},
	/** 构造方法 */
	"<init>" : function() {
		/** 要载入的页面（RW） */
		this.defineProperty("pageURL", "RW");
		var pageURL = this.pageURL();
		if (WebUI.isNaN(pageURL) == false)
			this.load(pageURL);
	}
});