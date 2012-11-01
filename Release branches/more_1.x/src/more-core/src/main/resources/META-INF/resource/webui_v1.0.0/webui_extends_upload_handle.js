/* Demo Note:  This demo uses a FileProgress class that handles the UI for displaying the file name and percent complete.
The FileProgress class is not part of SWFUpload.
 */
/* **********************
 Event Handlers
 These are my custom event handlers to make my
 web application behave the way I went when SWFUpload
 completes different tasks.  These aren't part of the SWFUpload
 package.  They are part of my application.  Without these none
 of the actions SWFUpload makes will show up in my application.
 ********************** */
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