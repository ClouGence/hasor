import Vue from 'vue';
import App from './App.vue';
import router from './router';
import splitPane from 'vue-splitpane';
import VueClipboard from 'vue-clipboard2';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/public.css';
import axios from 'axios';
import {ApiUrl} from '@/utils/api-const';

//
const lastCheckVersion = localStorage.getItem('lastCheckVersion');
if (lastCheckVersion === undefined || lastCheckVersion === null) {
    localStorage.setItem('lastCheckVersion', window.DATAWAY_VERSION);
}
const lastCheckVersionDialogRemember = localStorage.getItem('lastCheckVersionDialogRemember');
if (lastCheckVersionDialogRemember === undefined || lastCheckVersionDialogRemember === null) {
    localStorage.setItem('lastCheckVersionDialogRemember', 'false');
}

const configIsBool = ['resultStructure', 'wrapAllParameters', 'showGitButton', 'checkDatawayVersion'];
const toBoolean = (val) => {
    return val != null && val.toLowerCase() === 'true';
};

axios({
    url: ApiUrl.globalConfig,
    method: 'GET',
    Accept: 'application/json',
    withCredentials: true,
    responseType: 'json',
}).then(async (response) => {
    const defaultOption = {
        resultStructure: true,
        responseFormat:
            '{\n' +
            '  "success"      : "@resultStatus",\n' +
            '  "message"      : "@resultMessage",\n' +
            '  "code"         : "@resultCode",\n' +
            '  "lifeCycleTime": "@timeLifeCycle",\n' +
            '  "executionTime": "@timeExecution",\n' +
            '  "value"        : "@resultData"\n' +
            '}',
        wrapAllParameters: false,
        wrapParameterName: 'root',
        showGitButton: true,
        checkDatawayVersion: true
    };
    if (response.data.success) {
        const configs = response.data.result;
        Object.keys(configs).forEach(function (key) {
            if (configIsBool.indexOf(key) > -1) {
                defaultOption[key] = toBoolean(configs[key]);
            } else {
                defaultOption[key] = configs[key];
            }
        });
    }
    //
    Vue.prototype.defaultOption = defaultOption;
    Vue.config.productionTip = false;
    Vue.use(ElementUI);
    Vue.component('SplitPane', splitPane);
    Vue.use(VueClipboard);
    new Vue({
        router,
        render: h => h(App)
    }).$mount('#app');
});

