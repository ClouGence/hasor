var more = more;
if (typeof (more) == "undefined")
	more = {
		util : {}
	};
/* ===================================================================== */
more.util.formatData = function(formatData, formater) {
	/* eg:new Data() */
	var type = Object.prototype.toString.call(formatData);
	if (formatData == null || type != "[object Date]")
		var data = new Date();
	else
		var data = formatData;
	/* eg:format="yyyy-MM-dd hh:mm:ss"; */
	var type = Object.prototype.toString.call(formater);
	if (formater == null || type == "undefined")
		var format = "yyyy-MM-dd hh:mm:ss";
	else
		var format = formater;
	var o = {
		"M+" : data.getMonth() + 1, // month
		"d+" : data.getDate(), // day
		"h+" : data.getHours(), // hour
		"m+" : data.getMinutes(), // minute
		"s+" : data.getSeconds(), // second
		"q+" : Math.floor((data.getMonth() + 3) / 3), // quarter
		"S" : data.getMilliseconds()
	// millisecond
	}
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (data.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
}
/* toStringº¯Êý */
more.util.toString = function(o) {
	var type = Object.prototype.toString.call(o);
	if (o == null || type == "undefined")
		// null
		return 'null';
	else if (type == "[object String]")
		// String
		return '"' + o + '"';
	else if (type == "[object Number]")
		// Number
		return o;
	else if (type == "[object Boolean]")
		// Boolean
		return (o == true) ? 'true' : 'false';
	else if (type == "[object Date]")
		// String 2008-12-12 12:12:12
		return '"' + more.util.formatData(o) + '"';
	else if (type == "[object Array]") {
		// Array
		var s = "";
		for ( var i = 0; i < o.length; i++)
			s += more.util.toString(o[i]) + ",";
		// È¥³ýÄ©Î²¶ººÅ
		if (s.substr(s.length - 1, s.length) == ",")
			s = s.substr(0, s.length - 1);
		return "[" + s + "]";
	} else if (type == "[object Object]") {
		// Object
		var s = "";
		for ( var key in o)
			s += key + ":" + more.util.toString(o[key]) + ",";
		// È¥³ýÄ©Î²¶º
		if (s.substr(s.length - 1, s.length) == ",")
			s = s.substr(0, s.length - 1);
		return "{" + s + "}";
	}
};
/* toObjectº¯Êý */
more.util.toObject = function(json) {
	return eval(json);
}
/* ===================================================================== */
more.util.AjaxCall = function(obj) {
	if (typeof (obj) != "object")
		throw "param error!";
	else
		var construction = obj;
	// ============================
	this.CreateXmlHttpRequestObject = function() {
		var xmlHttp;
		try {
			xmlHttp = new XMLHttpRequest();
		} catch (e) {
			var XmlHttpVersions = new Array("MSXML2.XMLHTTP.6.0",
					"MSXML2.XMLHTTP.5.0", "MSXML2.XMLHTTP.4.0",
					"MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP");
			for ( var i = 0; i < XmlHttpVersions.length && !xmlHttp; i++)
				try {
					xmlHttp = new ActiveXObject(XmlHttpVersions[i]);
				} catch (e) {
				}
		}// end try
		if (!xmlHttp)
			alert("Error creating the XMLHttpRequest object.");
		else
			return xmlHttp;
	};
	this.call = function(args) {
		var arg = "";
		for ( var k in args)
			arg += k + "=" + encodeURI(args[k]) + "&";
		if (arg.substr(arg.length - 1, arg.length) == "&")
			arg = arg.substr(0, arg.length - 1);
		var ajax = this.CreateXmlHttpRequestObject();
		ajax.open("post", construction.url, !construction.synchronize);
		ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		ajax.setRequestHeader("Content-Length", arg.length);
		ajax.onreadystatechange = function() {
			if (ajax.readyState == 4) {
				if (ajax.status == 200)
					construction.success(ajax);
				else
					construction.failure(ajax);
			}
		};
		ajax.send(arg);
		return ajax.responseText;
	};// end function call
};
/* ===================================================================== */
more.retain = more.retain;
if (typeof (more.retain) == "undefined")
	more.retain = {
		serverCallURL : ""
	};
more.retain.callServerFunction = function(callName, funArray) {
	var type = Object.prototype.toString.call(funArray);
	var argsArray = null;
	if (type == "[object Array]") {
		argsArray = {};
		if (funArray.length == 1)
			argsArray = funArray[0];
		else
			for ( var i = 0; i < funArray.length; i++)
				argsArray[i] = funArray[i];
	} else
		argsArray = funArray;
	var ajax = new more.util.AjaxCall( {
		url : more.retain.serverCallURL,
		synchronize : true,
		success : function(ajax) {
		},
		failure : function(ajax) {
			throw "more request error.";
		}
	});
	var argsString = more.util.toString(argsArray);
	var result = ajax.call( {
		callName : callName,
		args : argsString
	});
	return more.util.toObject(result);
};