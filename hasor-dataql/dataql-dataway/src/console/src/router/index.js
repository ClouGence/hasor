import Vue from 'vue'
import Router from 'vue-router'
import InterfaceList from '@/components/InterfaceList'
import InterfaceEdit from '@/components/InterfaceEdit'
Vue.use(Router)

export default new Router({
  routes: [
    {path: '/', name: 'root', component: InterfaceList},
    {path: '/new', name: 'new', component: InterfaceEdit},
    {path: '/edit/:id', name: 'edit', component: InterfaceEdit}
  ]
})
