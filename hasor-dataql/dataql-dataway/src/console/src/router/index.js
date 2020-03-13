import Vue from 'vue'
import VueRouter from 'vue-router'
import InterfaceList from '../views/InterfaceList.vue'
import InterfaceEdit from '../views/InterfaceEdit.vue'

Vue.use(VueRouter)

const routes = [
    {path: '/', name: 'root', component: InterfaceList},
    {path: '/new', name: 'new', component: InterfaceEdit},
    {path: '/edit/:id', name: 'edit', component: InterfaceEdit}
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
