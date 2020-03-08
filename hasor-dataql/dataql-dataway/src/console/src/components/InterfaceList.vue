<template>
  <SplitPane :min-percent='30' :default-percent='30' split="vertical">
    <template slot="paneL">
      <el-table ref="interfaceTable" :data="tableData" height="100%"
                :row-class-name="tableRowClassName"
                @current-change="handleTableDataCurrent"
                highlight-current-row border lazy >
        <el-table-column prop="id" width="24" :resizable='false'>
          <template slot-scope="scope">
            <el-checkbox name="type" v-model="scope.row.checked" v-on:change="handleTableDataCurrent(scope.row)"/>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="Api" :show-overflow-tooltip="true">
          <template slot="header" slot-scope="scope">
            <el-input size="mini" v-model="apiSearch" placeholder="search Api">
              <template slot="prepend">Api</template>
              <el-button slot="append" icon="el-icon-search"></el-button>
            </el-input>
          </template>
        </el-table-column>
      </el-table>
    </template>
    <template slot="paneR">
      <split-pane v-on:resize="handleSplitResize" :min-percent='30' :default-percent='panelPercent' split="horizontal">
        <template slot="paneL">
          <div class="request-btns">
            <el-button class="z-index-top" icon="el-icon-s-promotion" size="mini" type="success" plain></el-button>
            <el-button class="z-index-top" icon="el-icon-s-open" size="mini" type="warning"
                       plain v-if="this.panelMode === 'req_parameters'"></el-button>
            <el-button class="z-index-top" icon="el-icon-plus" size="mini" type="primary"
                       plain v-if="this.panelMode === 'req_headers'"></el-button>
          </div>
          <el-tabs class="request-tabs" type="card" v-model="panelMode">
            <el-tab-pane name="req_parameters" label="Parameters" lazy>
              <div id="requestBodyRef">
                <codemirror v-model="requestBody" :options="defaultOption"></codemirror>
              </div>
            </el-tab-pane>
            <el-tab-pane name="req_headers" label="Headers" lazy>
              <el-table ref="requestHeaderTable" :data="headerData" :height="headerPanelHeight"
                        @select-all="handleApisSelectAll" border>
                <el-table-column prop="checked" width="24" type="selection" :resizable='false'/>
                <el-table-column prop="name" label="Key" min-width="30%">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name" size="mini" placeholder="key of Header"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="value" label="Value" :resizable='false'>
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.value" size="mini" placeholder="value of Header"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="name" width="38" :resizable='false'>
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger" icon="el-icon-delete" circle></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </template>
        <template slot="paneR">
          <div class="response-btns">
            <el-button class="z-index-top" icon="el-icon-document-copy" size="mini" type="primary" plain></el-button>
            <el-button class="z-index-top" icon="el-icon-s-open" size="mini" type="warning" plain></el-button>
          </div>
          <el-tabs class="response-tabs" type="card">
            <el-tab-pane label="JsonResult">
              <div id="responseBodyRef">
                <codemirror v-model="responseBody" :options="defaultOption"></codemirror>
              </div>
            </el-tab-pane>
          </el-tabs>
        </template>
      </split-pane>
    </template>
  </SplitPane>
</template>
<script>
import '../../config/codemirror'

export default {
  methods: {
    handleSplitResize (data) {
      this.panelPercent = data
      let dataNum = data / 100
      let size = document.documentElement.clientHeight - 60
      let requestBody = document.querySelectorAll('#requestBodyRef .CodeMirror')[0]
      requestBody.style.height = (size * dataNum) - 31 + 'px'
      this.headerPanelHeight = (size * dataNum) - 31 + 'px'
      //
      let responseBody = document.querySelectorAll('#responseBodyRef .CodeMirror')[0]
      responseBody.style.height = (size * (1 - dataNum) - 36) + 'px'
    },
    handleApisSelectAll (s) {
      for (let j = 0; j < this.headerData.length; j++) {
        this.headerData[j].checked = false
        for (let i = 0; i < s.length; i++) {
          if (s[i].name === this.headerData[j].name) {
            this.headerData[j].checked = true
          }
        }
      }
    },
    handleTableDataCurrent (row) {
      for (let i = 0; i < this.tableData.length; i++) {
        this.tableData[i].checked = row.id === this.tableData[i].id
      }
    },

    tableRowClassName ({row, rowIndex}) {
      if (rowIndex === 1) {
        return 'warning-row'
      } else if (rowIndex === 3) {
        return 'success-row'
      }
      return ''//
    }
  },
  mounted () {
    if (this.tableData || this.tableData.length > 0) {
      this.tableData[0].checked = true
      this.$refs.interfaceTable.setCurrentRow(this.tableData[0])
    }
    let _this = this
    window.addEventListener('resize', () => {
      return (() => {
        _this.handleSplitResize(_this.panelPercent)
      })()
    })
    this.handleSplitResize(this.panelPercent)
  },
  data () {
    return {
      headerPanelHeight: '100%',
      panelPercent: 50,
      panelMode: 'req_parameters',
      defaultOption: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/javascript'
      },
      //
      //
      apiSearch: '',
      requestBody: '[1,2,3,45,6]',
      responseBody: 'responseBody',
      //
      //
      tableData: [
        {id: 1, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 2, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 3, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 4, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 5, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 6, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 7, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 8, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 9, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 0, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 11, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 12, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
        {id: 13, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'}
      ],
      headerData: [
        {checked: true, name: 'name1', value: 'value'},
        {checked: false, name: 'name2', value: 'value'},
        {checked: false, name: 'name3', value: 'value'},
        {checked: true, name: 'name4', value: 'value'},
        {checked: true, name: 'name5', value: 'value'}
      ]
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
  .request-tabs, .response-tabs {
    top: -33px;
    position: relative;
  }

  .request-btns, .response-btns {
    padding: 2px 5px;
    display: flex;
    justify-content: flex-end;

    .z-index-top {
      z-index: 10000;
    }
  }

  .el-tabs--card > .el-tabs__header {
    padding-left: 30px;
  }

  .el-table--border {
    border: 0px !important;
  }

  .el-input--mini .el-input__inner {
    height: 25px !important;
    line-height: 25px !important;
  }

  .el-table .warning-row {
    background: oldlace;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
</style>
