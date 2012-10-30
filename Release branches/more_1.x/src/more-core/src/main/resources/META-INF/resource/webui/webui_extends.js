/** ui_AjaxButton：可以向服务器ajax方式向服务器发送OnAction事件 */
WebUI.Component.$extends("ui_AjaxButton", "UIButton", {
    /** 构造方法 */
    "<init>" : function() {
        if (WebUI.isNaN($(this.getElement()).attr('onclick')) == true)
            this.bindEvent("click", this.onclick);
    },
    /** 引发标签的click事件。 */
    click : function(params, callBackFun, errorFun) {
        this.doEvent("OnAction", params, callBackFun, errorFun);
    },
    /** 标签元素的click事件处理程序。 */
    onclick : function() {
        this.click({}, function(event) {
        /* TODO OnChange , OK CallBack. */
        }, function(event) {
        /* TODO OnChange , Error CallBack. */
        });
    }
});
/** ui_AjaxForm组建 */
WebUI.Component.$extends("ui_AjaxForm", "UIForm", {
    /** 构造方法 */
    "<init>" : function() {
        if (WebUI.isNaN($(this.getElement()).attr('onsubmit')) == true)
            this.bindEvent("submit", this.onsubmit);
    }
});
/** ui_Caption组建 */
WebUI.Component.$extends("ui_Caption", "UIOutput", {
    /** 构造方法 */
    "<init>" : function() {}
});
/** ui_Page：分页标签 */
WebUI.Component.$extends("ui_Page", "UIComponent", {
    /** 构造方法 */
    "<init>" : function() {}
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
    /** （重写方法）设置表单值（不支持） */
    setValue : function(newValue) {
        var Data = newValue;
        eval(this.getVarWScript());
    },
});
/** ui_Input：表单元素基类 */
WebUI.Component.$extends("ui_Text", "UIInput", {
    /** 构造方法 */
    "<init>" : function() {
        if (WebUI.isNaN($(this.getElement()).attr('onchange')) == true)
            this.bindEvent("change", this.onchange);
    },
    /** 文本组建是否为多行输入 */
    getMultiLine : function() {
        return this.getState().get("multiLine");
    }
});
/* ui_SelectOne组建 */
WebUI.Component.$extends("ui_SelectOne", "UIInput", {
    /** 构造方法 */
    "<init>" : function() {
        this.bindEvent("change", this.onchange);
    },
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
                var k = $this.getKeyField();
                var v = $this.getVarField();
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
/** ui_SelectCheck组建 */
WebUI.Component.$extends("ui_SelectCheck", "UIInput", {
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
        this.doEvent("OnLoadData", paramData, function(event) {
            // A.成功装载
            if (WebUI.isFun(funOK) == true)
                funOK.call($this, event);
            else {
                var k = $this.getKeyField();
                var v = $this.getVarField();
                var arrayData = eval(event.result);
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
        }, function(event) {
            // B.装载失败
            if (WebUI.isFun(funError) == true)
                funError.call($this, {
                    event : event
                });
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
/* ui_SWFUpload组建 */
WebUI.Component.$extends("ui_Upload", "UIInput", {
    /** 构造方法 */
    "<init>" : function() {
        this.initUpload();
    },
    SWFUpload : null,
    /* Event Handlers */
    initUpload : function() {
        var uploadParamStr = this.getState().get("uploadParamStr");// 上传action后面追加的参数，？后面的。
        var uploadDir = this.getState().get("uploadDir");// 上传到的目的地。
        var allowFiles = this.getState().get("allowFiles");// 默认允许的文件：任意。
        var allowFilesDesc = this.getState().get("allowFilesDesc");// 默认允许的文件类型说明。
        var allowSize = this.getState().get("allowSize");// 默认允许的大小，例：1MB。
        var buttonWidth = this.getState().get("buttonWidth");// 上传按钮宽。
        var buttonHeight = this.getState().get("buttonHeight");// 上传按钮高。
        var buttonImage = this.getState().get("buttonImage");// 按钮上的图片。
        buttonImage = (buttonImage != null) ? buttonImage : WebUI.Base + "/swfupload_v2.2.0.1/upload_65x29.png";
        var allowMulti = this.getState().get("allowMulti");// 是否允许多文件上传
        allowMulti = (allowMulti == true) ? 0 : 1;
        var buttonPlaceID = this.componentID + "PlaceID";
        //
        var sendData = {};
        sendData["WebUI_PF_Target"] = this.componentID;/* 发生事件的组建 */
        sendData["WebUI_PF_TargetPath"] = this.componentPath;/* 发生事件的组建 */
        sendData["WebUI_PF_Ajax"] = true;
        sendData["WebUI_PF_Event"] = "OnUpLoad";/* 引发的事件 */
        sendData["WebUI_PF_Render"] = "No";/* 不执行渲染 */
        sendData["WebUI_PF_State"] = WebUI.util.b64.uncoded64(this.getState().getCode());
        sendData["WebUI_PF_Invoke"] = null;
        var postData = uploadParamStr + "&" + WebUI.mapToURI(sendData);
        //
        var settings = {
            flash_url : WebUI.Base + "/swfupload_v2.2.0.1/falsh/swfupload.swf",// flash版本
            flash9_url : WebUI.Base + "/swfupload_v2.2.0.1/falsh/swfupload_fp9.swf",// 版本
            upload_url : WebUI.getLocal() + "?" + postData,// 上传地址
            file_post_name : "Filedata",// 是POST过去的$_FILES的数组名
            file_types : allowFiles,// 允许上传的文件类型，例：*.jpg;*.gif
            file_types_description : allowFilesDesc,// 文件类型描述，例：Web Image Files
            file_size_limit : allowSize,// 上传文件体积上限，单位MB
            file_upload_limit : allowMulti,// 限定用户一次性最多上传多少个文件，在上传过程中，该数字会累加，如果设置为“0”，则表示没有限制
            debug : false,// 是否显示调试信息
            //
            custom_settings : {
                componentObject : this
            },
            //
            button_placeholder_id : buttonPlaceID,
            button_width : buttonWidth,
            button_height : buttonHeight,
            button_image_url : buttonImage,
            // button_text : '<span class="theFont">上传</span>',
            // button_text_style : ".theFont { font-size: 16; }",
            // button_text_left_padding : 12,
            // button_text_top_padding : 3,
            //
            file_queue_error_handler : fileQueueError,
            swfupload_preload_handler : preLoad,
            swfupload_load_failed_handler : loadFailed,
            file_queued_handler : fileQueued,
            file_dialog_complete_handler : fileDialogComplete,
            upload_start_handler : uploadStart,
            upload_progress_handler : uploadProgress,
            upload_success_handler : uploadSuccess,
            upload_complete_handler : uploadComplete,
        };
        this.SWFUpload = new SWFUpload(settings);
    },
    /** 取消上传 */
    cancelQueue : function() {
        this.SWFObject.cancelQueue();
    }
});
