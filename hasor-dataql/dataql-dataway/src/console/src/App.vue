<template>
  <el-container>
    <el-header>
      <el-menu class="el-menu-demo" mode="horizontal">
        <el-menu-item index="1">
          <router-link to="/">
            <el-link><i class="el-icon-notebook-1" />Interface</el-link>
          </router-link>
        </el-menu-item>
        <el-menu-item index="2">
          <router-link to="/new">
            <el-link><i class="el-icon-plus" />New</el-link>
          </router-link>
        </el-menu-item>
        <el-menu-item index="3">
          <el-link href="https://www.hasor.net/web/dataql/what_is_dataql.html" target="_blank"><i class="el-icon-warning-outline"></i>What is DataQL?</el-link>
        </el-menu-item>
      </el-menu>
      <div v-if="this.defaultOption.showGitButton" class="gitStyle">
        <!-- Github -->
        <span><a target="_blank" href="https://github.com/zycgit/hasor/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/zycgit/hasor?style=social"></a></span>
        <span><a target="_blank" href="https://github.com/zycgit/hasor/network"><img alt="GitHub forks" src="https://img.shields.io/github/forks/zycgit/hasor?style=social"></a></span>
        <br />
        <!-- gitee -->
        <span><a target="_blank" href="https://gitee.com/zycgit/hasor/stargazers"><img src="https://gitee.com/zycgit/hasor/badge/star.svg?theme=white" alt="star"></a></span>
        <span><a target="_blank" href="https://gitee.com/zycgit/hasor/members"><img src="https://gitee.com/zycgit/hasor/badge/fork.svg?theme=white" alt="fork"></a></span>
      </div>
    </el-header>
    <el-main>
      <div :style="{height:fullHeight + 'px', overflow: 'hidden'}">
        <router-view />
      </div>
    </el-main>
  </el-container>
</template>

<script>
export default {
    name: 'App',
    data: function () {
        return {
            fullHeight: document.documentElement.clientHeight - 60,
        };
    },
    mounted() {
        this.fullHeight = document.documentElement.clientHeight - 60;
        window.addEventListener('resize', () => {
            return (() => {
                this.fullHeight = document.documentElement.clientHeight - 60;
            })();
        });
    }
};
</script>
<style scoped>
.gitStyle {
  float: right;
  top: -55px;
  position: relative;
}

.gitStyle span {
  display: inline-block;
  width: 100px;
}
</style>
