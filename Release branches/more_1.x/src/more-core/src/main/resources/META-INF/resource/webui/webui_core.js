var WebUI;
if (WebUI == undefined) {
    WebUI = function(serverID) {
        var res = $("[comid=" + serverID + "]");
        if (res.length != 0)
            return res[0].uiObject;
    };
}
/*----------------------------------------------------------------------------------------------------util*/
/** Web UI 工具方法 */
WebUI.util = {
    b64 : {
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
/** 执行字符串，如果定义的只是函数名则调用该函数，如果是脚本字符串则执行脚本（注：如果脚本返回的是函数则这个函数也会被执行）。 */
WebUI.runSrcipt = function(scriptText, thisContext, paramObj) {
    var e = eval.call(thisContext, scriptText);
    if (WebUI.isFun(e) == true)
        return e.apply(thisContext, paramObj);
    return e;
};
/*----------------------------------------------------------------------------------------------------组建基类*/
WebUI.Component = function() {};
/** 静态方法，用于从WebUI.Component中派生一个新的类型。 */
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
    newFo.getClass = function() {
        return newType;
    };
    newFo.superClass = superObj;
    // B.赋予新方法
    if (WebUI.isObject(define) == true)
        for ( var k in define)
            newFo[k] = define[k];
    else if (WebUI.isFun(define) == true)
        define.call(newFo);
    WebUI.Component[newType] = newFo;
    return newFo;
};
/** 创建组建对象 */
WebUI.Component.create = function(clientID) {
    var targetObj = $("#" + clientID);
    var com_ID = targetObj.attr('comID');
    var com_Type = targetObj.attr('comType');
    //
    var tarClass = WebUI.Component[com_Type];
    if (WebUI.isObject(tarClass) == false)
        tarClass = new WebUI.Component();
    var fo = function() {};
    fo.prototype = tarClass;
    var newFo = new fo();
    newFo.clientID = clientID;
    newFo.componentID = com_ID;
    // C.调用构造方法
    if (WebUI.isFun(newFo["<init>"]) == true)
        newFo["<init>"]();
    $("#" + clientID)[0].uiObject = newFo;
    return newFo;
};
/** 组建原型 */
WebUI.Component.prototype = {
    /** 组建的客户端ID（在WebUI.Component.create方法中赋予） */
    clientID : null,
    /** 组建的服务器ID（在WebUI.Component.create方法中赋予） */
    componentID : null,
    /** 父类类型（在定义类型时赋予） */
    superClass : null,
    /** 组建类型。 */
    getClass : function() {
        return "UIComponent";
    },
    /** 获取组建的Dom标签 */
    getElement : function() {
        return $("#" + this.clientID)[0];
    },
    /** 从当前对象类型上派生一个新的子类。 */
    $extends : function(newType, define) {
        return WebUI.Component.$extends(newType, this.getClass(), define);
    },
    /** 静态方法，用于获取当前网页的URL参数。 */
    getEnvironmentMap : WebUI.getEnvironmentMap,
    /** 获取组建自身状态 */
    getState : function() {
        var sMap = WebUI.Component.State;
        return new sMap(this);
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
        /* 不传事件名不处理事件 */
        if (WebUI.isNaN(eventName) == true)
            return;
        /* 准备请求参数 */
        var sendData = {};
        if (WebUI.isObject(paramData) == true)
            for ( var k in paramData)
                sendData[k] = paramData[k];
        /* 携带WebUI头信息 */
        sendData["WebUI_PF_Target"] = this.componentID;/* 发生事件的组建 */
        sendData["WebUI_PF_Event"] = eventName;/* 引发的事件 */
        sendData["WebUI_PF_Render"] = "No";/* 不执行渲染 */
        sendData["WebUI_PF_State"] = WebUI.util.b64.uncoded64(this.getState().getCode());
        /* beforeScript */
        var beforeRes = WebUI.runSrcipt(this.beforeScript(), this, [ eventName, paramData ]);
        if (WebUI.isNaN(beforeRes) == true || beforeRes == true) {} else
            return;
        /* ajax请求 */
        var afterScr = this.afterScript();
        var errorScr = this.errorScript();
        var _this = this;
        var postData = "";
        for ( var k in sendData) {
            var v = sendData[k];
            if (WebUI.isArray(v) == true)
                for ( var i = 0; i < v.length; i++)
                    postData += (encodeURIComponent(k) + "=" + encodeURIComponent(v[i]) + "&");
            else
                postData += (encodeURIComponent(k) + "=" + encodeURIComponent(v) + "&");
        }
        $.ajax({
            type : 'post',
            url : window.location,
            data : postData,
            async : this.async(),
            success : function(res) {
                if (WebUI.isNaN(afterScr) == false)
                    WebUI.runSrcipt(afterScr, _this, [ eventName, paramData, res ]);
                if (WebUI.isFun(okCallBack) == true)
                    okCallBack(res);
            },
            error : function(XMLHttpRequest, textStatus) {
                if (WebUI.isNaN(errorScr) == false)
                    WebUI.runSrcipt(errorScr, _this, [ eventName, paramData, XMLHttpRequest, textStatus ]);
                if (WebUI.isFun(errorCallBack) == true)
                    errorCallBack(XMLHttpRequest, textStatus);
            }
        });
    },
    /** 设置客户端事件 */
    bindEvent : function(eventName, fun) {
        $(this.getElement()).bind(eventName, function() {
            var $this = this.uiObject;// this 是Element元素对象
            fun.call($this);
        });
    },
    /** 在客户端事件链中加入一个客户端事件的绑定。 */
    onlyBindEvent : function(eventName, fun) {
        $(this.getElement()).unbind(eventName).removeAttr("on" + eventName).bind(eventName, function() {
            var $this = this.uiObject;// this 是Element元素对象
            fun.call($this);
        });
    },
    /** 定义一个组建属性 */
    defineProperty : function(name, define) {
    // TODO
    },
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    /** 客户端在请求之前进行的调用，返回false取消本次ajax请求，只读 */
    beforeScript : function() {
        return this.getState().get("beforeScript");
    },
    /** 客户端脚本回调函数，只读 */
    afterScript : function() {
        return this.getState().get("afterScript");
    },
    /** 调用错误回调函数，只读 */
    errorScript : function() {
        return this.getState().get("errorScript");
    },
    /** Ajax是否使用同步操作，只读 */
    async : function() {
        return this.getState().get("async");
    }
};
/*----------------------------------------------------------------------------------------------------状态管理*/
WebUI.Component.State = function(component) {
    var target = component;
    /** 获取状态对象操作的那个组建。 */
    this.getTarget = function() {
        return target;
    };
};
/** 获取组建状态的编码字符串。 */
WebUI.Component.State.prototype.getCode = function() {
    return $(this.getTarget().getElement()).attr("uiState");
};
/** 设置组建状态的编码字符串。 */
WebUI.Component.State.prototype.setCode = function(newState) {
    return $(this.getTarget().getElement()).attr("uiState", newState);
};
/** 获取组建状态数据jsonMap */
WebUI.Component.State.prototype.getArray = function() {
    var comStateData = this.getCode();
    if (WebUI.isNaN(comStateData) == true) {
        // TODO : 从全局的ViewData中获取状态数据。
    }
    return eval(WebUI.util.b64.uncoded64(comStateData));
};
/** 获取组建的某个状态属性。 */
WebUI.Component.State.prototype.get = function(attName) {
    var array = this.getArray();
    if (WebUI.isArray(array) == false)
        return null;
    if (array.length == 0)
        return null;
    return array[0][attName];
};
/** 在客户端改变组建状态（用于组建回溯上一个视图状态的数据），值得注意的是服务端只会处理在服务端定义过的属性。 */
WebUI.Component.State.prototype.set = function(attName, newValue) {
    var array = this.getArray();
    if (WebUI.isArray(array) == false)
        array = [];
    if (array.length == 0)
        array.push({});// 自身状态
    if (array.length == 1)
        array.push({});// 孩子状态
    // 写
    array[0][attName] = newValue;
    var newCode = WebUI.util.b64.encode64(JSON.stringify(array));
    this.setCode(newCode);
};
/*----------------------------------------------------------------------------------------------------Demo*/
/* 客户端组建初始化 */
$(function() {
    $('[comType]').each(function() {
        WebUI.Component.create($(this).attr('id'));
    });
});
// /** 创建类型A */
// var classA = WebUI.Component.extends("classA", {
// "<init>" : function() {
// alert("构造方法，" + this.class);
// },
// printType : function() {
// alert("this is classA.");
// }
// });
// /** 创建类型B */
// var classB = WebUI.Component.extends("classB", {
// "<init>" : function() {
// alert("构造方法，" + this.class);
// },
// printType : function() {
// alert("this is classB.");
// }
// });
// /** 创建类型C，继承自A */
// var classC = classA.extends("classC", function() {
// this["<init>"] = function() {
// alert("构造方法，" + this.class);
// };
// });
// //
// classA.printType();
// classB.printType();
// classC.printType();
// var encode = WebUI.util.b64.encode64('你好 Hello');
// alert(encode);
// var encode = WebUI.util.b64.uncoded64(encode);
// alert(encode);
