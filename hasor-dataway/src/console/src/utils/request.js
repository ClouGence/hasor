import Vue from 'vue';
import axios from 'axios';

function decodeUtf8(bytes) {
    const bufferTypes = new Uint8Array(bytes);
    let tempString = '';
    for (let i = 0; i < bufferTypes.length; ++i) {
        let hexDat = bufferTypes[i].toString(16);
        if (hexDat.length === 1) {
            hexDat = '0' + hexDat;
        }
        tempString += '%' + hexDat;
    }
    return decodeURIComponent(tempString);
}

function arrayBufferFromBlob(bytesBlob) {
    return new Promise(function (resolve, reject) {
        const reader = new FileReader()
        reader.readAsArrayBuffer(bytesBlob)
        reader.onload = function () {
            resolve(this.result)
        }
    });
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [option] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(apiURL, options, successCallback, errorCallback) {
    const defaultOptions = {
        loading: true,
        direct: false,
        credentials: 'include'
    };
    const newOptions = {
        ...defaultOptions,
        ...options,
        url: apiURL
    };
    newOptions.headers = {
        'Accept': 'application/json',
        ...newOptions.headers,
    };
    //
    if (newOptions.method === 'GET') {
        newOptions.params = newOptions.data;
    }
    if (newOptions.method === 'POST' || newOptions.method === 'PUT') {
        if (!(newOptions.data instanceof FormData)) {
            newOptions.headers = {
                'Content-Type': 'application/json; charset=utf-8',
                ...newOptions.headers,
            };
            newOptions.data = JSON.stringify(newOptions.data);
        }
    }
    //
    errorCallback = (errorCallback === null || errorCallback === undefined) ? () => {
    } : errorCallback;
    successCallback = (successCallback === null || successCallback === undefined) ? () => {
    } : successCallback;
    //
    let finallyCallback = () => {
        /**/
    };
    if (newOptions.loading) {
        const loading = Vue.prototype.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.5)'
        });
        finallyCallback = () => {
            loading.close();
        };
    }
    return axios.request({
        ...newOptions,
        withCredentials: true,
        responseType: 'blob'
    }).then(async (response) => {
        let contentType = '';
        for (const key in response.headers) {
            if (key.toLowerCase() === 'x-interfaceui-contexttype') {
                contentType = response.headers[key];
                contentType = contentType.toLowerCase();
                break;
            }
        }
        if (contentType === undefined || contentType == null || contentType === '') {
            if (response.data.type === 'application/json') {
                contentType = 'json';
            }
        }
        //
        if (contentType === 'json') {
            // json
            arrayBufferFromBlob(response.data).then(arrayBuffer => {
                response.dataTypeMode = 'json';
                response.data = JSON.parse(decodeUtf8(arrayBuffer));
                successCallback(response);
            });
        } else if (contentType === 'text') {
            // text
            arrayBufferFromBlob(response.data).then(arrayBuffer => {
                response.dataTypeMode = 'text';
                response.data = decodeUtf8(arrayBuffer);
                successCallback(response);
            });
        } else {
            // bytes
            arrayBufferFromBlob(response.data).then(arrayBuffer => {
                const bufferTypes = new Uint8Array(arrayBuffer);
                let tempString = '';
                let n = 0;
                for (let i = 0; i < bufferTypes.length; ++i) {
                    let hexDat = bufferTypes[i].toString(16).toUpperCase();
                    if (hexDat.length === 1) {
                        hexDat = '0' + hexDat;
                    }
                    if (n < 15) {
                        n++;
                        tempString = tempString + hexDat + ' ';
                    } else {
                        n = 0;
                        tempString = tempString + hexDat + '\n';
                    }
                }
                response.dataTypeMode = 'bytes';
                response.data = tempString.trim();
                successCallback(response);
            });
        }
    }).catch(errorCallback).finally(finallyCallback);
}
