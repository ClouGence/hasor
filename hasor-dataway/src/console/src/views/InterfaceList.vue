<template>
    <SplitPane v-on:resize="handleVerticalSplitResize" :min-percent='30' :default-percent='verticalPanelPercent' split="vertical">
        <template slot="paneL">
            <el-table ref="interfaceTable" height="100%"
                      :data="tableData.filter(dat => !apiSearch || dat.path.toLowerCase().includes(apiSearch.toLowerCase()) || dat.comment.toLowerCase().includes(apiSearch.toLowerCase()))"
                      @current-change="handleApiDataChange"
                      empty-text="No Api" highlight-current-row border lazy stripe>
                <el-table-column prop="id" width="24" :resizable='false'>
                    <template slot="header" class="dir-list-icon">
                        <el-tooltip class="item" effect="dark" content="Directory" placement="right">
                            <el-link v-on:click="showToggle"><i class="el-icon-menu"/></el-link>
                        </el-tooltip>
                    </template>
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
                        <el-tag size="mini" style="float: left;width: 45px;text-align: center;margin-right: 2px;" effect="dark" :type="tableRowMethodTagClassName(scope.row).css">{{tableRowMethodTagClassName(scope.row).title}}</el-tag>
                        <el-tag size="mini" style="float: left;width: 65px;text-align: center;" :type="tableRowStatusTagClassName(scope.row).css">{{tableRowStatusTagClassName(scope.row).title}}</el-tag>
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
            <el-tree id="directory-list" :default-expand-all="true" v-show="directoryShow"
                     node-key="id" :data="directoryList" :props="defaultProps" @node-click="treeClick">
            </el-tree>
        </template>
        <template slot="paneR">
            <split-pane v-on:resize="handleHorizontalSplitResize" :min-percent='30' :default-percent='horizontalPanelPercent' split="horizontal">
                <template slot="paneL">
                    <RequestPanel ref="listRequestPanel"
                                  v-bind:header-data="headerData"
                                  v-bind:request-body="requestBody"
                                  @onRun="handleRun"
                                  @onHeaderChange="(data)=> { this.headerData = data}"
                                  @onRequestBodyChange="(data)=> { this.requestBody = data}"/>
                </template>
                <template slot="paneR">
                    <ResponsePanel ref="listResponsePanel"
                                   :response-body="responseBody" :on-edit-page="false" :result-type="responseType"
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
    import {checkRequestBody, errorBox, headerData, methodTagInfo, statusTagInfo} from "../utils/utils"

    export default {
        components: {
            RequestPanel, ResponsePanel
        },
        mounted() {
            this.handleSplitResize(this.verticalPanelPercent, this.horizontalPanelPercent);
            //
            const self = this;
            this._resize = () => {
                return (() => {
                    self.handleSplitResize(self.verticalPanelPercent, self.horizontalPanelPercent);
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
            treeClick(obj, node, e) {
                this.apiSearch = obj.label;
                this.showToggle();
            },
            showToggle(e) {
                this.directoryShow = !this.directoryShow;
                let path = [];
                this.tableData.forEach(function (item, index) {
                    path.push(item.path.replace(/^http[s]?:\/\//, "").replace(/\w*:?\w*/, "").replace(/\/$/, ""));
                });
                this.directoryList = this.buildChild(path, "");
                this.directoryList.splice(0, 0, {"label": "/"})
            },
            buildChild(pathList, label) {
                let tree = [];
                let labelKeySet = new Set();

                pathList.forEach(path => {
                    if (path.indexOf(label) != 0) {
                        return;
                    }

                    let subPath = path.substring(path.indexOf(label) + label.length);
                    if (subPath.lastIndexOf("/") <= 0) {
                        return;
                    }
                    let subLabel = label + subPath.substring(0, subPath.indexOf("/", 1));
                    if (labelKeySet.has(subLabel)) {
                        return;
                    }
                    labelKeySet.add(subLabel);
                    tree.push({
                        "label": subLabel,
                        "children": this.buildChild(pathList, subLabel)
                    });
                });

                return tree;
            },
            // 面板大小改变
            handleVerticalSplitResize(data) {
                this.handleSplitResize(data, this.horizontalPanelPercent);
            },
            handleHorizontalSplitResize(data) {
                this.handleSplitResize(this.verticalPanelPercent, data);
            },
            handleSplitResize(verticalPercent, horizontalPercent) {
                this.verticalPanelPercent = verticalPercent;
                this.horizontalPanelPercent = horizontalPercent;
                let verticalDataNum = verticalPercent / 100;
                let horizontalDataNum = horizontalPercent / 100;
                let widthSize = document.documentElement.clientWidth * verticalDataNum;
                let heightSize = document.documentElement.clientHeight - 60;
                //
                this.$refs.listRequestPanel.doLayout(heightSize * horizontalDataNum, widthSize);
                this.$refs.listResponsePanel.doLayout(heightSize * (1 - horizontalDataNum) + 10, widthSize);
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
            tableRowStatusTagClassName(row) {
                return statusTagInfo(row.status);
            },
            tableRowMethodTagClassName(row) {
                return methodTagInfo(row.select);
            },
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
                    "headers": {
                        ...headerData(this.headerData),
                        "X-InterfaceUI-Info": "true"
                    },
                    "data": JSON.parse(this.requestBody)
                }, response => {
                    self.responseType = response.dataTypeMode
                    if (response.dataTypeMode === 'json') {
                        self.responseBody = JSON.stringify(response.data, null, 2);
                    } else {
                        self.responseBody = response.data.result;
                    }
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
                verticalPanelPercent: 50,
                horizontalPanelPercent: 50,
                loading: false,
                //
                apiSearch: '',
                tableData: [],
                directoryShow: false,
                directoryList: [],
                defaultProps: {
                    children: 'children',
                    label: 'label'
                },
                //
                headerData: [],
                requestApiInfo: {},
                requestBody: '{}',
                responseBody: '"empty."',
                responseType: 'json'
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
    #directory-list {
        top: 30px;
        left: -4px;
        position: absolute;
        width: 100%;
        height: 100%;
        z-index: 100;
        background: #fff;
    }

    .el-tree-node__content {
        border-bottom: 1px solid #ccc;
        line-height: 30px;
        background-color: #fbfbfb99;
    }

    /*.el-tree-node:nth-of-type(odd){
        background-color: #FAFAFA;
    }*/
    /*.has-gutter th:first-of-type{
        background-color: #ccc;
    }*/
</style>
