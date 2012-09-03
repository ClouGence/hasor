/* -------------------------------------------------------------------- */
/* Input Component */
/* -------------------------------------------------------------------- */
/** ui_Input：表单元素基类 */
WebUI.Component.$extends("ui_Input", "", {
    /** 构造方法 */
    "<init>" : function() {
        if (WebUI.isNaN($(this.getElement()).attr('onchange')) == true)
            this.bindEvent("change", this.onchange);
    },
    /** 标签元素的change事件处理程序。 */
    onchange : function() {
        if (WebUI.isNaN(this.getState().get("onChangeEL")) == true)
            return;
        var paramData = {};
        paramData[this.componentID + ":value"] = this.getValue();
        this.doEvent("OnChange", paramData, function(res) {
        /* TODO OnChange , OK CallBack. */
        }, function(XMLHttpRequest, textStatus) {
        /* TODO OnChange , Error CallBack. */
        });
    },
    /** 从服务器上载入数据 */
    loadData : function(paramData, funOK, funError) {
        if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
            return;
        var $this = this;
        this.doEvent("OnLoadData", paramData, function(res) {
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, res);
        }, function(XMLHttpRequest, textStatus) {
            if (WebUI.isFun(funError) == true)
                funError.call($this, XMLHttpRequest, textStatus);
        });
    },
    /** （重写方法）返回一个值用于表示是否为一个表单元素（只要定义了name属性就成为表单元素） */
    isForm : function() {
        return !WebUI.isNaN(this.getName());
    },
    /** 验证表单值内容 */
    verification : function() {
        var ver = this.getState().get("verification");
        if (WebUI.isNaN(ver) == true)
            return true;
        var verRegExp = new RegExp(ver);
        return verRegExp.test(this.getValue());
    },
    /** 获取表单值（该方法不会引发State变化） */
    getValue : function() {
        return $(this.getElement()).attr("value");
    },
    /** 设置表单值（该方法不会引发State变化） */
    setValue : function(newValue) {
        $(this.getElement()).attr("value", newValue);
    },
    /** 获取表单名（该方法不会引发State变化） */
    getName : function() {
        return $(this.getElement()).attr("name");
    },
    /** 设置表单名（该方法不会引发State变化） */
    setName : function(newName) {
        $(this.getElement()).attr("name", newName);
    }
});
/* ui_Text组建 */
WebUI.Component.$extends("ui_Text", "ui_Input", {
    /** 构造方法 */
    "<init>" : function() {
        this.bindEvent("change", this.onchange);
    },
    /** 文本组建是否为多行输入 */
    getMultiLine : function() {
        return this.getState().get("multiLine");
    }
});
/* ui_SelectOne组建 */
WebUI.Component.$extends("ui_SelectOne", "ui_Input", {
    /** 构造方法 */
    "<init>" : function() {
        this.bindEvent("change", this.onchange);
    },
    /** （重写方法）从服务器上载入数据 */
    loadData : function(paramData, funOK, funError) {
        if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
            return;
        var $this = this;
        this.doEvent("OnLoadData", paramData, function(res) {
            // A.成功装载
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, res);
            else {
                var k = $this.getKeyField();
                var v = $this.getVarField();
                var arrayData = eval(res);
                var e = $this.getElement();
                e.options.length = 0;
                for ( var i = 0; i < arrayData.length; i++)
                    e.options.add(new Option(arrayData[i][v], arrayData[i][k]));
            }
        }, function(XMLHttpRequest, textStatus) {
            // B.装载失败
            if (WebUI.isFun(funError) == true)
                funError.call($this, XMLHttpRequest, textStatus);
        });
    },
    /** （重写方法）获取选中的值 */
    getValue : function() {
        var e = this.getElement();
        return e.options[e.selectedIndex].value; // 选中值
    },
    /** 获取选中的值 */
    getSelectValue : function() {
        var e = this.getElement();
        var jqObject = e.options[e.selectedIndex]; // 选中值
        var atItem = {};
        atItem[jqObject.value] = jqObject.text;
        return atItem;
    },
    /** 数据（R） */
    getListData : function() {
        var e = this.getElement();
        var dataList = new Array();
        for ( var i = 0; i < e.options.length; i++) {
            var atItem = {};
            var jqObject = e.options[i];
            atItem[jqObject.value] = jqObject.text;
            dataList.push(atItem);
        }
        return dataList;
    },
    /** 显示名称字段（R） */
    getKeyField : function() {
        return this.getState().get("keyField");
    },
    /** 值字段（R） */
    getVarField : function() {
        return this.getState().get("varField");
    }
});
/* ui_SelectCheck组建 */
WebUI.Component.$extends("ui_SelectCheck", "ui_Input", {
    /** 构造方法 */
    "<init>" : function() {
        var fun = this.onchange;
        $("#" + this.clientID + " input[type=checkbox]").bind("change", function() {
            var comID = $(this).attr("forComID");
            var $this = WebUI(comID);
            fun.call($this);
        });
    },
    /** （重写方法）从服务器上载入数据 */
    loadData : function(paramData, funOK, funError) {
        if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
            return;
        var $this = this;
        this.doEvent("OnLoadData", paramData, function(res) {
            // A.成功装载
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, res);
            else {
                var k = $this.getKeyField();
                var v = $this.getVarField();
                var arrayData = eval(res);
                var jqObject = $($this.getElement());
                var titleFirst = $this.getState().get('titleFirst');
                var selectValues = $this.getState().get('value');
                try {
                    selectValues = selectValues.split(",");
                } catch (e) {
                    selectValues = [ selectValues ];
                }
                jqObject.html('');
                for ( var i = 0; i < arrayData.length; i++) {
                    var span = '<span>' + arrayData[i][v] + '</span>';
                    var input = '<input type="checkbox" forComID="' + $this.componentID + '" name="' + $this.getName() + '" value="' + arrayData[i][k] + '" varValue="' + arrayData[i][k] + '"';
                    for ( var j = 0; j < selectValues.length; j++)
                        if (selectValues[j] == arrayData[i][k])
                            input += " checked='checked'";
                    input += "/>";
                    if (titleFirst == true)
                        jqObject.append('<li>' + span + input + '</li>');
                    else
                        jqObject.append('<li>' + input + span + '</li>');
                }
            }
        }, function(XMLHttpRequest, textStatus) {
            // B.装载失败
            if (WebUI.isFun(funError) == true)
                funError.call($this, XMLHttpRequest, textStatus);
        });
    },
    /** （重写方法）获取选中的值 */
    getValue : function() {
        var selectData = new Array();
        $("#" + this.clientID + " input[type=checkbox]").each(function() {
            if (this.checked == true)
                selectData.push(this.value);
        });
        return selectData; // 选中值
    },
    /** 获取选中的值 */
    getSelectValue : function() {
        var dataList = new Array();
        $("#" + this.clientID + " input[type=checkbox]").each(function() {
            if (this.checked == true) {
                var atItem = {};
                var jqObject = $(this);
                atItem[jqObject.attr('value')] = jqObject.attr('varValue');
                dataList.push(atItem);
            }
        });
        return dataList; // 选中值
    },
    /** 数据（R） */
    getListData : function() {
        var dataList = new Array();
        $("#" + this.clientID + " input[type=checkbox]").each(function() {
            var atItem = {};
            var jqObject = $(this);
            atItem[jqObject.attr('value')] = jqObject.attr('varValue');
            dataList.push(atItem);
        });
        return dataList;
    },
    /** 显示名称字段（R） */
    getKeyField : function() {
        return this.getState().get("keyField");
    },
    /** 值字段（R） */
    getVarField : function() {
        return this.getState().get("varField");
    }
});
/* ui_ScriptInput组建 */
WebUI.Component.$extends("ui_ScriptInput", "ui_Input", {
    /** 构造方法 */
    "<init>" : function() {
    /* 不具备这个特性，this.bindEvent("change", this.onchange); */
    },
    /** 绑定的客户端脚本,读（R） */
    getVarRScript : function() {
        return this.getState().get("varRScript");
    },
    /** 绑定的客户端脚本,写（R） */
    getVarWScript : function() {
        return this.getState().get("varWScript");
    },
    /** （重写方法）获表达式的值 */
    getValue : function() {
        return eval(this.getVarRScript());
    },
    /** 设置表单值（不支持） */
    setValue : function(newValue) {
        var Data = newValue;
        eval(this.getVarWScript());
    },
});