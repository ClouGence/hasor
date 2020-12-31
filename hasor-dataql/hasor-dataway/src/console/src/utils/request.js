import Vue from 'vue';
import axios from 'axios';
import {errorBox} from './utils';

const codeMessage = {
    200: 'ok.',
    401: 'no permission.',
    404: 'not found.',
};

const showMessage = (res) => {
    const response = res.response;
    const errorText = codeMessage[response.status] || response.statusText;
    let url = response.config.url.replace('//', '_');
    url = url.substr(url.indexOf('/'));
    errorBox(`${response.status}: ${errorText} (${url})`);
};

function decodeUtf8(bytes, encode) {
    const bufferTypes = new Uint8Array(bytes);
    if (encode !== '') {
        return new TextDecoder(encode).decode(bufferTypes);
    } else {
        return new TextDecoder().decode(bufferTypes);
    }
}

function arrayBufferFromBlob(bytesBlob) {
    return new Promise(function (resolve, reject) {
        const reader = new FileReader();
        reader.readAsArrayBuffer(bytesBlob);
        reader.onload = function () {
            resolve(this.result);
        };
    });
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [option] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(
    apiURL,
    options,
    successCallback,
    errorCallback
) {
    const defaultOptions = {
        loading: true,
        direct: false,
        credentials: 'include',
    };
    const newOptions = {
        ...defaultOptions,
        ...options,
        url: apiURL,
    };
    newOptions.headers = {
        Accept: 'application/json',
        ...newOptions.headers,
    };
    //
    if (newOptions.method === 'GET') {
        newOptions.params = newOptions.data;
    }
    if (newOptions.method === 'POST' || newOptions.method === 'PUT') {
        if (!(newOptions.data instanceof FormData)) {
            newOptions.headers = {
                'Content-Type': 'application/json;',
                ...newOptions.headers,
            };
            newOptions.data = JSON.stringify(newOptions.data);
        }
    }
    //
    errorCallback =
        errorCallback === null || errorCallback === undefined
            ? (errorMessage) => {
                showMessage(errorMessage);
            }
            : errorCallback;
    successCallback =
        successCallback === null || successCallback === undefined
            ? () => {
            }
            : successCallback;
    //
    let finallyCallback = () => {
        /**/
    };
    if (newOptions.loading) {
        const loading = Vue.prototype.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.5)',
        });
        finallyCallback = () => {
            loading.close();
        };
    }
    return axios.request({
        ...newOptions,
        withCredentials: true,
        responseType: 'blob',
    }).then(async (response) => {
        let contentType = '';
        let contentEncode = '';
        for (const key in response.headers) {
            const keyLC = key.toLowerCase();
            if (keyLC === 'x-interfaceui-contexttype') {
                contentType = response.headers[key];
                contentType = contentType.toLowerCase();
                break;
            } else if (keyLC === 'content-type') {
                // "application/json;charset=UTF-8"
                contentEncode = response.headers[key];
                const splitContent = contentEncode.split(';');
                for (const splitItem in splitContent) {
                    const splitItemContent = splitContent[splitItem];
                    if (splitItemContent.toLowerCase().startsWith('charset=')) {
                        contentEncode = splitItemContent.substring('charset='.length, splitItemContent.length);
                    }
                }
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
            arrayBufferFromBlob(response.data).then((arrayBuffer) => {
                response.dataTypeMode = 'json';
                response.data = JSON.parse(decodeUtf8(arrayBuffer, contentEncode));
                successCallback(response);
            });
        } else if (contentType === 'text') {
            // text
            arrayBufferFromBlob(response.data).then((arrayBuffer) => {
                response.dataTypeMode = 'text';
                response.data = decodeUtf8(arrayBuffer, contentEncode);
                successCallback(response);
            });
        } else {
            // bytes
            arrayBufferFromBlob(response.data).then((arrayBuffer) => {
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
    }
    ).catch(errorCallback).finally(finallyCallback);
}
