<template>
  <div class="requestPanel">
    <div class="request-btns" style="height: 30px;">
      <el-button-group>
        <el-tooltip class="item" effect="dark" content="Execute Query" placement="bottom-end">
          <el-button v-if="this.hideRunBtn === false" class="z-index-top" size="mini" round @click.native="triggerRun">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconexecute"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip v-if="this.panelMode === 'req_parameters'" class="item" effect="dark" placement="bottom-end" content="Format Parameters">
          <el-button class="z-index-top" size="mini" round @click.native="handleParametersFormatter">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconformat"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip v-if="this.panelMode === 'req_headers'" class="item" effect="dark" placement="bottom-end" content="Add Header">
          <el-button class="z-index-top" size="mini" round @click.native="handleHeaderAddNew">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconadd"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip v-if="this.panelMode === 'req_schema'" class="item" effect="dark" placement="bottom-end" content="refresh Schema">
          <el-button class="z-index-top" size="mini" round @click.native="handleAnalyzeParametersSchema">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconanalysis"></use>
            </svg>
          </el-button>
        </el-tooltip>
      </el-button-group>
    </div>
    <el-tabs v-model="panelMode" class="request-tabs" type="card">
      <el-tab-pane name="req_parameters" label="Parameters" lazy>
        <div ref="requestPanel" />
      </el-tab-pane>
      <el-tab-pane name="req_headers" label="Headers" lazy>
        <el-table ref="requestHeaderTable" :data="headerDataCopy" :height="headerPanelHeight" border empty-text="No Header">
          <el-table-column prop="checked" width="24" :resizable="false">
            <template slot="header" slot-scope="scope">
              <el-checkbox v-model="headerSelectAllStatus" name="type" :indeterminate="headerSelectIndeterminateStatus" @change="handleHeaderCheckAllChange" />
            </template>
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.checked" name="type" @change="updateIndeterminate" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="Key" min-width="30%">
            <template slot-scope="scope">
              <el-input v-model="scope.row.name" size="mini" placeholder="key of Header" />
            </template>
          </el-table-column>
          <el-table-column prop="value" label="Value" :resizable="false">
            <template slot-scope="scope">
              <el-input v-model="scope.row.value" size="mini" placeholder="value of Header" />
            </template>
          </el-table-column>
          <el-table-column prop="name" width="38" :resizable="false">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="dark" content="Delete" placement="left">
                <el-button size="mini" type="danger" icon="el-icon-delete" circle @click.native="handleHeaderDelete(scope.row,scope.$index)" />
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <!--      <el-tab-pane name="req_schema" label="Schema" lazy>-->
      <!--        <el-table :data="tableData" :height="headerPanelHeight" row-key="id" border default-expand-all :tree-props="{children: 'children', hasChildren: 'hasChildren'}">-->
      <!--          <el-table-column prop="date" label="Name" :resizable="false" />-->
      <!--          <el-table-column prop="date" label="Type" width="120" :resizable="false">-->
      <!--            <template slot-scope="scope">-->
      <!--              <el-select v-model="scope.row.type" style="width: 100%" size="mini" placeholder="Choose">-->
      <!--                <el-option label="Any" value="any" />-->
      <!--                <el-option label="Number" value="number" />-->
      <!--                <el-option label="Boolean" value="boolean" />-->
      <!--                <el-option label="String" value="string" />-->
      <!--                <el-option label="Array" value="array" />-->
      <!--                <el-option label="Object" value="object" />-->
      <!--              </el-select>-->
      <!--            </template>-->
      <!--          </el-table-column>-->
      <!--          <el-table-column prop="date" label="Comment" :resizable="false">-->
      <!--            <template slot-scope="scope">-->
      <!--              <el-input v-model="scope.row.defaultOrRefValue" style="width: 100%" size="mini" placeholder="value of Header" />-->
      <!--            </template>-->
      <!--          </el-table-column>-->
      <!--        </el-table>-->
      <!--      </el-tab-pane>-->
    </el-tabs>
  </div>
</template>
<script>
import {defineMonacoEditorFoo} from '@/utils/editorUtils';
import request from '../utils/request';
import {ApiUrl} from '@/utils/api-const';
import {errorBox} from '@/utils/utils';

