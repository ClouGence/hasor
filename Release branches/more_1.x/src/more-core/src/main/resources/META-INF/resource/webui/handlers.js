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
function preLoad() {
    if (!this.support.loading) {
        alert("You need the Flash Player 9.028 or above to use SWFUpload.");
        return false;
    }
}
function loadFailed() {
    alert("Something went wrong while loading SWFUpload. If this were a real application we'd clean up and then give you an alternative");
}
function fileQueued(file) {
    try {
        // this.customSettings.tdFilesQueued.innerHTML = this.getStats().files_queued;
    } catch (ex) {
        this.debug(ex);
    }
}
function fileDialogComplete() {
    this.startUpload();
}
function uploadStart(file) {
    try {
        this.customSettings.progressCount = 0;
        updateDisplay.call(this, file);
    } catch (ex) {
        this.debug(ex);
    }
}
function uploadProgress(file, bytesLoaded, bytesTotal) {
    try {
        this.customSettings.progressCount++;
        updateDisplay.call(this, file);
    } catch (ex) {
        this.debug(ex);
    }
}
function uploadSuccess(file, serverData) {
    try {
        eval("var data=" + serverData + ";");
        updateDisplay.call(this, file);
        this.customSettings.tdUpload(data);
    } catch (ex) {
        this.debug(ex);
    }
}
function uploadComplete(file) {
// this.customSettings.tdFilesQueued.innerHTML = this.getStats().files_queued;
// this.customSettings.tdFilesUploaded.innerHTML = this.getStats().successful_uploads;
// this.customSettings.tdErrors.innerHTML = this.getStats().upload_errors;
}
function updateDisplay(file) {
    this.customSettings.tdCurrentSpeed.innerHTML = "<font size=3>&nbsp;&nbsp;&nbsp;平均上传速度:&nbsp;" + SWFUpload.speed.formatBPS(file.averageSpeed) + "/s</font>";
}
function fileQueueError(file, errorCode, message) {
    try {
        // Handle this error separately because we don't want to create a FileProgress element for it.
        switch (errorCode) {
            case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
                alert("You have attempted to queue too many files.\n" + (message === 0 ? "You have reached the upload limit." : "You may select " + (message > 1 ? "up to " + message + " files." : "one file.")));
                return;
            case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
                alert("上传的文件过大.");
                this.debug("Error Code: File too big, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
                return;
            case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
                alert("The file you selected is empty.  Please select another file.");
                this.debug("Error Code: Zero byte file, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
                return;
            case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
                alert("The file you choose is not an allowed file type.");
                this.debug("Error Code: Invalid File Type, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
                return;
            default:
                alert("An error occurred in the upload. Try again later.");
                this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
                return;
        }
    } catch (e) {}
}
