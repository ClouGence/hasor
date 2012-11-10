/* -------------------------------------------------------------------- */
/* UIUploadInput Component */
/* -------------------------------------------------------------------- */
function webui_swfupload_load_failed_handler() {}
/** 发生错误版本。 */
function webui_swfupload_preload_handler() {}
/** 当某个文件被加入到上传队列 */
function webui_file_queued_handler(file) {}
/** 上传队列中发生错误 */
function webui_file_queue_error_handler(file, errorCode, message) {}
/** 上传对话框关闭之后。 */
function webui_file_dialog_complete_handler(numFilesSelected, numFilesQueued) {}
/** 当某个文件开始上传 */
function webui_upload_start_handler(file) {}
/** 文件上传过程中调用，用于更新进度。 */
function webui_upload_progress_handler(file, bytesLoaded, bytesTotal) {}
/** 上传组建级的错误。 */
function webui_upload_error_handler(file, errorCode, message) {}
/** 当队列中正在上传的文件完成上传之后调用。 */
function webui_upload_success_handler(file, serverData) {}
/** 每个文件上传完毕都会调用一遍 */
function webui_upload_complete_handler(file) {}
/** 当队列上传完毕之后，返回值决定组建在表单中的值。 */
function webui_queue_complete_handler(numFilesUploaded, allServerData) {
	return allServerData;
}
/** **************************** */
/** **************************** */
/** **************************** */
/** **************************** */
/** **************************** */
/** **************************** */
function WebUI_FileProgress(file, targetID) {
	this.fileProgressID = file.id;
	this.opacity = 100;
	this.height = 0;
	this.fileProgressWrapper = document.getElementById(this.fileProgressID);
	if (!this.fileProgressWrapper) {
		this.fileProgressWrapper = document.createElement("div");
		this.fileProgressWrapper.className = "webui_progressWrapper";
		this.fileProgressWrapper.id = this.fileProgressID;
		this.fileProgressElement = document.createElement("div");
		this.fileProgressElement.className = "webui_progressContainer";
		var progressCancel = document.createElement("a");
		progressCancel.className = "webui_progressCancel";
		progressCancel.href = "#";
		progressCancel.style.visibility = "hidden";
		progressCancel.appendChild(document.createTextNode(" "));
		var progressText = document.createElement("div");
		progressText.className = "webui_progressName";
		progressText.appendChild(document.createTextNode(file.name));
		var progressBar = document.createElement("div");
		progressBar.className = "webui_progressBarInProgress";
		var progressStatus = document.createElement("div");
		progressStatus.className = "webui_progressBarStatus";
		progressStatus.innerHTML = "&nbsp;";
		this.fileProgressElement.appendChild(progressCancel);
		this.fileProgressElement.appendChild(progressText);
		this.fileProgressElement.appendChild(progressStatus);
		this.fileProgressElement.appendChild(progressBar);
		this.fileProgressWrapper.appendChild(this.fileProgressElement);
		document.getElementById(targetID).appendChild(this.fileProgressWrapper);
	} else {
		this.fileProgressElement = this.fileProgressWrapper.firstChild;
		this.reset();
	}
	this.height = this.fileProgressWrapper.offsetHeight;
	this.setTimer(null);
}
WebUI_FileProgress.prototype.setTimer = function(timer) {
	this.fileProgressElement["FP_TIMER"] = timer;
};
WebUI_FileProgress.prototype.getTimer = function(timer) {
	return this.fileProgressElement["FP_TIMER"] || null;
};
WebUI_FileProgress.prototype.reset = function() {
	this.fileProgressElement.className = "webui_progressContainer";
	this.fileProgressElement.childNodes[2].innerHTML = "&nbsp;";
	this.fileProgressElement.childNodes[2].className = "webui_progressBarStatus";
	this.fileProgressElement.childNodes[3].className = "webui_progressBarInProgress";
	this.fileProgressElement.childNodes[3].style.width = "0%";
	this.appear();
};
WebUI_FileProgress.prototype.setProgress = function(percentage) {
	this.fileProgressElement.className = "webui_progressContainer webui_progress_color_green";
	this.fileProgressElement.childNodes[3].className = "webui_progressBarInProgress";
	this.fileProgressElement.childNodes[3].style.width = percentage + "%";
	this.appear();
};
WebUI_FileProgress.prototype.setComplete = function() {
	this.fileProgressElement.className = "webui_progressContainer webui_progress_color_blue";
	this.fileProgressElement.childNodes[3].className = "webui_progressBarComplete";
	this.fileProgressElement.childNodes[3].style.width = "";
	var oSelf = this;
	this.setTimer(setTimeout(function() {
		oSelf.disappear();
	}, 10000));
};
WebUI_FileProgress.prototype.setError = function() {
	this.fileProgressElement.className = "webui_progressContainer webui_progress_color_red";
	this.fileProgressElement.childNodes[3].className = "webui_progressBarError";
	this.fileProgressElement.childNodes[3].style.width = "";
	var oSelf = this;
	this.setTimer(setTimeout(function() {
		oSelf.disappear();
	}, 5000));
};
WebUI_FileProgress.prototype.setCancelled = function() {
	this.fileProgressElement.className = "webui_progressContainer";
	this.fileProgressElement.childNodes[3].className = "webui_progressBarError";
	this.fileProgressElement.childNodes[3].style.width = "";
	var oSelf = this;
	this.setTimer(setTimeout(function() {
		oSelf.disappear();
	}, 2000));
};
WebUI_FileProgress.prototype.setStatus = function(status) {
	this.fileProgressElement.childNodes[2].innerHTML = status;
};
// Show/Hide the cancel button
WebUI_FileProgress.prototype.toggleCancel = function(show, swfUploadInstance) {
	this.fileProgressElement.childNodes[0].style.visibility = show ? "visible" : "hidden";
	if (swfUploadInstance) {
		var fileID = this.fileProgressID;
		this.fileProgressElement.childNodes[0].onclick = function() {
			swfUploadInstance.cancelUpload(fileID);
			return false;
		};
	}
};
WebUI_FileProgress.prototype.appear = function() {
	if (this.getTimer() !== null) {
		clearTimeout(this.getTimer());
		this.setTimer(null);
	}
	if (this.fileProgressWrapper.filters) {
		try {
			this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = 100;
		} catch (e) {
			// If it is not set initially, the browser will throw an error.
			// This will set it if it is not set yet.
			this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=100)";
		}
	} else {
		this.fileProgressWrapper.style.opacity = 1;
	}
	this.fileProgressWrapper.style.height = "";
	this.height = this.fileProgressWrapper.offsetHeight;
	this.opacity = 100;
	this.fileProgressWrapper.style.display = "";
};
// Fades out and clips away the FileProgress box.
WebUI_FileProgress.prototype.disappear = function() {
	var reduceOpacityBy = 15;
	var reduceHeightBy = 4;
	var rate = 30; // 15 fps
	if (this.opacity > 0) {
		this.opacity -= reduceOpacityBy;
		if (this.opacity < 0) {
			this.opacity = 0;
		}
		if (this.fileProgressWrapper.filters) {
			try {
				this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = this.opacity;
			} catch (e) {
				// If it is not set initially, the browser will throw an
				// error. This will set it if it is not set yet.
				this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + this.opacity + ")";
			}
		} else {
			this.fileProgressWrapper.style.opacity = this.opacity / 100;
		}
	}
	if (this.height > 0) {
		this.height -= reduceHeightBy;
		if (this.height < 0) {
			this.height = 0;
		}
		this.fileProgressWrapper.style.height = this.height + "px";
	}
	if (this.height > 0 || this.opacity > 0) {
		var oSelf = this;
		this.setTimer(setTimeout(function() {
			oSelf.disappear();
		}, rate));
	} else {
		this.fileProgressWrapper.style.display = "none";
		this.setTimer(null);
	}
};
/* UIUploadInput组建 */
WebUI.Component.$extends("UIUploadInput", "UIInput", {
	SWFUpload : null,
	/* Event Handlers Define */
	handlers : {
		swfupload_load_failed_handler : function() {},
		swfupload_preload_handler : function() {},
		//
		file_queued_handler : function() {},
		file_queue_error_handler : function() {},
		file_dialog_complete_handler : function() {},
		upload_start_handler : function() {},
		upload_progress_handler : function() {},
		upload_error_handler : function() {},
		upload_success_handler : function() {},
		upload_complete_handler : function() {},
		queue_complete_handler : function() {}// Queue plugin event
	},
	initUpload : function() {
		var uploadAction = this.uploadAction();
		uploadAction = (uploadAction != null) ? uploadAction : WebUI.getLocal();
		uploadAction += (uploadAction.indexOf("?") <= 0) ? "?" : "&";
		var buttonImage = this.buttonImage();// 按钮上的图片。
		buttonImage = (buttonImage != null) ? buttonImage : WebUI.variable("WebUI_Var_Library") + "images/upload_65x29.png";
		//
		var sendData = {};
		var varDataMap = this.getVariableMap().getDataMap();
		for ( var k in varDataMap) {
			var v = varDataMap[k];
			if (WebUI.isFun(v) == true)
				sendData[k] = v(this);
			else
				sendData[k] = v;
		}
		sendData["WebUI_PF_Target"] = this.componentID;/* 发生事件的组建 */
		sendData["WebUI_PF_TargetPath"] = this.componentPath;/* 发生事件的组建 */
		sendData["WebUI_PF_Ajax"] = true;
		sendData["WebUI_PF_Event"] = "OnUpLoad";/* 引发的事件 */
		sendData["WebUI_PF_Render"] = "No";/* 不执行渲染 */
		sendData["WebUI_PF_State"] = WebUI.Base64.uncoded64(this.getState().getCode());
		sendData["WebUI_PF_Invoke"] = null;
		var postData = WebUI.mapToURI(sendData);
		//
		var settings = {
			flash_url : WebUI.variable("WebUI_Var_Library") + "dependence/swfupload_v2.2.0.1/falsh/swfupload.swf",// flash版本
			flash9_url : WebUI.variable("WebUI_Var_Library") + "dependence/swfupload_v2.2.0.1/falsh/swfupload_fp9.swf",// 版本
			upload_url : uploadAction + postData,// 上传地址
			file_post_name : "Filedata",// 是POST过去的$_FILES的数组名
			file_types : this.allowFiles(),// 允许上传的文件类型，例：*.jpg;*.gif
			file_types_description : this.allowFilesDesc(),// 文件类型描述，例：Web
			// Image Files
			file_size_limit : this.allowSize(),// 上传文件体积上限，单位MB,例：1MB。
			debug : false,// 是否显示调试信息
			//
			custom_settings : {
				componentObject : this
			},
			//
			button_placeholder_id : this.componentID + "PlaceID",
			button_action : (this.allowMulti() == true) ? SWFUpload.BUTTON_ACTION.SELECT_FILES : SWFUpload.BUTTON_ACTION.SELECT_FILE,// 是否允许多文件上传
			button_width : this.buttonWidth(),
			button_height : this.buttonHeight(),
			button_image_url : buttonImage,
			//
			swfupload_load_failed_handler : this.handlers.swfupload_load_failed_handler,
			swfupload_preload_handler : this.handlers.swfupload_preload_handler,
			file_queued_handler : this.handlers.file_queued_handler,
			file_queue_error_handler : this.handlers.file_queue_error_handler,
			file_dialog_complete_handler : this.handlers.file_dialog_complete_handler,
			upload_start_handler : this.handlers.upload_start_handler,
			upload_progress_handler : this.handlers.upload_progress_handler,
			upload_error_handler : this.handlers.upload_error_handler,
			upload_success_handler : this.handlers.upload_success_handler,
			upload_complete_handler : this.handlers.upload_complete_handler,
			/** Queue plugin event */
			queue_complete_handler : this.handlers.queue_complete_handler
		};
		this.SWFUpload = new SWFUpload(settings);
		this.showProgress(this.showProgress());// 自己给自己设置一次就完成了设置。
		//
	},
	/** 取消上传 */
	cancelQueue : function() {
		this.SWFObject.cancelQueue();
	},
	/** 开始上传 */
	startUpload : function() {
		this.SWFUpload.startUpload();
	},
	/** 构造方法 */
	"<init>" : function() {
		/** 接收上传的action地址，配置该属性会覆盖内置的上传通路，默认的递交位置是当前页（高级属性）（R） */
		this.defineProperty("uploadAction", "R");
		/** 默认允许的文件：任意（RW） */
		this.defineProperty("allowFiles", "RW");
		/** 默认允许的文件类型说明（RW） */
		this.defineProperty("allowFilesDesc", "RW");
		/** 默认允许的大小，例：1MB（RW） */
		this.defineProperty("allowSize", "RW");
		/** 上传按钮宽（RW） */
		this.defineProperty("buttonWidth", "RW");
		/** 上传按钮高（RW） */
		this.defineProperty("buttonHeight", "RW");
		/** 按钮上的图片（RW） */
		this.defineProperty("buttonImage", "RW");
		/** 是否允许多文件上传，默认不允许（RW） */
		this.defineProperty("allowMulti", "RW");
		/** 是否显示进度条（RW） */
		this.defineProperty("showProgress", function() {
			return this.getState().get("showProgress");
		}, function(newValue) {
			if (newValue == true)
				$("#" + this.progressTargetID()).show();
			else
				$("#" + this.progressTargetID()).hide();
			this.getState().set("showProgress", newValue);
		});
		/** 覆盖value属性 */
		this.defineProperty("value", function() {
			return $("#" + this.componentID + "Input").attr("value");
		}, function(newValue) {
			$("#" + this.componentID + "Input").attr("value", newValue);
			if (WebUI.isNaN(this.onchange) == false)
				this.onchange.call(this, newValue);// 事件没法帮上，在加上所有设置值都是通过该方法进行，因此在此处触发事件。
		});
		/** 表单名（RW） */
		this.defineProperty("name", function() {
			return $("#" + this.componentID + "Input").attr("name");
		}, function(newValue) {
			$("#" + this.componentID + "Input").attr("name", newValue);
		});
		/** 获取progressTargetID（R） */
		this.defineProperty("progressTargetID", function() {
			return this.componentID + "Progress";
		}, null);
		//
		//
		this.initUpload();
	}
});
WebUI.Component.UIUploadInput.handlers.swfupload_load_failed_handler = function() {
	// 1.通知用户程序
	if (WebUI.isNaN(webui_swfupload_load_failed_handler) == false)
		webui_swfupload_load_failed_handler();
};
/** 发生错误版本。 */
WebUI.Component.UIUploadInput.handlers.swfupload_preload_handler = function() {
	// 1.通知用户程序
	if (WebUI.isNaN(webui_swfupload_preload_handler) == false)
		webui_swfupload_preload_handler();
	else
		alert("您的Flash版本过低，SWfUpload 需要您更新 Flash Player 到 9.028 或者更高版本.");
};
/** 当某个文件被加入到上传队列 */
WebUI.Component.UIUploadInput.handlers.file_queued_handler = function(file) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setStatus("等待响应...");
		progress.toggleCancel(true, this);
	}
	// 2.通知用户程序
	if (WebUI.isNaN(webui_file_queued_handler) == false)
		webui_file_queued_handler(file);
};
/** 上传队列中发生错误 */
WebUI.Component.UIUploadInput.handlers.file_queue_error_handler = function(file, errorCode, message) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setError();
		progress.toggleCancel(false);
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			progress.setStatus("File is too big.");
			this.debug("Error Code: File too big, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			progress.setStatus("Cannot upload Zero Byte files.");
			this.debug("Error Code: Zero byte file, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			progress.setStatus("Invalid File Type.");
			this.debug("Error Code: Invalid File Type, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		default:
			if (file !== null) {
				progress.setStatus("Unhandled Error");
			}
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	}
	// 2.错误信息显示用户函数调用
	if (errorCode === SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED) {
		alert("You have attempted to queue too many files.\n" + (message === 0 ? "You have reached the upload limit." : "You may select " + (message > 1 ? "up to " + message + " files." : "one file.")));
		return;
	}
	// 3.通知用户程序
	if (WebUI.isNaN(webui_file_queue_error_handler) == false)
		webui_file_queue_error_handler(file, errorCode, message);
};
/** 上传对话框关闭之后。 */
WebUI.Component.UIUploadInput.handlers.file_dialog_complete_handler = function(numFilesSelected, numFilesQueued) {
	// 1.启动上传
	this.startUpload();// 对话框关闭之后就开始上传 TODO
	// 2.通知用户程序
	if (WebUI.isNaN(webui_file_dialog_complete_handler) == false)
		webui_file_dialog_complete_handler();
};
/** 当某个文件开始上传 */
WebUI.Component.UIUploadInput.handlers.upload_start_handler = function(file) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setStatus("上传中...");
		progress.toggleCancel(true, this);
	}
	// 2.通知用户程序
	if (WebUI.isNaN(webui_upload_start_handler) == false)
		webui_upload_start_handler(file);
};
/** 文件上传过程中调用，用于更新进度。 */
WebUI.Component.UIUploadInput.handlers.upload_progress_handler = function(file, bytesLoaded, bytesTotal) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setProgress(percent);
		progress.setStatus("上传中...");
	}
	// 2.通知用户程序
	if (WebUI.isNaN(webui_upload_progress_handler) == false)
		webui_upload_progress_handler(file, bytesLoaded, bytesTotal);
};
/** 上传组建级的错误。 */
WebUI.Component.UIUploadInput.handlers.upload_error_handler = function(file, errorCode, message) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setError();
		progress.toggleCancel(false);
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			progress.setStatus("Upload Error: " + message);
			this.debug("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			progress.setStatus("Upload Failed.");
			this.debug("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			progress.setStatus("Server (IO) Error");
			this.debug("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			progress.setStatus("Security Error");
			this.debug("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			progress.setStatus("Upload limit exceeded.");
			this.debug("Error Code: Upload Limit Exceeded, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			progress.setStatus("Failed Validation.  Upload skipped.");
			this.debug("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			// If there aren't any files left (they were all cancelled)
			// disable the cancel button
			if (this.getStats().files_queued === 0) {
				document.getElementById(this.customSettings.cancelButtonId).disabled = true;
			}
			progress.setStatus("Cancelled");
			progress.setCancelled();
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			progress.setStatus("Stopped");
			break;
		default:
			progress.setStatus("Unhandled Error: " + errorCode);
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	}
	// 2.通知用户程序
	if (WebUI.isNaN(webui_upload_error_handler) == false)
		webui_upload_error_handler(file, errorCode, message);
}
/** 当队列中正在上传的文件完成上传之后调用。 */
WebUI.Component.UIUploadInput.handlers.upload_success_handler = function(file, serverData) {
	// 1.进度条处理
	if (this.customSettings.componentObject.showProgress() == true) {
		var progress = new WebUI_FileProgress(file, this.customSettings.componentObject.progressTargetID());
		progress.setComplete();
		progress.setStatus("完成.");
		progress.toggleCancel(false);
	}
	// 2.记录服务器返回的数据
	if (WebUI.isNaN(this.customSettings.serverDataList))
		this.customSettings.serverDataList = [];
	this.customSettings.serverDataList.push(serverData);
	// 3.通知用户程序
	if (WebUI.isNaN(webui_upload_success_handler) == false)
		webui_upload_success_handler(file, serverData);
};
/** 每个文件上传完毕都会调用一遍 */
WebUI.Component.UIUploadInput.handlers.upload_complete_handler = function(file) {
	// 1.通知用户程序
	if (WebUI.isNaN(webui_upload_complete_handler) == false)
		webui_upload_complete_handler(file);
};
/** 当队列上传完毕之后 */
WebUI.Component.UIUploadInput.handlers.queue_complete_handler = function(numFilesUploaded) {
	// 1.获取到所有服务器返回的值
	var allServerData = this.customSettings.serverDataList;
	this.customSettings.serverDataList = null;// 清理掉属性
	// 2.通知用户程序
	if (WebUI.isNaN(webui_queue_complete_handler) == false)
		allServerData = webui_queue_complete_handler(numFilesUploaded, allServerData);// 最终的值由用户决定
	else
		allServerData = JSON.stringify(allServerData);
	// 3.设置值到组建上(JSON.stringify(allServerData))
	this.customSettings.componentObject.value(allServerData);
};