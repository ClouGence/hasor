import Vue from 'vue'
import Router from 'vue-router'
import InterfaceList from '@/components/InterfaceList'
import InterfaceEdit from '@/components/InterfaceEdit'
Vue.use(Router)

export default new Router({
  routes: [
    {path: '/', name: 'InterfaceList', component: InterfaceList},
    {path: '/new', name: 'InterfaceEdit', component: InterfaceEdit},
    {path: '/edit', name: 'InterfaceEdit', component: InterfaceEdit}
  ]
})
