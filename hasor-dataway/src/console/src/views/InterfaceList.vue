<template>
    <SplitPane :min-percent='30' :default-percent='30' split="vertical">
        <template slot="paneL">
            <el-table ref="interfaceTable" height="100%"
                      :data="tableData.filter(dat => !apiSearch || dat.path.toLowerCase().includes(apiSearch.toLowerCase()) || dat.comment.toLowerCase().includes(apiSearch.toLowerCase()))"
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
                        <el-tag size="mini" style="float: left;width: 65px;text-align: center;" :type="tableRowTagClassName(scope.row).css">{{tableRowTagClassName(scope.row).title}}</el-tag>
                        <span style="overflow-x: hidden;">{{requestPath(scope.row.path)}}&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span style="color: #adadad;display: contents;float: right; overflow-x: hidden;">[{{scope.row.comment}}]</span>
                    </template>
                </el-table-column>
                <el-table-column prop="id" width="24" :resizable='false'>
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
                                   :response-body="responseBody" :on-edit-page="false"
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
    import {ApiUrl, contextPath} from "../utils/api-const"
    import {checkRequestBody, errorBox, headerData, tagInfo} from "../utils/utils"

    export default {
        components: {
            RequestPanel, ResponsePanel
        },
        mounted() {
            this.handleSplitResize(this.panelPercent);
            //
            const self = this;
            this._resize = () => {
                return (() => {
                    self.handleSplitResize(self.panelPercent);
                })();
            };
            window.addEventListener('resize', this._resize);
            this.loadList();
        },
        beforeDestroy() {
            window.removeEventListener('resize', this._resize);
        },
        methods: {
            requestPath(apiPath) {
                return contextPath() + apiPath;
            },
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
                return tagInfo(row.status);
            },
            //
            //
            // 加载列表
            loadList() {
                const self = this;
                request(ApiUrl.apiList, {
                    "method": "GET",
                }, response => {
                    self.tableData = response.data.result;
                    if (self.tableData && self.tableData.length > 0) {
                        self.tableData[0].checked = true;
                        self.$refs.interfaceTable.setCurrentRow(self.tableData[0]);
                    }
                });
            },
            // 加载一个API
            loadApi(row) {
                if (row === null || row === undefined) {
                    return;
                }
                const self = this;
                request(ApiUrl.apiInfo + '?id=' + row.id, {
                    "method": "GET"
                }, response => {
                    let data = response.data.result;
                    self.requestApiInfo = data;
                    self.requestBody = data.requestBody || "{}";
                    self.responseBody = data.responseBody || '"empty."';
                    self.headerData = data.headerData || [];
                    self.$nextTick(function () {
                        self.$refs.listRequestPanel.doUpdate();
                        self.$refs.listResponsePanel.doUpdate();
                    });
                });
            },
            // 执行API调用
            handleRun() {
                if (!(this.requestApiInfo.status === 1 || this.requestApiInfo.status === 2)) {
                    this.$message.error('Api must be Published or Changes.');
                    return;
                }
                //
                let testResult = checkRequestBody(this.requestApiInfo.select, this.requestApiInfo.codeType, this.requestBody);
                if (!testResult) {
                    return;
                }
                //
                const self = this;
                let requestURL = contextPath() + ("/" + this.requestApiInfo.path).replace("//", "/");
                request(requestURL, {
                    "direct": true,
                    "method": this.requestApiInfo.select,
                    "headers": headerData(this.headerData),
                    "data": JSON.parse(this.requestBody)
                }, response => {
                    self.responseBody = JSON.stringify(response.data, null, 2);
                    self.$nextTick(function () {
                        self.$refs.listResponsePanel.doUpdate();
                        self.$message({message: 'Success.', type: 'success'});
                    });
                }, response => {
                    errorBox(`${response.data.code}: ${response.data.message}`);
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
<style lang="scss"/>
