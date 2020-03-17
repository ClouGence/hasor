import Vue from 'vue';
import axios from 'axios';
import {errorBox} from 'utils';

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

const checkStatus = response => {
    if (response.status === 200 || response.status === 304) {
        return response;
    }
    showMessage(response);
    const errorText = codeMessage[response.status] || response.statusText;
    const error = new Error(errorText);
    error.name = response.status;
    error.response = response;
    throw error;
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
    successCallback = (successCallback === null || successCallback === undefined) ? () => {
    } : successCallback;
    errorCallback = (errorCallback === null || errorCallback === undefined) ? showMessage : errorCallback;
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
    }).then(checkStatus).then(successCallback).catch(errorCallback).finally(finallyCallback);
}
