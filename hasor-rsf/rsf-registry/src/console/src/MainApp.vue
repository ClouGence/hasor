<template>
  <div>
    <!-- 顶部菜单 -->
    <el-menu :default-active="activeIndex" @select="handleSelect" mode="horizontal" v-show="false"
             class="el-menu-demo" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
      <el-menu-item index="1">处理中心</el-menu-item>
      <el-submenu index="2">
        <template slot="title">我的工作台</template>
        <el-menu-item index="2-1">选项1</el-menu-item>
        <el-menu-item index="2-2">选项2</el-menu-item>
        <el-menu-item index="2-3">选项3</el-menu-item>
      </el-submenu>
      <el-menu-item index="3"><a href="https://www.ele.me" target="_blank">订单管理</a></el-menu-item>
    </el-menu>

    <!-- 顶部面包屑 -->
    <NavBread></NavBread>
    <br/>

    <!-- 正文部分 -->
    <router-view/>
  </div>
</template>

<script>
  import Vue from 'vue'
  import ElementUI from 'element-ui'
  import NavBread from './components/NavBread'

  Vue.use(ElementUI);

  export default {
    components: {
      NavBread,
    },
    data() {
      return {
        activeIndex: '1'
      };
    },
    methods: {
      handleSelect(key, keyPath) {
        console.log(key, keyPath);
      }
    }
  }
</script>

<style>
  .blackColor {
    padding-left: 15px;
    background-color: #f1f1f1;
  }
</style>
