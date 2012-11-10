/*----------------------------------------------------------------------------------------------------常量定义*/
if (typeof (WebUI_Var_Library) == 'undefined')
	WebUI_Var_Library = null;// webui lib所处的目录
var WebUI = function(serverID) {
	var res = $("[comid=" + serverID + "]");
	if (res.length != 0)
		if (WebUI.isNaN(res[0].uiObject) == true)
			return WebUI.Component.create($(res[0]).attr('id'));
		else
			return res[0].uiObject;
};
WebUI.init = function() {
	$('[sMode]').each(function() {
		WebUI.Component.create($(this).attr('id'));
	});
	$('[sMode]').each(function() {
		if (WebUI.isFun(this.uiObject.render) == true)
			this.uiObject.render();
	});
};
/* 客户端组建初始化 */
$(WebUI.init);
/*----------------------------------------------------------------------------------------------------util*/
/** 获取变量。 */
WebUI.variable = function(varName) {
	if (varName == 'WebUI_Var_Library')
		return (typeof (WebUI_Var_Library) != 'undefined') ? WebUI_Var_Library : "";
};
/** Web UI Base64工具 */
WebUI.Base64 = {
	Chars : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*-",
	/* 对字符串做Base64编码。 */
	encode64 : function(s) {
		if (!s || s.length == 0)
			return s;
		var d = "";
		var b = this.ucs2_utf8(s);
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
			d += this.Chars.charAt(b0);
			d += this.Chars.charAt(b1);
			d += this.Chars.charAt(b2);
			d += this.Chars.charAt(b3);
		}
		return d;
	},
	/* 对字符串做Base64解码。 */
	uncoded64 : function(s) {
		if (!s)
			return null;
		var len = s.length;
		if (len % 4 != 0)
			throw s + " is not a valid Base64 string.";
		var b = new Array();
		var i = 0, j = 0, e = 0, c, tmp;
		while (i < len) {
			c = this.Chars.indexOf(s.charAt(i++));
			tmp = c << 18;
			c = this.Chars.indexOf(s.charAt(i++));
			tmp |= c << 12;
			c = this.Chars.indexOf(s.charAt(i++));
			if (c < 64) {
				tmp |= c << 6;
				c = this.Chars.indexOf(s.charAt(i++));
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
		return this.utf8_ucs2(b);
	},
	/**/
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
	/**/
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
};
/** 判断目标是否为空、未定义、空字符串 */
WebUI.isNaN = function(target) {
	if (target == null || target === "" || typeof (target) == 'undefined')
		return true;
	else
		return false;
};
WebUI.isFun = function(target) {
	if (typeof (target) == 'function')
		return true;
	else
		return false;
};
WebUI.isObject = function(target) {
	if (typeof (target) == 'object')
		return true;
	else
		return false;
};
WebUI.isArray = function(target) {
	return target instanceof Array;
};
/** 获取当前网页的URL */
WebUI.getLocal = function() {
	var localStr = window.location.toString();
	var firstIndex = localStr.indexOf("?");
	if (firstIndex <= 0)
		return localStr;
	return localStr.substr(0, firstIndex + 1);
};
/** 获取当前网页的URL参数 */
WebUI.getEnvironmentMap = function() {
	var localStr = window.location.toString();
	var firstIndex = localStr.indexOf("?");
	var cfg = {};
	if (firstIndex != -1) {
		var purl = decodeURIComponent(localStr.substr(firstIndex + 1));
		var ps = purl.split("&");
		for ( var index in ps) {
			var psItem = ps[index];
			var k = psItem.split("=")[0];
			var v = psItem.split("=")[1];
			cfg[k] = v;
		}
	}
	return cfg;
};
/** 删掉位于字符串最末尾的字符，如果它存在的话。 */
WebUI.deleteLast = function(stringData, words) {
	if (stringData.length < words)
		return stringData;
	if (stringData.substr(stringData.length - words.length) != words)
		return stringData;
	// 删掉最后一个,
	return stringData.substr(0, stringData.length - words.length)
}
/** 将一个map对象转换成为uri请求参数（不支持符合对象）。 */
WebUI.mapToURI = function(mapObject) {
	var postData = "";
	for ( var k in mapObject) {
		var v = mapObject[k];
		if (WebUI.isArray(v) == true)
			for ( var i = 0; i < v.length; i++)
				postData += (encodeURIComponent(k) + "=" + encodeURIComponent(v[i]) + "&");
		else
			postData += (encodeURIComponent(k) + "=" + encodeURIComponent(v) + "&");
	}
	return WebUI.deleteLast(postData, '&');// 删掉最后一个&
};
/** 执行字符串，如果定义的只是函数名则调用该函数，如果是脚本字符串则执行脚本（注：如果脚本返回的是函数则这个函数也会被执行）。 */
WebUI.runSrcipt = function(scriptContext, thisContext, paramMap) {
	WebUI.runSrcipt.$Key = null;
	WebUI.runSrcipt.$Var = null;
	WebUI.runSrcipt.$ParamMap = paramMap;
	WebUI.runSrcipt.$ParamArray = new Array();
	// 1.准备环境变量
	for (WebUI.runSrcipt.$Key in WebUI.runSrcipt.$ParamMap) {
		WebUI.runSrcipt.$Var = WebUI.runSrcipt.$ParamMap[WebUI.runSrcipt.$Key];
		WebUI.runSrcipt.$ParamArray.push(WebUI.runSrcipt.$Var);
		eval(WebUI.runSrcipt.$Key + "=WebUI.runSrcipt.$Var");
	}
	// 2.执行脚本
	var e = (new Function("return " + scriptContext)).call(thisContext);
	if (WebUI.isFun(e) == true)
		e = e.apply(thisContext, WebUI.runSrcipt.$ParamArray);
	return e;
};
/**
 * paramMap['ajaxBefore'] = null;// 开始之前 paramMap['ajaxAfter'] = null;// 正确回调
 * paramMap['ajaxError'] = null;// 错误回调 paramMap['dataMap'] = {};// 请求参数
 * paramMap['invoke'] = null;//
 * 引发服务端OnInvoke事件，invoke代表要在服务端执行的EL表达式。（注意：如果指定invoke参数则event参数会失效）
 * paramMap['event'] = null;// 要发送到服务端的事件名。如果指定了invoke参数则该参数会失效。
 * paramMap['dataMap'] = {};// 请求参数 paramMap['renderType'] = "No";//
 * 渲染方式：No（不渲染，默认值）Part（渲染target目标，如target为空则不渲染）ALL（渲染整个页） paramMap['async'] =
 * true;// true表示同步
 */
WebUI.call = function(target, paramMap) {
	if (WebUI.isNaN(paramMap) == true) {
		paramMap = {};
		paramMap['url'] = WebUI.getLocal();// 开始之前
		paramMap['ajaxBefore'] = null;// 开始之前
		paramMap['ajaxAfter'] = null;// 正确回调
		paramMap['ajaxError'] = null;// 错误回调
		paramMap['dataMap'] = {};// 请求参数
		paramMap['invoke'] = null;// 引发服务端OnInvoke事件，invoke代表要在服务端执行的EL表达式。（注意：如果指定invoke参数则event参数会失效）
		paramMap['event'] = null;// 要发送到服务端的事件名。如果指定了invoke参数则该参数会失效。
		paramMap['renderType'] = 'No';// 渲染方式：No（不渲染，默认值）Part（渲染target目标，如target为空则不渲染）ALL（渲染整个页）
		paramMap['async'] = true;// true表示同步
	}
	/* 1.最终发送的数据对象 */
	var sendData = {};
	/* 发生ajax之前的函数 */
	var beforeFun = function() {
		return true;
	};
	/* 发生ajax成功之后的函数 */
	var afterFun = function() {
		return true;
	};
	/* 发生ajax错误的函数 */
	var errorFun = function() {
		return true;
	};
	var url = (WebUI.isNaN(paramMap['url']) == true) ? WebUI.getLocal() : paramMap['url'];
	var async = (WebUI.isNaN(paramMap['async']) == true) ? true : paramMap['async'];
	// 2.默认请求头
	sendData['WebUI_PF_Target'] = 'com_root';/* 发生事件的组建 */
	sendData['WebUI_PF_TargetPath'] = '/';/* 发生事件的组建 */
	sendData['WebUI_PF_State'] = '[{},{}]';/* 状态数据 */
	sendData['WebUI_PF_Invoke'] = null;
	sendData['WebUI_PF_Ajax'] = true;
	sendData['WebUI_PF_Event'] = 'OnInvoke';/* 引发的事件 */
	sendData['WebUI_PF_Render'] = 'No';/* 不执行渲染 */
	/* 3.处理paramMap数据 */
	/* ajaxBefore */
	if (WebUI.isFun(paramMap['ajaxBefore']) == true)
		beforeFun = paramMap['ajaxBefore'];
	/* ajaxAfter */
	if (WebUI.isFun(paramMap['ajaxAfter']) == true)
		afterFun = paramMap['ajaxAfter'];
	/* ajaxError */
	if (WebUI.isFun(paramMap['ajaxError']) == true)
		errorFun = paramMap['ajaxError'];
	/* dataMap */
	var dataMap = paramMap.dataMap;
	if (WebUI.isObject(dataMap) == true)
		for ( var k in dataMap)
			sendData[k] = dataMap[k];
	/* invoke */
	sendData['WebUI_PF_Invoke'] = paramMap['invoke'];
	if (WebUI.isNaN(sendData['WebUI_PF_Invoke']) == false)
		sendData['WebUI_PF_Event'] = 'OnInvoke';/* 引发的事件 */
	else if (WebUI.isNaN(paramMap['event']) == false)
		sendData['WebUI_PF_Event'] = paramMap['event'];/* 引发的事件 */
	/* renderType,渲染方式：No（不渲染，默认值）Part（渲染target目标，如target为空则不渲染）ALL（渲染整个页） */
	if (WebUI.isNaN(paramMap['renderType']) == false) {
		var renderType = paramMap['renderType'];
		if (renderType == 'No')
			sendData['WebUI_PF_Render'] = 'No';
		else if (renderType == 'Part')
			sendData['WebUI_PF_Render'] = 'Part';
		else if (renderType == 'ALL')
			sendData['WebUI_PF_Render'] = 'ALL';
	}
	/* 4.获取target组建对象携带的参数信息 */
	if (WebUI.isNaN(target) == false) {
		sendData['WebUI_PF_Target'] = target.componentID;/* 发生事件的组建 */
		sendData['WebUI_PF_TargetPath'] = target.componentPath;/* 发生事件的组建 */
		sendData['WebUI_PF_State'] = WebUI.Base64.uncoded64(target.getState().getCode());
		/* 准备请求参数：1.绑定的值，2.paramData参数值 */
		var varDataMap = target.getVariableMap().getDataMap();
		for ( var k in varDataMap) {
			var v = varDataMap[k];
			if (WebUI.isFun(v) == true)
				sendData[k] = v(target);
			else
				sendData[k] = v;
		}
		/* 得到after，before，errorFun */
		if (WebUI.isNaN(target.beforeScript()) == false) {
			var _before = beforeFun;
			beforeFun = function(res) {
				if (_before.call(target, res) == false)
					return;
				return new Function('event', 'return ' + target.beforeScript() + ';').call(target, res);
			}
		}
		if (WebUI.isNaN(target.afterScript()) == false) {
			var _afterFun = afterFun;
			afterFun = function(res) {
				if (_afterFun.call(target, res) == false)
					return;
				return new Function('event', 'return ' + target.afterScript() + ';').call(target, res);
			}
		}
		if (WebUI.isNaN(target.errorScript()) == false) {
			var _errorFun = errorFun;
			errorFun = function(res) {
				if (_errorFun.call(target, res) == false)
					return;
				return new Function('event', 'return ' + target.errorScript() + ';').call(target, res);
			}
		}
		/* async */
		async = target.async();
	}
	/* 5.固定的信息 */
	sendData['WebUI_PF_Ajax'] = true;
	/* 6.处理beforeFun */
	var beforeRes = beforeFun.call(target, {
		eventName : sendData['WebUI_PF_Event'],
		target : target,
		paramData : paramMap
	});
	if (WebUI.isNaN(beforeRes) == true || beforeRes == true) {} else
		return;
	/* 7.处理beforeFun */
	var postData = WebUI.mapToURI(sendData);
	var res = $.ajax({
		type : 'post',
		url : url,
		data : postData,
		cache : false,
		async : async,
		success : function(res) {
			var event = {
				eventName : sendData['WebUI_PF_Event'],
				target : target,
				paramData : paramMap,
				result : res
			};
			var returnData = afterFun.call(target, event);
			return returnData;
		},
		error : function(XMLHttpRequest, textStatus) {
			var event = {
				eventName : sendData['WebUI_PF_Event'],
				target : target,
				paramData : paramMap,
				errorID : null,
				message : null,
				trace : null
			};
			if (XMLHttpRequest.status == 20) {
				var returnData = eval('(' + XMLHttpRequest.responseText + ')');
				event.errorID = returnData.errorID;
				event.message = returnData.message;
				event.trace = returnData.trace;
			} else {
				event.errorID = XMLHttpRequest.status;
				event.message = XMLHttpRequest.responseText;
				event.trace = null;
			}
			return errorFun.call(target, event);
		}
	});
	return res;
};
/**
 * 静态方法，向服务器发送事件
 * @param invokeString 要调用的服务器方法
 * @param paramData 携带的参数
 * @param async true|false同步状态（true表示使用同步）
 * @param okCallBack 回调函数
 * @param errorCallBack 回调函数
 */
WebUI.invoke = function(invokeString, paramData, async, okCallBack, errorCallBack) {
	var paramMap = {};
	paramMap['ajaxBefore'] = null;// 开始之前
	if (WebUI.isFun(okCallBack) == true)
		paramMap['ajaxAfter'] = okCallBack;// 正确回调
	if (WebUI.isFun(errorCallBack) == true)
		paramMap['ajaxError'] = errorCallBack;// 错误回调
	paramMap['dataMap'] = paramData;// 请求参数
	paramMap['invoke'] = invokeString;// 引发服务端OnInvoke事件，invoke代表要在服务端执行的EL表达式。（注意：如果指定invoke参数则event参数会失效）
	paramMap['async'] = (WebUI.isNaN(async) == true) ? true : async;
	return WebUI.call(null, paramMap);
};