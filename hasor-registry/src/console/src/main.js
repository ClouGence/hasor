import Vue from 'vue'
import MainApp from './MainApp'
import router from './router'


Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<MainApp/>',
  components: {MainApp}
})
