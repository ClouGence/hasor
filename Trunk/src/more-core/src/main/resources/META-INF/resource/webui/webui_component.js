var WebUI = {};

/* 组建核心方法 */
WebUI.Core = {
	Base64 : {
		Base64Chars : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*-",
		encode64 : function(s) {
			if (!s || s.length == 0)
				return s;
			var d = "";
			var b = WebUI.Core.Base64.ucs2_utf8(s);
			var b0, b1, b2, b3;
			var len = b.length;
			var i = 0;
			while (i < len) {
				var tmp = b[i++];
				b0 = (tmp & 0xfc) >> 2;
				b1 = (tmp & 0x03) << 4;
				if (i < len) {
					tmp = b[i++];
					b1 |= (tmp & 0xf0) >> 4;
					b2 = (tmp & 0x0f) << 2;
					if (i < len) {
						tmp = b[i++];
						b2 |= (tmp & 0xc0) >> 6;
						b3 = tmp & 0x3f;
					} else
						b3 = 64; // 1 byte "-" is supplement
				} else
					b2 = b3 = 64; // 2 bytes "-" are supplement
				d += WebUI.Core.Base64.Base64Chars.charAt(b0);
				d += WebUI.Core.Base64.Base64Chars.charAt(b1);
				d += WebUI.Core.Base64.Base64Chars.charAt(b2);
				d += WebUI.Core.Base64.Base64Chars.charAt(b3);
			}
			return d;
		},
		uncoded64 : function(s) {
			if (!s)
				return null;
			var len = s.length;
			if (len % 4 != 0)
				throw s + " is not a valid Base64 string.";
			var b = new Array();
			var i = 0, j = 0, e = 0, c, tmp;
			while (i < len) {
				c = WebUI.Core.Base64.Base64Chars.indexOf(s.charAt(i++));
				tmp = c << 18;
				c = WebUI.Core.Base64.Base64Chars.indexOf(s.charAt(i++));
				tmp |= c << 12;
				c = WebUI.Core.Base64.Base64Chars.indexOf(s.charAt(i++));
				if (c < 64) {
					tmp |= c << 6;
					c = WebUI.Core.Base64.Base64Chars.indexOf(s.charAt(i++));
					if (c < 64)
						tmp |= c;
					else
						e = 1;
				} else {
					e = 2;
					i++;
				}
				b[j + 2] = tmp & 0xff;
				tmp >>= 8;
				b[j + 1] = tmp & 0xff;
				tmp >>= 8;
				b[j + 0] = tmp & 0xff;
				j += 3;
			}
			b.splice(b.length - e, e);
			return WebUI.Core.Base64.utf8_ucs2(b);
		},
		ucs2_utf8 : function(s) {
			if (!s)
				return null;
			var d = new Array();
			if (s == "")
				return d;
			var c = 0, i = 0, j = 0;
			var len = s.length;
			while (i < len) {
				c = s.charCodeAt(i++);
				if (c <= 0x7f)
					// 1 byte
					d[j++] = c;
				else if ((c >= 0x80) && (c <= 0x7ff)) {
					// 2 bytes
					d[j++] = ((c >> 6) & 0x1f) | 0xc0;
					d[j++] = (c & 0x3f) | 0x80;
				} else {
					// 3 bytes
					d[j++] = (c >> 12) | 0xe0;
					d[j++] = ((c >> 6) & 0x3f) | 0x80;
					d[j++] = (c & 0x3f) | 0x80;
				}
			}// end whil
			return d;
		},
		utf8_ucs2 : function(s) {
			if (!s)
				return null;
			var len = s.length;
			if (len == 0)
				return "";
			var d = "";
			var c = 0, i = 0, tmp = 0;
			while (i < len) {
				c = s[i++];
				if ((c & 0xe0) == 0xe0) {
					// 3 bytes
					tmp = (c & 0x0f) << 12;
					c = s[i++];
					tmp |= ((c & 0x3f) << 6);
					c = s[i++];
					tmp |= (c & 0x3f);
				} else if ((c & 0xc0) == 0xc0) {
					// 2 bytes
					tmp = (c & 0x1f) << 6;
					c = s[i++];
					tmp |= (c & 0x3f);
				} else
					// 1 byte
					tmp = c;
				d += String.fromCharCode(tmp);
			}
			return d;
		}
	},
	Component : {
		/**获取当前网页的URL参数*/
		getEnvironmentMap : function(){
			var firstIndex=window.location.toString().indexOf("?");
			var cfg={};
			if (firstIndex!=-1){
				var purl=decodeURIComponent(window.location.toString().substr(firstIndex + 1));
				var ps=purl.split("&")
				for (var index in ps){
					var psItem=ps[index];
					var k=psItem.split("=")[0];
					var v=psItem.split("=")[1];
					cfg[k]=v;
				}
			}
			return cfg;
		},
		/** 获取组建自身状态 */
		getState : function(jQueryObj) {
			var stateData = jQueryObj.attr("uiState");
			return eval(WebUI.Core.Base64.uncoded64(stateData))[0];
		},
		/** 在客户端改变组建状态（用于组建回溯上一个视图状态的数据），值得注意的是服务端只会处理在服务端定义过的属性。 */
		setStateAtt : function(jQueryObj, attName, newValue) {
			var stateData = jQueryObj.attr("uiState");
			var stateMap = eval(WebUI.Core.Base64.uncoded64(stateData))[0];
			stateMap[attName] = newValue;
			stateData = [ stateMap,WebUI.Core.Component.getChildrenState(jQueryObj) ];
			jQueryObj.attr("uiState", WebUI.Core.Base64.encode64(JSON.stringify(stateData)));
		},
		/** 获取子组建状态 */
		getChildrenState : function(jQueryObj) {
			var stateData = jQueryObj.attr("uiState");
			return eval(WebUI.Core.Base64.uncoded64(stateData))[1];
		},
		/** 执行组件的Action动作 */
		doAction : function(jQueryObj, urlData, okCallBack, errorCallBack) {
			var stateMap = WebUI.Core.Component.getState(jQueryObj);
			var postData = {
				/* 发生事件的组建 */
				"WebUI_PF_Target" : stateMap.id,
				/* 引发的事件 */
				"WebUI_PF_Event" : "",
				/* 不执行渲染 */
				"WebUI_PF_Render" : "No",
				/* 组建状态，使用明文传输以减少服务器反解的压力 */
				"WebUI_PF_State" : WebUI.Core.Base64.uncoded64(jQueryObj.attr("uiState"))
			};
			for ( var k in urlData)
				postData[stateMap.id + ":" + k] = urlData[k];
			if (eval(stateMap.beforeScript) == false)
				return;
			$.ajax({
				type : 'post',
				url : window.location,
				data : postData,
				async : stateMap.async,
				success : function(res) {
					eval(stateMap.afterScript);
					okCallBack(res);
				},
				error : function(XMLHttpRequest, textStatus) {
					eval(stateMap.errorScript);
					errorCallBack(XMLHttpRequest, textStatus);
				}
			});
		},
		/** 触发组建身上的某个事件 */
		doEvent : function(jQueryObj, eventName, urlData) {
			// alert();
			var stateMap = WebUI.Core.Component.getState(jQueryObj);
			var postData = {
				/* 发生事件的组建 */
				"WebUI_PF_Target" : stateMap.id,
				/* 引发的事件 */
				"WebUI_PF_Event" : eventName,
				/* 不执行渲染 */
				"WebUI_PF_Render" : "No",
				/* 组建状态，使用明文传输以减少服务器反解的压力 */
				"WebUI_PF_State" : WebUI.Core.Base64.uncoded64(jQueryObj.attr("uiState"))
			};
			for ( var k in urlData)
				postData[stateMap.id + ":" + k] = urlData[k];
			$.ajax({
				type : 'post',
				url : window.location,
				data : postData,
				async : stateMap.async,
				success : function(res) {
					// okCallBack(res);
				},
				error : function(XMLHttpRequest, textStatus) {
					// errorCallBack(XMLHttpRequest, textStatus);
				}
			});
		}
	}
};
/* 组建初始化 */
$(function() {
	$('[uiState]').each(function() {
		var id = $(this).attr('comID');
		var comType = $(this).attr('comType');
		if (window[id] == null)
			window[id] = eval("new WebUI." + comType + "()");
	});
});
/* -------------------------------------------------------------------- */
/**/
/* -------------------------------------------------------------------- */
/* ui_AjaxButton组建 */
WebUI.ui_AjaxButton = function(cfgMap,targetObj) {
	var _this = this;
	this.config = cfgMap;
	this.state={};
	if (targetObj!=null){
		var stateMap = WebUI.Core.Component.getState($(targetObj));
		for (var i in stateMap)
			this.state[i]=stateMap[i];
	}
	/** 处理ui_AjaxButton的Action动作 */
	this.doAction = function(jQueryObj, okCallBack, errCallBack) {
		WebUI.Core.Component.doAction(jQueryObj, {}, okCallBack, errCallBack);
	};
	/** 响应ui_AjaxButton组建的点击事件。 */
	this.onclick = function(targetObj) {
		_this.doAction($(targetObj));
	};
};
/* ui_Text组建 */
WebUI.ui_Text = function(cfgMap,targetObj) {
	var _this = this;
	this.config = cfgMap;
	this.state={};
	if (targetObj!=null){
		var stateMap = WebUI.Core.Component.getState($(targetObj));
		for (var i in stateMap)
			this.state[i]=stateMap[i];
	}
	/** 处理ui_AjaxButton的Action动作 */
	this.doAction = function(jQueryObj, okCallBack, errCallBack) {
		WebUI.Core.Component.setStateAtt(jQueryObj, "value", jQueryObj.attr("value"));
		WebUI.Core.Component.doAction(jQueryObj, {
			value : jQueryObj.attr("value")
		}, okCallBack, errCallBack);
	};
	/** 响应ui_Text组建的点击事件。 */
	this.onchange = function(targetObj) {
		WebUI.Core.Component.doEvent($(targetObj), "OnChange");
	};
};
/* ui_TargetButton组建 */
WebUI.ui_TargetButton = function(cfgMap,targetObj) {
	var _this = this;
	this.config = cfgMap;
	this.state={};
	if (targetObj!=null){
		var stateMap = WebUI.Core.Component.getState($(targetObj));
		for (var i in stateMap)
			this.state[i]=stateMap[i];
	}
	/** 响应ui_TargetButton组建的点击事件。 */
	this.doAction = function(jQueryObj, okCallBack, errCallBack) {
		var stateMap = WebUI.Core.Component.getState(jQueryObj);
		if (eval(stateMap.beforeScript) == false)
			return;
		var targetObject = $('[comid="' + stateMap.target + '"]');
		if (targetObject.length <= 0)
			return;
		//
		var targetActionFun = eval(stateMap.target + ".doAction");
		if (typeof (targetActionFun) == "function")
			targetActionFun($(targetObject[0]), function() {
				eval(stateMap.afterScript);// 回调OK方法
				okCallBack();
			}, function() {
				eval(stateMap.errorScript);// 回调Error方法
				errCallBack();
			});
	};
	this.onclick = function(targetObj) {
		_this.doAction($(targetObj));
	};
};
/* ui_Page组建 */
WebUI.ui_Page = function(cfgMap,targetObj) {
	var _this = this;
	this.config = cfgMap;
	this.state={};
	if (targetObj!=null){
		var stateMap = WebUI.Core.Component.getState($(targetObj));
		for (var i in stateMap)
			this.state[i]=stateMap[i];
	}
//	this.goPage = function(targetObj, goIndex) {
//		if (typeof (targetObj) == 'string')
//			targetObj = $("[comid='" + targetObj + "']");
//		//
//		var stateMap = WebUI.Core.Component.getState(targetObj);
//		var clickFun=eval(stateMap.clickFun);
//		if (typeof(clickFun)=='function')
//			clickFun.call(_this, targetObj, goIndex);
//	};
//	/**计算在该分页组建上指定页码的起始记录号（只有被实例化之后的组建才可以被调用）*/
//	this.evalRowNum = function(pageNum) {
//		return this.state.pageSize * pageNum + this.state.startWith;
//	};
};