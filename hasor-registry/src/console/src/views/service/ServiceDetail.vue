<template>
  <el-collapse v-model="activeCollapse">

    <!-- 基本信息 -->
    <el-collapse-item name="1">
      <template slot="title">
        <div class="blackColor">基本信息</div>
      </template>
      服务ID：<br/>
      应用名：<br/>
      分组名：<br/>
    </el-collapse-item>

    <!-- 服务信息 -->
    <el-collapse-item name="2">
      <template slot="title">
        <div class="blackColor">服务信息</div>
      </template>
      <el-tabs type="card" activeName="providerPane">
        <el-tab-pane name="providerPane" label="提供者">
          <el-input placeholder="请输入服务名...格式：<服务名>，例如：net.hasor.rsf.xxx.service">
            <el-button slot="append" icon="el-icon-search">过滤</el-button>
          </el-input>
          <el-table :data="dataList" :default-sort="{prop: 'date', order: 'descending'}">
            <el-table-column prop="ip" label="IP" sortable>
              <template slot-scope="scope">
                <router-link to="/telnet/255.255.255.255:2181">255.255.255.255</router-link>
                <el-tag size="mini" type="success">unitname</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="protocol" label="协议" align="center" width="200">
              <template slot-scope="scope">
                <el-tooltip content="端口：2180" placement="bottom" effect="light">
                  <el-tag size="mini">RSF/1.0</el-tag>
                </el-tooltip>
                <el-tooltip content="端口：2181" placement="bottom" effect="light">
                  <el-tag size="mini">Hprose</el-tag>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="timeout" label="超时时间(ms)" align="center" width="140"></el-table-column>
            <el-table-column prop="timeout" label="序列化方式" align="center" width="200">
              <template slot-scope="scope">
                <el-tag size="mini" type="warning">Java</el-tag>
                <el-tag size="mini" type="warning">Hprose</el-tag>
                <el-tag size="mini" type="warning">Json</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div align="center">
            <el-pagination :current-page="5" :total="1000" :page-size="100"
                           layout="prev, pager, next, total"></el-pagination>
          </div>
        </el-tab-pane>

        <el-tab-pane name="consumerPane" label="消费者">
          <el-input placeholder="请输入服务名...格式：<服务名>，例如：net.hasor.rsf.xxx.service">
            <el-button slot="append" icon="el-icon-search">过滤</el-button>
          </el-input>
          <el-table :data="dataList" :default-sort="{prop: 'date', order: 'descending'}">
            <el-table-column prop="ip" label="IP" sortable>
              <template slot-scope="scope">
                <router-link to="/telnet/255.255.255.255:2181">255.255.255.255:2181</router-link>
                <el-tag size="mini" type="success">unitname</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="protocol" label="协议" align="center" width="100">
              <template slot-scope="scope">
                <el-tag size="mini">RSF/1.0</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="appName" label="应用" sortable align="center" width="200"></el-table-column>
            <el-table-column prop="timeout" label="超时时间(ms)" align="center" width="140"></el-table-column>
            <el-table-column prop="timeout" label="序列化方式" align="center" width="140">
              <template slot-scope="scope">
                <el-tag size="mini" type="warning">Json</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div align="center">
            <el-pagination :current-page="5" :total="1000" :page-size="100"
                           layout="prev, pager, next, total"></el-pagination>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-collapse-item>

    <!-- 元数据 -->
    <el-collapse-item name="3">
      <template slot="title">
        <div class="blackColor">元数据</div>
      </template>
      服务的方法信息...
    </el-collapse-item>

  </el-collapse>

</template>

<script>

  export default {
    data() {

      var dataList = [
        {"serviceID": "xxxxxxx", "group": "xxxxxxx", "appName": "xxxxxxx"},
      ];

      return {
        activeCollapse: ["1", "2", "3"],
        dataList: dataList
      };
    }
  };
</script>

<style>
</style>
