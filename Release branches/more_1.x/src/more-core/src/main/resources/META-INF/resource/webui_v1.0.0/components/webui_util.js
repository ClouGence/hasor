/** 内部方法：Web UI Base64工具 */
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
/** 内部方法：抛出错误。 */
WebUI.throwError = function(throwObject, params) {
	if (WebUI.variable('WebUI_Debug') == true) {
		var trace = '<b>Throw:</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + throwObject + '<br/>';
		var params = '<b>Params:</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + JSON.stringify(params) + '<br/>';
		var caller = arguments.callee;
		var stack = [], caller = arguments.callee.caller;
		while (caller) {
			stack.unshift(WebUI.getFunctionName(caller));
			caller = caller && caller.caller;
		}
		callStack = '<b>CallStack:</b><pre>' + stack.join('\n') + '</pre>';
		WebUI.dialog({
			title : 'Debug Throw：' + throwObject,
			cancelVal : '关闭',
			cancel : true,
			width : 500,
			max : false,
			content : '<div style="height:350px;overflow:scroll">' + trace + params + callStack + '</div>'
		});
	}
	throw throwObject;
};
/** 内部方法：删掉位于字符串最末尾的字符，如果它存在的话。 */
WebUI.deleteLast = function(stringData, words) {
	if (stringData.length < words)
		return stringData;
	if (stringData.substr(stringData.length - words.length) != words)
		return stringData;
	// 删掉最后一个,
	return stringData.substr(0, stringData.length - words.length)
};
/** 内部方法：获取函数的函数名 */
WebUI.getFunctionName = function(func) {
	if (typeof func == 'function' || typeof func == 'object') {
		var name = ('' + func).match(/function\s*([\w\$]*)\s*\(/);
	}
	return name && name[1];
};