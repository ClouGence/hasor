import Vue from 'vue';
import axios from 'axios';
import {errorBox} from './utils';

const codeMessage = {
    200: 'ok.',
    401: 'no permission.',
    404: 'not found.'
};

const showMessage = res => {
    let response = res.response;
    const errorText = codeMessage[response.status] || response.statusText;
    errorBox(`${response.status}: ${errorText}`);
};

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
        let contentType = "";
        for (let key in response.headers) {
            if (key.toLowerCase() === 'x-interfaceui-contexttype') {
                contentType = response.headers[key];
                contentType = contentType.toLowerCase();
                break;
            }
        }
        if (contentType === undefined || contentType == null || contentType === '') {
            if (response.data.type === "application/json") {
                contentType = 'json';
            }
        }
        //
        if (contentType === 'json') {
            // json
            response.dataTypeMode = 'json';
            let text = await response.data.text();
            response.data = JSON.parse(text);
            successCallback(response);
        } else if (contentType === 'text') {
            // text
            response.dataTypeMode = 'text';
            response.data.result = await response.data.text();
            successCallback(response);
        } else {
            // bytes
            response.dataTypeMode = 'bytes';
            let buffer = await response.data.arrayBuffer();
            let bufferTypes = new Uint8Array(buffer);
            let tempString = "";
            let n = 0;
            for (let i = 0; i < bufferTypes.length; ++i) {
                let hexDat = bufferTypes[i].toString(16).toUpperCase();
                if (hexDat.length === 1) {
                    hexDat = "0" + hexDat;
                }
                if (n < 15) {
                    n++;
                    tempString = tempString + hexDat + " ";
                } else {
                    n = 0;
                    tempString = tempString + hexDat + "\n";
                }
            }
            response.data.result = tempString.trim();
            successCallback(response);
        }
    }).catch(errorCallback).finally(finallyCallback);
}