export default {
    props: {
        optionInfo: {
            type: Object,
            default: function () {
                return {};
            }
        },
        apiInfo: {
            type: Object,
            default: function () {
                return {
                    apiID: -1,
                    select: 'POST',
                    apiPath: '',
                    comment: ''
                };
            }
        },
        requestBody: {
            type: String,
            default: function () {
                return '{}';
            }
        },
        headerData: {
            type: Array,
            default: function () {
                return [];
            }
        },
        hideRunBtn: {
            type: Boolean,
            default: function () {
                return false;
            }
        }
    },
    data() {
        return {
            optionInfoCopy: {},
            requestBodyCopy: '',
            headerDataCopy: [],
            //
            headerPanelHeight: '100%',
            panelMode: 'req_parameters',
            headerSelectIndeterminateStatus: false,
            headerSelectAllStatus: false,
            // tableData: [
            //     {
            //         id: 1,
            //         name: '王小虎',
            //         type: 'int',
            //         source: '',
            //         defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //         date: '2016-05-02'
            //     }, {
            //         id: 2,
            //         name: '王小虎',
            //         type: 'int',
            //         source: '',
            //         defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //         date: '2016-05-02'
            //     }, {
            //         id: 3,
            //         name: '王小虎',
            //         type: 'int',
            //         source: '',
            //         defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //         date: '2016-05-02',
            //         children: [{
            //             id: 31,
            //             name: '王小虎',
            //             type: 'int',
            //             source: '',
            //             defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //             date: '2016-05-02'
            //         }, {
            //             id: 32,
            //             name: '王小虎',
            //             type: 'int',
            //             source: '',
            //             defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //             date: '2016-05-02'
            //         }]
            //     }, {
            //         id: 4,
            //         name: '王小虎',
            //         type: 'int',
            //         source: '',
            //         defaultOrRefValue: '上海市普陀区金沙江路 1518 弄',
            //         date: '2016-05-02'
            //     }]
        };
    },
    watch: {
        'optionInfoCopy': {
            handler(val, oldVal) {
                this.$emit('onOptionChange', this.optionInfoCopy);
            },
            deep: true
        },
        'headerDataCopy': {
            handler(val, oldVal) {
                this.updateIndeterminate();
                this.$emit('onHeaderChange', this.headerDataCopy);
            },
            deep: true
        },
        'requestBodyCopy': {
            handler(val, oldVal) {
                this.$emit('onRequestBodyChange', this.requestBodyCopy);
            }
        }
    },
    mounted() {
        const self = this;
        this.monacoEditor = defineMonacoEditorFoo(this.$refs.requestPanel, {});
        this.monacoEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
            self.requestBodyCopy = self.monacoEditor.getValue();
        });
        this.doUpdate();
    },
    methods: {
        // Header 点击了全选
        handleHeaderCheckAllChange(s) {
            for (let i = 0; i < this.headerDataCopy.length; i++) {
                this.headerDataCopy[i].checked = s;
            }
            this.updateIndeterminate();
        },
        // 请求参数格式化
        handleParametersFormatter() {
            try {
                this.requestBodyCopy = JSON.stringify(JSON.parse(this.requestBodyCopy), null, 2);
                this.monacoEditor.setValue(this.requestBodyCopy);
            } catch (e) {
                this.$message.error('Parameters Format Error : ' + e);
            }
        },
        // Header 添加一个新的
        handleHeaderAddNew() {
            this.headerDataCopy.push({checked: true, name: '', value: ''});
            this.updateIndeterminate();
        },
        // Header 删除
        handleHeaderDelete(row, rowIndex) {
            const newArrays = [];
            for (let i = 0; i < this.headerDataCopy.length; i++) {
                if (i !== rowIndex) {
                    newArrays.push(this.headerDataCopy[i]);
                }
            }
            this.headerDataCopy = newArrays;
            this.updateIndeterminate();
        },
        // 请求参数结构分析
        handleAnalyzeParametersSchema() {
            const self = this;
            request(ApiUrl.analyzeSchema + '?id=' + this.apiInfo.apiID, {
                'method': 'POST',
                'data': {
                    'id': this.apiInfo.apiID,
                    'requestParameters': JSON.parse(this.requestBodyCopy),
                    'optionInfo': this.optionInfoCopy
                }
            }, response => {
                if (response.data.result) {
                    self.$message({message: 'Api Delete finish.', type: 'success'});
                } else {
                    errorBox('Request parameter structure analysis error.');
                }
            });
        },
        //
        updateIndeterminate() {
            let checkedCount = 0;
            for (let i = 0; i < this.headerDataCopy.length; i++) {
                if (this.headerDataCopy[i].checked) {
                    checkedCount++;
                }
            }
            this.headerSelectAllStatus = checkedCount === this.headerDataCopy.length;
            this.headerSelectIndeterminateStatus = checkedCount > 0 && checkedCount !== this.headerDataCopy.length;
        },
        // 触发执行
        triggerRun() {
            this.$emit('onRun');
        },
        // 执行布局
        doLayout(height, width) {
            this.headerPanelHeight = (height - 31) + 'px';
            this.monacoEditor.layout({height: (height - 31), width: width});
        },
        doUpdate() {
            this.optionInfoCopy = { ...this.defaultOption, ...this.optionInfo};
            this.requestBodyCopy = this.requestBody;
            this.headerDataCopy = this.headerData;
            this.monacoEditor.setValue(this.requestBodyCopy);
        }
    }
};
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

    .requestPanel {
    }

    .request-tabs {
        top: -33px;
        position: relative;
    }

    .request-btns {
        padding: 2px 5px;
        display: flex;
        justify-content: flex-end;

    }

    .z-index-top {
        z-index: 1000;
    }
</style>
