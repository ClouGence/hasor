<template>
  <div>
    <!-- 选择机器 -->
    <el-input placeholder="请输入IP地址" :value="address" class="input-with-select">
      <el-button slot="append" icon="el-icon-refresh">连通</el-button>
    </el-input>

    <el-collapse v-model="activeCollapse">
      <!-- 基本信息 -->
      <el-collapse-item name="1">
        <template slot="title">
          <div class="blackColor">基本信息</div>
        </template>
        服务器：{{$route.params.ip}}<br/>
        应用名：<br/>
        单元名：<br/>
      </el-collapse-item>

      <!-- 服务列表 -->
      <el-collapse-item name="2">
        <template slot="title">
          <div class="blackColor">服务列表</div>
        </template>
        <el-table :data="serviceDataList" :default-sort="{prop: 'date', order: 'descending'}">
          <el-table-column prop="serviceName" label="服务名" sortable>
            <template slot-scope="scope">
              <el-tag size="mini">RSF</el-tag>
              <router-link :to="{path:'/service/detail?service_id=xxxxx'}">xxxx.xxxx.xxxx</router-link>
            </template>
          </el-table-column>
          <el-table-column prop="serviceVersion" label="版本" width="150">
            <template slot-scope="scope">
              1.0.0
            </template>
          </el-table-column>
          <el-table-column prop="mode" label="模式" align="center" width="80">
            <template slot-scope="scope">
              <el-tag size="mini">消费者</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="timeout" label="序列化方式" align="center" width="140">
            <template slot-scope="scope">
              <el-tag size="mini" type="warning">Json</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>

      <!-- 可执行的命令 -->
      <el-collapse-item name="3">
        <template slot="title">
          <div class="blackColor">运维指令</div>
        </template>
        <el-radio-group :v-model="1">
          <el-radio :label="1">SSSSSSSS</el-radio>
          <el-radio :label="2">SSSSSSSS</el-radio>
          <el-radio :label="3">SSSSSSSS</el-radio>
        </el-radio-group>
        <el-input type="textarea" :rows="4" placeholder="指令参数..." v-model="commondBody"></el-input>
      </el-collapse-item>

      <!-- 执行结果 -->
      <el-collapse-item name="4">
        <template slot="title">
          <div class="blackColor">执行结果</div>
        </template>
        <el-input type="textarea" :rows="6" placeholder="执行结果..." v-model="commondBody" readonly="true"></el-input>
      </el-collapse-item>
    </el-collapse>


  </div>
</template>

<script>
  export default {
    data() {
      return {
        address: "$route.params.ip",
        activeCollapse: ["1", "2", "3", "4"],
        commondBody: '',
        serviceDataList: [
          {}
        ]
      }
    }
  }
</script>

<style>
</style>
