import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/Home'
//
// -服务查询
import ServiceQuery from '@/views/service/ServiceQuery'
import ServiceDetail from '@/views/service/ServiceDetail'
//
// -单机维护
import ServerTelnet from '@/views/appops/ServerTelnet'
//
// -服务治理
import RoutingRule from '@/views/governance/RoutingRule'

//
//
Vue.use(Router)

const SubView = {
  template: `
      <router-view></router-view>
  `
}

export default new Router({
  routes: [
    {
      path: '/', name: '首页', component: Home,
    },
    {
      path: '/service', name: '服务查询', component: SubView,
      children: [
        {path: '', component: ServiceQuery},
        {path: 'detail', name: '服务信息', component: ServiceDetail}
      ]
    },
    {
      path: '/telnet/:ip', name: '单机运维', component: ServerTelnet
    },
    {
      path: '/governance', name: '服务治理', component: SubView,
      children: [
        {path: '', component: Home},
        {path: 'public', name: '全局规则', component: Home},
        {path: 'traffic', name: '流量控制', component: Home},
        {path: 'routing', name: '路由策略', component: RoutingRule},
        {path: 'unit', name: '跨单元', component: Home},
        {path: 'sentinel', name: '限流', component: Home}
      ]
    },

  ]
});
