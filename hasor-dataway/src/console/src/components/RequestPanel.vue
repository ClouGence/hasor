<template>
    <div class="requestPanel">
        <div class="request-btns" style="height: 30px;">
            <el-button-group>
                <el-tooltip class="item" effect="dark" content="Execute Query" placement="bottom-end">
                    <el-button class="z-index-top" size="mini" round
                               v-if="this.hideRunBtn === false" @click.native='triggerRun'>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconexecute"></use>
                        </svg>
                    </el-button>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" v-if="this.panelMode === 'req_parameters'"
                            placement="bottom-end" content="Format Parameters">
                    <el-button class="z-index-top" size="mini" round
                               @click.native='handleParametersFormatter'>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconformat"></use>
                        </svg>
                    </el-button>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" v-if="this.panelMode === 'req_headers'"
                            placement="bottom-end" content="Add Header">
                    <el-button class="z-index-top" size="mini" round
                               @click.native='handleHeaderAddNew'>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconadd"></use>
                        </svg>
                    </el-button>
                </el-tooltip>
            </el-button-group>
        </div>
        <el-tabs class="request-tabs" type="card" v-model="panelMode">
            <el-tab-pane name="req_parameters" label="Parameters" lazy>
                <div ref="requestPanel"/>
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
                            <el-tooltip class="item" effect="dark" content="Delete" placement="left">
                                <el-button size="mini" type="danger" icon="el-icon-delete"
                                           circle @click.native='handleHeaderDelete(scope.row,scope.$index)'/>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>
<script>
    import {defineMonacoEditorFoo} from "../utils/editorUtils"

    export default {
        props: {
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
                headerSelectAllStatus: false
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
            doLayout(height, width) {
                this.headerPanelHeight = (height - 31) + 'px';
                this.monacoEditor.layout({height: (height - 31), width: width});
            },
            doUpdate() {
                this.requestBodyCopy = this.requestBody;
                this.headerDataCopy = this.headerData;
                this.monacoEditor.setValue(this.requestBodyCopy);
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
        z-index: 1000;
    }
</style>
