<template>
    <el-container>
        <el-header>
            <el-menu class="el-menu-demo" mode="horizontal">
                <el-menu-item index="1">
                    <router-link to="/">
                        <el-link><i class="el-icon-notebook-1"></i>Interface</el-link>
                    </router-link>
                </el-menu-item>
                <el-menu-item index="2">
                    <router-link to="/new">
                        <el-link><i class="el-icon-plus"></i>New</el-link>
                    </router-link>
                </el-menu-item>
                <el-menu-item index="3">
                    <el-link href="https://www.hasor.net/web/dataql/what_is_dataql.html" target="_blank"><i class="el-icon-warning-outline"></i>What is DataQL?</el-link>
                </el-menu-item>
            </el-menu>
        </el-header>
        <el-main>
            <div :style="{height:fullHeight + 'px', overflow: 'hidden'}"><router-view/></div>
        </el-main>
    </el-container>
</template>

<script>
export default {
  name: 'App',
  data: function () {
    return {
      fullHeight: document.documentElement.clientHeight - 60
    }
  },
  mounted () {
    this.fullHeight = document.documentElement.clientHeight - 60
    window.addEventListener('resize', () => {
      return (() => {
        this.fullHeight = document.documentElement.clientHeight - 60
      })()
    })
  }
}
</script>
<style scoped>
    .el-header, .el-main {
        padding: 0;
    }
</style>
