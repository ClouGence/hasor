<template>
    <div class="requestPanel">
        <div class="request-btns">
            <el-button class="z-index-top" icon="el-icon-s-promotion" size="mini" type="success"
                       plain v-if="this.hideRunBtn === false"
                       @click.native='triggerRun'/>
            <el-button class="z-index-top" icon="el-icon-s-open" size="mini" type="warning"
                       plain v-if="this.panelMode === 'req_parameters'"
                       @click.native='handleParametersFormatter'/>
            <el-button class="z-index-top" icon="el-icon-plus" size="mini" type="primary"
                       plain v-if="this.panelMode === 'req_headers'"
                       @click.native='handleHeaderAddNew'/>
        </div>
        <el-tabs class="request-tabs" type="card" v-model="panelMode">
            <el-tab-pane name="req_parameters" label="Parameters" lazy>
                <div :id="id + '_requestBodyRef'">
                    <codemirror v-model="requestBodyCopy" :options="defaultOption"/>
                </div>
            </el-tab-pane>
            <el-tab-pane name="req_headers" label="Headers" lazy>
                <el-table ref="requestHeaderTable" :data="headerDataCopy" :height="headerPanelHeight" border empty-text="No Header">
                    <el-table-column prop="checked" width="24" :resizable='false'>
                        <template slot="header" slot-scope="scope">
                            <el-checkbox name="type" v-model='headerSelectAllStatus' :indeterminate='headerSelectIndeterminateStatus' @change="handleHeaderCheckAllChange"/>
                        </template>
                        <template slot-scope="scope">
                            <el-checkbox name="type" v-model="scope.row.checked" @change="updateIndeterminate"/>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="Key" min-width="30%">
                        <template slot-scope="scope">
                            <el-input v-model="scope.row.name" size="mini" placeholder="key of Header"/>
                        </template>
                    </el-table-column>
                    <el-table-column prop="value" label="Value" :resizable='false'>
                        <template slot-scope="scope">
                            <el-input v-model="scope.row.value" size="mini" placeholder="value of Header"/>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" width="38" :resizable='false'>
                        <template slot-scope="scope">
                            <el-button size="mini" type="danger" icon="el-icon-delete"
                                       circle @click.native='handleHeaderDelete(scope.row,scope.$index)'/>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>
<script>
    import 'codemirror'

    export default {
        props: {
            id: {
                type: String,
                default: function () {
                    return 'requestPanel';
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
                requestBodyCopy: '',
                headerDataCopy: [],
                //
                headerPanelHeight: '100%',
                panelMode: 'req_parameters',
                headerSelectIndeterminateStatus: false,
                headerSelectAllStatus: false,
                defaultOption: {
                    tabSize: 4,
                    styleActiveLine: true,
                    lineNumbers: true,
                    line: true,
                    mode: 'text/javascript'
                }
            }
        },
        mounted() {
            this.doUpdate();
        },
        watch: {
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
                let newArrays = [];
                for (let i = 0; i < this.headerDataCopy.length; i++) {
                    if (i !== rowIndex) {
                        newArrays.push(this.headerDataCopy[i]);
                    }
                }
                this.headerDataCopy = newArrays;
                this.updateIndeterminate();
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
            doLayout(height) {
                let requestBodyID = '#' + this.id + '_requestBodyRef';
                let requestBody = document.querySelectorAll(requestBodyID + ' .CodeMirror')[0];
                requestBody.style.height = (height - 31) + 'px';
                this.headerPanelHeight = (height - 31) + 'px';
            },
            doUpdate() {
                this.requestBodyCopy = this.requestBody;
                this.headerDataCopy = this.headerData;
            }
        }
    }
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
        z-index: 10000;
    }
</style>
