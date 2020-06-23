import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import splitPane from 'vue-splitpane';
import VueClipboard from 'vue-clipboard2';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/public.css';

//
const lastCheckVersion = localStorage.getItem('lastCheckVersion');
if (lastCheckVersion === undefined || lastCheckVersion === null) {
    localStorage.setItem('lastCheckVersion', window.DATAWAY_VERSION);
}
const lastCheckVersionDialogRemember = localStorage.getItem('lastCheckVersionDialogRemember');
if (lastCheckVersionDialogRemember === undefined || lastCheckVersionDialogRemember === null) {
    localStorage.setItem('lastCheckVersionDialogRemember', 'false');
}

//
Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.component('SplitPane', splitPane);
Vue.use(VueClipboard);
new Vue({
    el: '#app',
    router,
    store, // store:store 和router一样，将我们创建的Vuex实例挂载到这个vue实例中
    render: h => h(App)
});
