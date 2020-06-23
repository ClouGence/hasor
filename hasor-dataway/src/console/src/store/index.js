import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const defaultApiInfo = {
    apiID: 1,
    select: 'POST',
    apiPath: '',
    comment: '',
    apiStatus: 0,
    codeType: 'DataQL',
    codeValue: '// a new Query.\nreturn ${message};',
    headerData: []
};

const store = new Vuex.Store({
    state: {
        currentApiInfo: defaultApiInfo
    },
    mutations: {
        updateApiInfo(state, payload) {
            state.currentApiInfo = payload;
        },
        updateHeader(state, payload) {
            state.currentApiInfo = payload;
        }
    }
});

export default store;
