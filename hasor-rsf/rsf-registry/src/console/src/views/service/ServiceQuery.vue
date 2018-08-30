<template>
  <div>
    <!-- 搜索 -->
    <el-tabs type="card" :activeName="activeName">
      <el-tab-pane name="servicePane" label="服务">
        <el-form ref="form" size="mini">
          <el-input placeholder="请输入服务名...格式：<服务名>，例如：net.hasor.rsf.xxx.service">
            <el-button slot="append" icon="el-icon-search">搜索</el-button>
          </el-input>
        </el-form>
      </el-tab-pane>
      <el-tab-pane name="appPane" label="应用">
        <el-form ref="form" size="mini">
          <el-input placeholder="请输入应用名...">
            <el-button slot="append" icon="el-icon-search">搜索</el-button>
          </el-input>
        </el-form>
      </el-tab-pane>
      <el-tab-pane name="ipPane" label="IP">
        <el-form ref="form" size="mini">
          <el-input placeholder="请输入IP...">
            <el-button slot="append" icon="el-icon-search">搜索</el-button>
          </el-input>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <!-- 查询结果 -->
    <br/>
    <el-table :data="dataList" :default-sort="{prop: 'date', order: 'descending'}">
      <el-table-column prop="serviceID" label="serviceID" sortable>
        <template slot-scope="scope">
          <router-link :to="{path:'/service/detail?service_id='+scope.row.serviceID}">{{scope.row.serviceID}}</router-link>
        </template>
      </el-table-column>
      <el-table-column prop="group" label="分组" sortable width="180" align="center"></el-table-column>
      <el-table-column prop="appName" label="应用" sortable width="180" align="center"></el-table-column>
      <el-table-column prop="serviceID" label="操作" width="200" align="center">
        <template slot-scope="scope">
          <router-link :to="{path:'/service/detail?service_id='+scope.row.serviceID}">
            <el-button size="mini">详情</el-button>
          </router-link>
          <router-link :to="{path:'/service/detail?service_id='+scope.row.serviceID}">
            <el-button size="mini">测试</el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <br/>
    <div align="center">
      <el-pagination :current-page="5" :total="1000" :page-size="100" layout="prev, pager, next, total"></el-pagination>
    </div>
  </div>
</template>

<script>

  export default {
    data() {

      var dataList = [
        {"serviceID": "xxxxxxx", "group": "xxxxxxx", "appName": "xxxxxxx"},
        {"serviceID": "yyyyyy", "group": "yyyyyy", "appName": "yyyyyy"},
      ];

      return {
        activeName: 'servicePane',
        dataList: dataList
      };
    }
  };
</script>

<style>

</style>
