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

export {
    tagInfo, errorBox, checkRequestBody, headerData
};