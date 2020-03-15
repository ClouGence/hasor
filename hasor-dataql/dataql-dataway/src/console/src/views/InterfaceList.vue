<template>
    <SplitPane :min-percent='30' :default-percent='50' split="vertical">
        <template slot="paneL">
            <el-table ref="interfaceTable" height="100%" v-loading="loading"
                      :data="tableData.filter(dat => !apiSearch || dat.path.toLowerCase().includes(apiSearch.toLowerCase()))"
                      @current-change="handleApiDataChange"
                      empty-text="No Api" highlight-current-row border lazy stripe>
                <el-table-column prop="id" width="24" :resizable='false'>
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" content="Choose to Test" placement="right">
                            <el-checkbox name="type" v-model="scope.row.checked" v-on:change="handleApiDataChange(scope.row)"/>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="path" label="Api" :show-overflow-tooltip="true" :resizable='false'>
                    <template slot="header" slot-scope="scope">
                        <el-input size="mini" v-model="apiSearch" placeholder="search Api"/>
                    </template>
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" :content="scope.row.comment" placement="top">
                            <span style="overflow-x: hidden;">{{scope.row.path}}</span>
                            <!--                            <span style="display:inline;overflow-x: hidden;font-size: 12px;color: #6c6c6c;"> - ({{scope.row.comment}})</span>-->
                        </el-tooltip>
                        <el-tag size="mini" style="float: right" :type="tableRowTagClassName(scope.row).css">{{tableRowTagClassName(scope.row).title}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="id" width="23" :resizable='false'>
                    <template slot="header">
                        <el-tooltip class="item" effect="dark" content="reload Api List" placement="right">
                            <el-link v-on:click="loadList"><i class="el-icon-refresh"/></el-link>
                        </el-tooltip>
                    </template>
                    <template slot-scope="scope">
                        <router-link :to="'/edit/' + scope.row.id">
                            <el-tooltip class="item" effect="dark" content="Edit" placement="right">
                                <el-link><i class="el-icon-edit"/></el-link>
                            </el-tooltip>
                        </router-link>
                    </template>
                </el-table-column>
            </el-table>
        </template>
        <template slot="paneR">
            <split-pane v-on:resize="handleSplitResize" :min-percent='30' :default-percent='panelPercent' split="horizontal">
                <template slot="paneL">
                    <RequestPanel id="listRequestPanel" ref="listRequestPanel"
                                  v-bind:header-data="headerData"
                                  v-bind:request-body="requestBody"
                                  @onRun="handleRun"
                                  @onHeaderChange="(data)=> { this.headerData = data}"
                                  @onRequestBodyChange="(data)=> { this.requestBody = data}"/>
                </template>
                <template slot="paneR">
                    <ResponsePanel id="listResponsePanel" ref="listResponsePanel"
                                   :response-body="responseBody"
                                   @onResponseBodyChange="(data)=> { this.responseBody = data}"/>
                </template>
            </split-pane>
        </template>
    </SplitPane>
</template>
<script>
    import RequestPanel from '../components/RequestPanel'
    import ResponsePanel from '../components/ResponsePanel'
    import request from "../utils/request";
    import {ApiUrl} from "../utils/api-const"

    export default {
        components: {
            RequestPanel, ResponsePanel
        },
        mounted() {
            this.handleSplitResize(this.panelPercent);
            //
            let _this = this;
            this._resize = () => {
                return (() => {
                    _this.handleSplitResize(_this.panelPercent);
                })();
            };
            window.addEventListener('resize', this._resize);
            this.loadList();
        },
        beforeDestroy() {
            window.removeEventListener('resize', this._resize);
        },
        methods: {
            // 面板大小改变，重新计算CodeMirror的高度
            handleSplitResize(data) {
                this.panelPercent = data;
                let dataNum = data / 100;
                let size = document.documentElement.clientHeight - 60;
                //
                this.$refs.listRequestPanel.doLayout(size * dataNum);
                this.$refs.listResponsePanel.doLayout(size * (1 - dataNum) + 10);
            },
            // 选择了 Api 中的一个，确保只能选一个
            handleApiDataChange(row) {
                if (row === null || row === undefined) {
                    return;
                }
                for (let i = 0; i < this.tableData.length; i++) {
                    this.tableData[i].checked = row.id === this.tableData[i].id;
                    if (this.tableData[i].checked) {
                        this.loadApi(this.tableData[i]);
                    }
                }
            },
            tableRowTagClassName(row) {
                if (row.status === 0) {
                    return {'css': 'info', 'title': 'Editor'};
                }
                if (row.status === 1) {
                    return {'css': 'success', 'title': 'Published'};
                }
                if (row.status === 2) {
                    return {'css': 'warning', 'title': 'Changes'};
                }
                if (row.status === 3) {
                    return {'css': 'danger', 'title': 'Disable'};
                }
                return {'css': '', 'title': ''};
            },
            //
            //
            // 加载列表
            loadList() {
                this.loading = true;
                request(ApiUrl.apiList, {
                    "method": "GET"
                }, response => {
                    this.tableData = response.data;
                    if (this.tableData && this.tableData.length > 0) {
                        this.tableData[0].checked = true;
                        this.$refs.interfaceTable.setCurrentRow(this.tableData[0]);
                    }
                    this.loading = false;
                }, response => {
                    this.$alert('Load Api List failed ->' + response.message, 'Error', {confirmButtonText: 'OK'});
                    this.loading = false;
                });
            },
            // 加载一个API
            loadApi(row) {
                if (row === null || row === undefined) {
                    return;
                }
                request(ApiUrl.apiInfo + '?id=' + row.id, {
                    "method": "GET"
                }, response => {
                    this.requestApiInfo = response.data;
                    this.requestBody = response.data.requestBody;
                    this.headerData = response.data.headerData;
                    this.$nextTick(function () {
                        this.$refs.listRequestPanel.doUpdate();
                        this.$refs.listResponsePanel.doUpdate();
                    });
                }, response => {
                    this.$alert('Load Api failed ->' + response.message, 'Error', {confirmButtonText: 'OK'});
                });
            },
            // 执行API调用
            handleRun() {
                if (!(this.requestApiInfo.status === 1 || this.requestApiInfo.status === 2)) {
                    this.$message.error('Api must be Published or Changes.');
                    return;
                }
                //
                let doRunParam = {};
                try {
                    doRunParam.id = this.requestApiInfo.id;
                    doRunParam.paramMap = JSON.parse(this.requestBody);
                } catch (e) {
                    this.$message.error('Parameters Format Error : ' + e);
                    return;
                }
                //
                let requestHeaderData = {};
                for (let i = 0; i < this.headerData.length; i++) {
                    if (this.headerData[i].checked && this.headerData[i].name !== '') {
                        requestHeaderData[this.headerData[i].name] = encodeURIComponent(this.headerData[i].value);
                    }
                }
                //
                request(ApiUrl.execute + '?id=' + doRunParam.id, {
                    "method": "POST",
                    "data": doRunParam.paramMap,
                    "headers": requestHeaderData
                }, response => {
                    this.responseBody = JSON.stringify(response.data, null, 2)
                    this.$nextTick(function () {
                        this.$refs.listResponsePanel.doUpdate();
                        this.$message({message: 'Success.', type: 'success'});
                    });
                }, response => {
                    this.$alert('Execute failed ->' + response.message, 'Error', {confirmButtonText: 'OK'});
                });
            }
        },
        data() {
            return {
                headerPanelHeight: '100%',
                panelPercent: 50,
                loading: false,
                //
                apiSearch: '',
                tableData: [],
                //
                headerData: [],
                requestApiInfo: {},
                requestBody: '{}',
                responseBody: '"empty."'
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
</style>
