/** ui_AjaxButton：可以向服务器ajax方式向服务器发送OnAction事件 */
WebUI.Component.$extends("ui_AjaxButton", "UIButton", {
    /** 构造方法 */
    "<init>" : function() {
        this.defineProperty("useLink", "RW");
    }
});
/** ui_AjaxForm组建 */
WebUI.Component.$extends("ui_AjaxForm", "UIForm", {});
/** ui_Caption组建 */
WebUI.Component.$extends("ui_Caption", "UIOutput", {});
/** ui_Page：分页标签 */
WebUI.Component.$extends("ui_Page", "UIComponent", {
// this.goPage = function(targetObj, goIndex) {
// if (typeof (targetObj) == 'string')
// targetObj = $("[comid='" + targetObj + "']");
// //
// var stateMap = WebUI.Core.Component.getState(targetObj);
// var clickFun=eval(stateMap.clickFun);
// if (typeof(clickFun)=='function')
// clickFun.call(_this, targetObj, goIndex);
// };
// /**计算在该分页组建上指定页码的起始记录号（只有被实例化之后的组建才可以被调用）*/
// this.evalRowNum = function(pageNum) {
// return this.state.pageSize * pageNum + this.state.startWith;
// };
});
/* ui_ScriptInput组建 */
WebUI.Component.$extends("ui_ScriptInput", "UIInput", {
    /** 构造方法 */
    "<init>" : function() {
        /** 绑定的客户端脚本,读（R） */
        this.defineProperty("varRScript", "RW");
        /** 绑定的客户端脚本,写（R） */
        this.defineProperty("varWScript", "RW");
        /** value */
        this.defineProperty("value", function() {
            return eval(this.varRScript());
        }, function(newValue) {
            this.getState().set("value", newValue);
            var Data = newValue;
            eval(this.varWScript());
        });
    }
});
/** ui_SelectCheck组建 */
WebUI.Component.$extends("ui_SelectCheck", "UISelectInput", {
    /** （重写方法）从服务器上载入数据 */
    loadData : function(paramData, funOK, funError) {
        if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
            return;
        var $this = this;
        this.doEvent("OnLoadData", paramData, function(event) {
            // A.成功装载
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, event);
            else {
                var k = $this.keyField();
                var v = $this.varField();
                var arrayData = eval(event.result);
                var jqObject = $($this.getElement());
                var titleFirst = $this.titleFirst();
                var selectValues = $this.getState().get('value');
                try {
                    selectValues = selectValues.split(",");
                } catch (e) {
                    selectValues = [ selectValues ];
                }
                jqObject.html('');
                for ( var i = 0; i < arrayData.length; i++) {
                    var span = '<span>' + arrayData[i][v] + '</span>';
                    var input = '<input type="checkbox" forComID="' + $this.componentID + '" name="' + $this.name() + '" value="' + arrayData[i][k] + '" varValue="' + arrayData[i][k] + '"';
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
        }, function(event) {
            // B.装载失败
            if (WebUI.isFun(funError) == true)
                funError.call($this, {
                    event : event
                });
        });
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
    /** 构造方法 */
    "<init>" : function() {
        /** 绑定事件 */
        var fun = this.onchange;
        $("#" + this.clientID + " input[type=checkbox]").bind("change", function() {
            var comID = $(this).attr("forComID");
            var $this = WebUI(comID);
            fun.call($this);
        });
        /** titleFirst */
        this.defineProperty("titleFirst", "RW");
        /** value */
        this.defineProperty("value", function() {
            var selectData = new Array();
            $("#" + this.clientID + " input[type=checkbox]").each(function() {
                if (this.checked == true)
                    selectData.push(this.value);
            });
            return selectData; // 选中值
        }, function(newValue) {
            var selectData = "";
            if (WebUI.isArray(newValue) == false)
                newValue = [ newValue ];
            $("#" + this.clientID + " input[type=checkbox]").each(function() {
                this.checked = false;
                for ( var v in newValue)
                    if (v == this.value) {
                        this.checked = true;
                        selectData = selectData + "," + this.value;
                    }
            });
            if (selectData.length > 0)
                selectData = selectData.substr(1);
            this.getState().set("value", selectData);
        });
    }
});
/* ui_SelectOne组建 */
WebUI.Component.$extends("ui_SelectOne", "UISelectInput", {
    /** （重写方法）从服务器上载入数据 */
    loadData : function(paramData, funOK, funError) {
        if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
            return;
        var $this = this;
        this.doEvent("OnLoadData", paramData, function(event) {
            // A.成功装载
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, {
                    event : event
                });
            else {
                var k = $this.keyField();
                var v = $this.varField();
                var arrayData = eval(event.result);
                var e = $this.getElement();
                e.options.length = 0;
                for ( var i = 0; i < arrayData.length; i++)
                    e.options.add(new Option(arrayData[i][v], arrayData[i][k]));
            }
        }, function(event) {
            // B.装载失败
            if (WebUI.isFun(funError) == true)
                funError.call($this, {
                    event : event
                });
        });
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
    /** 构造方法 */
    "<init>" : function() {
        /** value */
        this.defineProperty("value", function() {
            var e = this.getElement();
            return e.options[e.selectedIndex].value; // 选中值
        }, function(newValue) {
            var e = this.getElement();
            this.getState().set("value", newValue);
            e.options[e.selectedIndex].value = newValue; // 选中值
        });
    },
});
/** ui_Input：表单元素基类 */
WebUI.Component.$extends("ui_Text", "UIInput", {
    /** 构造方法 */
    "<init>" : function() {
        /** 文本组建是否为多行输入 */
        this.defineProperty("multiLine", "RW");
    }
});