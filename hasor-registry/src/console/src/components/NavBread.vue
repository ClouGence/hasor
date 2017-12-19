<template>
  <el-breadcrumb separator="->">
    <el-breadcrumb-item><a href="/" target="_parent">首页</a></el-breadcrumb-item>
    <el-breadcrumb-item v-for="navItem in navList" :key="navItem.path">
      <router-link :to="{path:navItem.path}">{{navItem.name}}</router-link>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
  import Vue from 'vue'
  import {Breadcrumb, BreadcrumbItem} from 'element-ui'

  Vue.use(Breadcrumb);
  Vue.use(BreadcrumbItem);

  export default {
    created() {
      this.getBreadcrumb()
    },
    data() {
      return {
        navList: null
      }
    },
    methods: {
      getBreadcrumb() {
        this.navList = this.$route.matched.filter(item => {
          if (item.name === '' || item.name === undefined) {
            return;
          }
          return item.path;
        });
      }
    },
    watch: {
      $route() {
        this.getBreadcrumb();
      }
    }
  }
</script>

<style>
</style>
