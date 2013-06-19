/*----------------------------------------------------------------------*/
/* UIComponent Component（所有组建的根） */
/* -------------------------------------------------------------------- */
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
	newFo.thisClass = newType;
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
/** 静态方法，创建组建实例对象 */
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
	return newFo;
};
/** 静态类，状态管理 */
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
/** 静态类，Var携带的属性集 */
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
	/** 当前类的类型（在定义类型时赋予） */
	thisClass : null,
	/** 当前组建html元素的JQuery形式。 */
	$e : null,
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
	 */
	doEvent : function(eventName, paramData) {
		if (WebUI.isNaN(paramData) == true)
			paramData = {};
		this.doEventTo(WebUI.getLocal(), eventName, paramData);
	},
	/**
	 * *
	 * 静态方法，向服务器发送事件，不同的是该事件的接收页面又第一个参数指定。注意：由于两个不同的页面其视图结构不一致WebUI_PF_State参数将不会被携带。
	 * @param eventName 事件名
	 * @param paramData 携带的参数
	 */
	doEventTo : function(url, eventName, paramData) {
		if (WebUI.isNaN(paramData) == true)
			paramData = {};
		if (WebUI.isNaN(url) == true)
			throw "send url is null.";
		//
		paramData['url'] = url;// 开始之前
		paramData['event'] = eventName;// 要发送到服务端的事件名。如果指定了invoke参数则该参数会失效。
		paramData['renderType'] = 'No';// 渲染方式：No（不渲染，默认值）Part（渲染target目标，如target为空则不渲染）ALL（渲染整个页）
		return WebUI.call(this, paramData);
	},
	/** 在客户端事件链中加入一个客户端事件的绑定。 */
	bindEvent : function(eventName, fun) {
		var $this=this;
		$(this.getElement()).bind(eventName, function() {
			fun.call($this);
		});
	},
	/** 取消所有已知事件的绑定，使用新的函数绑定到事件上。 */
	onlyBindEvent : function(eventName, fun) {
		var $this=this;
		$(this.getElement()).unbind(eventName).removeAttr("on" + eventName).bind(eventName, function() {
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
		this.doEvent('OnLoadData', {
			/* 携带的参数 */
			'dataMap' : paramData,
			/* 正确的回调 */
			'ajaxAfter' : function(event) {
				if (WebUI.isFun(funOK) == true)
					funOK.call($this, {
						event : event
					});
			},
			/* 错误的回调 */
			'ajaxError' : function(event) {
				if (WebUI.isFun(funError) == true)
					funError.call($this, {
						event : event
					});
			}
		});
	},
	/** 展示组建。 */
	show : function(speed, callback) {
		$(this.getElement()).show(speed, callback);
	},
	/** 隐藏组建。 */
	hide : function(speed, callback) {
		$(this.getElement()).hide(speed, callback);
	},
	/** 执行组建渲染 */
	render : function() {},
	/** 构造方法 */
	"<init>" : function() {
		this.$e = $(this.getElement());
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
		/** 发生事件时携带的附带参数。（RW） */
		this.defineProperty("ajaxParam", "RW");
	}
};
WebUI.Component.$extends("UIComponent", "", {
	/** !关于! */
	"<about>" : function() {
		alert("‘" + this.thisClass + "’类型继承自UIComponent组建。");
	}
});