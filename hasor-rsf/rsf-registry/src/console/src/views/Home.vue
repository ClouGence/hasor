<template>
  <div>
    <p>
      <router-link to="/service"><el-button>服务查询</el-button></router-link>
      <router-link to="/telnet/0.0.0.0:0"><el-button>单机运维</el-button></router-link>

      <router-link to="/hello"><el-button>服务测试</el-button></router-link>
      <router-link to="/hello"><el-button>服务鉴权</el-button></router-link>
    </p>
    <p>
      <router-link to="/governance/public"><el-button>全局规则</el-button></router-link>
      <router-link to="/governance/traffic"><el-button>流量控制</el-button></router-link>
      <router-link to="/governance/routing"><el-button>路由策略</el-button></router-link>
      <router-link to="/governance/unit"><el-button>跨单元</el-button></router-link>
      <router-link to="/governance/sentinel"><el-button>限流</el-button></router-link>

    </p>
  </div>
</template>

<script>
  export default {}
</script>

<style>
</style>
