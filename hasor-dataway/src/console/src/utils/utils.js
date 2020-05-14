import Vue from 'vue';


/***/
const tagInfo = (status) => {
    if (status === 0) {
        return {'css': 'info', 'title': 'Editor'};
    }
    if (status === 1) {
        return {'css': 'success', 'title': 'Published'};
    }
    if (status === 2) {
        return {'css': 'warning', 'title': 'Changes'};
    }
    if (status === 3) {
        return {'css': 'danger', 'title': 'Disable'};
    }
    return {'css': '', 'title': ''};
};

const errorBox = (content) => {
    Vue.prototype.$alert(content, 'Error', {confirmButtonText: 'OK'});
};

const checkRequestBody = (httpMethod, codeType, requestBody) => {
    let doRunParam = {};
    try {
        doRunParam = JSON.parse(requestBody);
    } catch (e) {
        errorBox('Parameters Format Error : ' + e);
        return false;
    }
    if (httpMethod === 'GET' || codeType === 'SQL') {
        if (Object.prototype.toString.call(doRunParam) !== '[object Object]') {
            errorBox('In GET or SQL , The request parameters must be Map.');
            return false;
        }
        for (let key in doRunParam) {
            let typeStr = Object.prototype.toString.call(doRunParam[key]);
            if (typeStr === '[object Object]' || typeStr === '[object Array]') {
                errorBox('In GET or SQL , can\'t have complex structure parameters.');
                return false;
            }
        }
    }
    return true;
};

const headerData = (oriData) => {
    let requestHeaderData = {};
    for (let i = 0; i < oriData.length; i++) {
        if (oriData[i].checked && oriData[i].name !== '') {
            requestHeaderData[oriData[i].name] = encodeURIComponent(oriData[i].value);
        }
    }
    return requestHeaderData;
};

const formatDate = (date, fmt = 'yyyyMMdd-hhmmss.S') => {
    if (typeof (date) === 'number') {
        date = new Date(date)
    }
    let o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "h+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    }
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length))
    for (let k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)))
        }
    }
    return fmt
};

export {
    tagInfo, errorBox, checkRequestBody, headerData, formatDate
};