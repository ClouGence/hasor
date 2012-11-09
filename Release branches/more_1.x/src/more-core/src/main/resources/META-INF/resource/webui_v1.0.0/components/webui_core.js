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
 * 静态方法，向服务器发送事件
 * @param invokeString 要调用的服务器方法
 * @param paramData 携带的参数
 * @param async true|false同步状态（true表示使用同步）
 * @param okCallBack 回调函数
 * @param errorCallBack 回调函数
 */
WebUI.invoke = function(invokeString, paramData, async, okCallBack, errorCallBack) {
	if (WebUI.isNaN(paramData) == true)
		paramData = {};
	if (WebUI.isNaN(async) == true)
		async = false;
	if (WebUI.isNaN(invokeString) == true)
		return;
	/* 准备请求参数 */
	var sendData = {};
	if (WebUI.isObject(paramData) == true)
		for ( var k in paramData)
			sendData[k] = paramData[k];
	/* 携带WebUI头信息 */
	sendData["WebUI_PF_Target"] = "com_root";/* 发生事件的组建 */
	sendData["WebUI_PF_TargetPath"] = "/";/* 发生事件的组建 */
	sendData["WebUI_PF_Ajax"] = true;
	sendData["WebUI_PF_Event"] = "OnInvoke";/* 引发的事件 */
	sendData["WebUI_PF_Render"] = "No";/* 不执行渲染 */
	sendData["WebUI_PF_State"] = "[{},{}]";
	sendData["WebUI_PF_Invoke"] = invokeString;
	/* ajax请求 */
	var postData = WebUI.mapToURI(sendData);
	var res = $.ajax({
		type : 'post',
		url : WebUI.getLocal(),
		data : postData,
		cache : false,
		async : async,
		success : function(res) {
			var eventObject = {
				invoke : invokeString,
				paramData : paramData,
				result : res
			};
			if (WebUI.isFun(okCallBack) == true)
				okCallBack(eventObject);
		},
		error : function(XMLHttpRequest, textStatus) {
			var eventObject = {
				invoke : invokeString,
				paramData : paramData,
				errorID : null,
				message : null,
				trace : null
			};
			if (XMLHttpRequest.status == 20) {
				var returnData = eval('(' + XMLHttpRequest.responseText + ')');
				eventObject.errorID = returnData.errorID;
				eventObject.message = returnData.message;
				eventObject.trace = returnData.trace;
			} else {
				eventObject.errorID = XMLHttpRequest.status;
				eventObject.message = XMLHttpRequest.responseText;
				eventObject.trace = null;
			}
			if (WebUI.isFun(errorCallBack) == true)
				errorCallBack(eventObject);
		}
	});
	return res.responseText;
};
/*----------------------------------------------------------------------------------------------------组建基类*/
WebUI.Component = function() {};
/** 静态方法，用于从WebUI.Component中派生一个新的类型对象。 */
WebUI.Component.$extends = function(newType, superName, define) {
	if (WebUI.Component[newType] != null)
		throw "重复定义：" + newType;
	var superObj = WebUI.Component[superName];
	// A.创建类型对象
	var fo = function() {};
	if (WebUI.isNaN(superObj) == true)
		fo = WebUI.Component;
	else
		fo.prototype = superObj;
	var newFo = new fo();
	newFo.cMode = newType;
	newFo.superClass = superObj;
	// B.赋予新方法
	if (WebUI.isObject(define) == true)
		for ( var k in define)
			newFo[k] = define[k];
	else if (WebUI.isFun(define) == true)
		define.call(newFo);
	// C.代理构造方法，目的是为了让构造方法可以传递调用。
	if (WebUI.isFun(newFo["<init>"]) == false)
		newFo["<init>"] = function() {};
	var userInit = newFo["<init>"];
	newFo["<init>"] = function(realThis) {
		if (WebUI.isNaN(newFo.superClass) == false)
			newFo.superClass["<init>"].call(realThis, realThis);// 根对象是没有superClass的所以要加上判断。
		userInit.call(realThis, realThis);
	};
	// D.保存定义的对象
	WebUI.Component[newType] = newFo;
	return newFo;
};
/** 创建组建实例对象 */
WebUI.Component.create = function(clientID) {
	var targetObj = $("#" + clientID);
	var com_ID = targetObj.attr('comID');
	var clientMode = targetObj.attr('cMode');
	var com_Path = targetObj.attr('comPath');
	// 构建实例对象
	var tarClass = WebUI.Component[clientMode];
	if (WebUI.isObject(tarClass) == false)
		tarClass = new WebUI.Component();
	var fo = function() {};
	fo.prototype = tarClass;
	var newFo = new fo();
	// 赋于属性值
	newFo.clientID = clientID;
	newFo.componentID = com_ID;
	newFo.componentPath = com_Path;
	var newFoState = new WebUI.Component.State(newFo);
	var newFoVar = new WebUI.Component.Variable(newFo);
	newFo.getState = function() {
		return newFoState;
	};
	newFo.getVariableMap = function() {
		return newFoVar;
	};
	// C.调用构造方法
	if (WebUI.isFun(newFo["<init>"]) == true)
		newFo["<init>"](newFo);
	$("#" + clientID)[0].uiObject = newFo;
	return newFo;
};
/** 状态管理 */
WebUI.Component.State = function(component) {
	var target = component;
	/** 获取状态对象操作的那个组建。 */
	this.getTarget = function() {
		return target;
	};
	/** 获取组建状态的编码字符串。 */
	this.getCode = function() {
		return $(this.getTarget().getElement()).attr("uiState");
	};
	/** 设置组建状态的编码字符串。 */
	this.setCode = function(newState) {
		return $(this.getTarget().getElement()).attr("uiState", newState);
	};
	/** 获取组建状态数据jsonMap */
	this.getArray = function() {
		var comStateData = this.getCode();
		if (WebUI.isNaN(comStateData) == true) {
			// TODO : 从全局的ViewData中获取状态数据。
		}
		return eval(WebUI.Base64.uncoded64(comStateData));
	};
	/** 获取组建的某个状态属性。 */
	this.get = function(attName) {
		var array = this.getArray();
		if (WebUI.isArray(array) == false)
			return null;
		if (array.length == 0)
			return null;
		return array[0][attName];
	};
	/** 在客户端改变组建状态（用于组建回溯上一个视图状态的数据），值得注意的是服务端只会处理在服务端定义过的属性。 */
	this.set = function(attName, newValue) {
		var array = this.getArray();
		if (WebUI.isArray(array) == false)
			array = [];
		if (array.length == 0)
			array.push({});// 自身状态
		if (array.length == 1)
			array.push({});// 孩子状态
		// 写
		array[0][attName] = newValue;
		var newCode = WebUI.Base64.encode64(JSON.stringify(array));
		this.setCode(newCode);
	};
};
/** Var携带的属性集 */
WebUI.Component.Variable = function(component) {
	var target = component;
	/** 获取属性集对象操作的那个组建。 */
	this.getTarget = function() {
		return target;
	};
	var dataMap = {};
	/** 获取Variable的Map形式 */
	this.getDataMap = function() {
		return dataMap;
	};
	/** 清空Variable内部的所有属性 */
	this.clear = function() {
		dataMap = {};
	};
	/** 获取组建状态数据jsonMap */
	this.getArray = function() {
		var array = new Array();
		var map = this.getDataMap();
		for ( var k in map) {
			var obj = {};
			obj[k] = map[k];
			array.push(obj);
		}
		return array;
	};
	/** 获取组建的某个状态属性。 */
	this.get = function(attName) {
		return this.getDataMap()[attName];
	};
	/** 在客户端改变组建状态（用于组建回溯上一个视图状态的数据），值得注意的是服务端只会处理在服务端定义过的属性。 */
	this.set = function(attName, newValue) {
		this.getDataMap()[attName] = newValue;
	};
};
/** 组建原型 */
WebUI.Component.prototype = {
	/** 组建的客户端ID（在WebUI.Component.create方法中赋予） */
	clientID : null,
	/** 组建的服务器ID（在WebUI.Component.create方法中赋予） */
	componentID : null,
	/** 组建的服务器上的路径（在WebUI.Component.create方法中赋予） */
	componentPath : null,
	/** 父类类型（在定义类型时赋予） */
	superClass : null,
	/** 服务端组建模型。 */
	serverMode : function() {
		return $(this.getElement()).attr("sMode");
	},
	/** 客户端组建模型。 */
	clientMode : function() {
		return $(this.getElement()).attr("cMode");
	},
	/** 获取组建的Dom标签 */
	getElement : function() {
		return $("#" + this.clientID)[0];
	},
	/** 静态方法，用于获取当前网页的URL参数。 */
	getEnvironmentMap : WebUI.getEnvironmentMap,
	/** 获取组建自身状态（在定义类型时赋予） */
	getState : null,
	/** 携带属性集（在定义类型时赋予） */
	getVariableMap : null,
	/** 设置一个绑定参数，该绑定参数会在触发服务端事件时携带。 */
	getVar : function(varKey) {
		return this.getVariableMap().get(varKey);
	},
	/** 设置一个绑定参数，该绑定参数会在触发服务端事件时携带。 */
	setVar : function(varKey, varValue) {
		this.getVariableMap().set(varKey, varValue);
	},
	/** 组建是否为一个表单元素 */
	isForm : function() {
		return false;
	},
	/**
	 * 静态方法，向服务器发送事件
	 * @param eventName 事件名
	 * @param paramData 携带的参数
	 * @param okCallBack 回调函数
	 * @param errorCallBack 回调函数
	 */
	doEvent : function(eventName, paramData, okCallBack, errorCallBack) {
		if (WebUI.isNaN(paramData) == true)
			paramData = {};
		paramData["WebUI_PF_State"] = WebUI.Base64.uncoded64(this.getState().getCode());
		this.doEventTo(WebUI.getLocal(), eventName, paramData, okCallBack, errorCallBack);
	},
	/**
	 * *
	 * 静态方法，向服务器发送事件，不同的是该事件的接收页面又第一个参数指定。注意：由于两个不同的页面其视图结构不一致WebUI_PF_State参数将不会被携带。
	 * @param eventName 事件名
	 * @param paramData 携带的参数
	 * @param okCallBack 回调函数
	 * @param errorCallBack 回调函数
	 */
	doEventTo : function(url, eventName, paramData, okCallBack, errorCallBack) {
		if (WebUI.isNaN(paramData) == true)
			paramData = {};
		/* 不传事件名不处理事件 */
		if (WebUI.isNaN(eventName) == true)
			return;
		/* beforeScript */
		var beforeRes = WebUI.runSrcipt(this.beforeScript(), this, {
			event : {
				eventName : eventName,
				target : this,
				paramData : paramData
			}
		});
		if (WebUI.isNaN(beforeRes) == true || beforeRes == true) {} else
			return;
		/* 准备请求参数：1.绑定的值，2.paramData参数值 */
		var sendData = {};
		var varDataMap = this.getVariableMap().getDataMap();
		for ( var k in varDataMap) {
			var v = varDataMap[k];
			if (WebUI.isFun(v) == true)
				sendData[k] = v(this);
			else
				sendData[k] = v;
		}
		if (WebUI.isObject(paramData) == true)
			for ( var k in paramData)
				sendData[k] = paramData[k];
		/* 携带WebUI头信息 */
		sendData["WebUI_PF_Target"] = this.componentID;/* 发生事件的组建 */
		sendData["WebUI_PF_TargetPath"] = this.componentPath;/* 发生事件的组建 */
		sendData["WebUI_PF_Ajax"] = true;
		sendData["WebUI_PF_Event"] = eventName;/* 引发的事件 */
		sendData["WebUI_PF_Render"] = "No";/* 不执行渲染 */
		sendData["WebUI_PF_State"] = WebUI.Base64.uncoded64(this.getState().getCode());
		sendData["WebUI_PF_Invoke"] = null;
		/* ajax请求 */
		var afterScr = this.afterScript();
		var errorScr = this.errorScript();
		var _this = this;
		var postData = WebUI.mapToURI(sendData);
		$.ajax({
			type : 'post',
			url : url,
			data : postData,
			cache : false,
			async : this.async(),
			success : function(res) {
				var eventObject = {
					event : {
						eventName : eventName,
						target : _this,
						paramData : paramData,
						result : res
					}
				};
				if (WebUI.isNaN(afterScr) == false)
					WebUI.runSrcipt(afterScr, _this, eventObject);
				if (WebUI.isFun(okCallBack) == true)
					okCallBack.call(_this, eventObject.event);
			},
			error : function(XMLHttpRequest, textStatus) {
				var eventObject = {
					event : {
						eventName : eventName,
						target : _this,
						paramData : paramData,
						errorID : null,
						message : null,
						trace : null
					}
				};
				if (XMLHttpRequest.status == 20) {
					var returnData = eval('(' + XMLHttpRequest.responseText + ')');
					eventObject.event.errorID = returnData.errorID;
					eventObject.event.message = returnData.message;
					eventObject.event.trace = returnData.trace;
				} else {
					eventObject.event.errorID = XMLHttpRequest.status;
					eventObject.event.message = XMLHttpRequest.responseText;
					eventObject.event.trace = null;
				}
				if (WebUI.isNaN(errorScr) == false)
					WebUI.runSrcipt(errorScr, _this, eventObject);
				if (WebUI.isFun(errorCallBack) == true)
					errorCallBack.call(_this, eventObject.event);
			}
		});
	},
	/** 在客户端事件链中加入一个客户端事件的绑定。 */
	bindEvent : function(eventName, fun) {
		$(this.getElement()).bind(eventName, function() {
			var $this = this.uiObject;// this 是Element元素对象
			fun.call($this);
		});
	},
	/** 取消所有已知事件的绑定，使用新的函数绑定到事件上。 */
	onlyBindEvent : function(eventName, fun) {
		$(this.getElement()).unbind(eventName).removeAttr("on" + eventName).bind(eventName, function() {
			var $this = this.uiObject;// this 是Element元素对象
			fun.call($this);
		});
	},
	/** 定义一个组建属性 */
	defineProperty : function(property, p1, p2) {
		if (WebUI.isNaN(p1) == true)
			throw new "除了属性名称之外必须指定一个属性访问权限参数或者一个到两个属性读写方法.";
		//
		if (typeof (p1) == 'string') {
			// 1.简单的属性添加。
			this[property] = function(newValue) {
				var name = arguments.callee.propertyName;// 获取绑定在函数自身上的name属性，该属性是由defineProperty方法赋予的。
				var access = arguments.callee.access;// 获取绑定在函数自身上的name属性，该属性是由defineProperty方法赋予的。
				if (typeof (newValue) != 'undefined') {
					if (/.*[Ww].*/.test(access) == true)
						this.getState().set(name, newValue);// set method
				} else {
					if (/.*[Rr].*/.test(access) == true)
						return this.getState().get(name);// get method
				}
			};
			this[property].propertyName = property;
			this[property].access = p1;
		} else if (WebUI.isFun(p1) == true) {
			// 2.自定义get/set方法的属性添加。
			this[property] = function(newValue) {
				var readMethod = arguments.callee.readMethod;// 获取绑定在函数自身上的name属性，该属性是由defineProperty方法赋予的。
				var writeMethod = arguments.callee.writeMethod;// 获取绑定在函数自身上的name属性，该属性是由defineProperty方法赋予的。
				//
				if (typeof (newValue) != 'undefined')
					writeMethod.call(this, newValue);// set method
				else
					return readMethod.call(this);// get method
			};
			this[property].readMethod = (p1 == null) ? jQuery.noop : p1;
			this[property].writeMethod = (p2 == null) ? jQuery.noop : p2;
		} else
			throw "未明确或不支持的类型.预期是string\function";
	},
	/** 从服务器上载入数据 */
	loadData : function(paramData, funOK, funError) {
		if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
			return;
		var $this = this;
		this.doEvent("OnLoadData", paramData, function(event) {
			if (WebUI.isFun(funOK) == true)
				funOK.call($this, {
					event : event
				});
		}, function(event) {
			if (WebUI.isFun(funError) == true)
				funError.call($this, {
					event : event
				});
		});
	},
	/** 执行组建渲染 */
	render : function() {},
	/** 构造方法 */
	"<init>" : function() {
		/** 客户端在请求之前进行的调用，返回false取消本次ajax请求（R） */
		this.defineProperty("beforeScript", "R");
		/** 客户端脚本回调函数（R） */
		this.defineProperty("afterScript", "R");
		/** 调用错误回调函数（R） */
		this.defineProperty("errorScript", "R");
		/** Ajax是否使用同步操作（R） */
		this.defineProperty("async", "R");
		/** 当发生事件OnLoadData时触发，该事件允许用户通过任意组建从服务端装载数据到客户端。（R） */
		this.defineProperty("onLoadDataEL", "R");
	}
};