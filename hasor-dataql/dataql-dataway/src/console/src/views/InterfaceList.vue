<template>
    <SplitPane :min-percent='30' :default-percent='30' split="vertical">
        <template slot="paneL">
            <el-table ref="interfaceTable" height="100%"
                      :data="tableData.filter(dat => !apiSearch || dat.path.toLowerCase().includes(apiSearch.toLowerCase()))"
                      @current-change="handleApiDataChange" empty-text="No Data"
                      highlight-current-row border lazy stripe>
                <el-table-column prop="id" width="24" :resizable='false'>
                    <template slot-scope="scope">
                        <el-checkbox name="type" v-model="scope.row.checked" v-on:change="handleApiDataChange(scope.row)"/>
                    </template>
                </el-table-column>
                <el-table-column prop="path" label="Api" :show-overflow-tooltip="true">
                    <template slot="header" slot-scope="scope">
                        <el-input size="mini" v-model="apiSearch" placeholder="search Api"/>
                    </template>
                    <template slot-scope="scope">
                        <span>{{scope.row.path}}</span>
                        <el-tag size="mini" style="float: right" :type="tableRowTagClassName(scope.row).css">{{tableRowTagClassName(scope.row).title}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="id" width="23" :resizable='false'>
                    <template slot-scope="scope">
                        <router-link :to="'/edit/' + scope.row.id">
                            <el-link><i class="el-icon-edit"/></el-link>
                        </router-link>
                    </template>
                </el-table-column>
            </el-table>
        </template>
        <template slot="paneR">
            <split-pane v-on:resize="handleSplitResize" :min-percent='30' :default-percent='panelPercent' split="horizontal">
                <template slot="paneL">
                    <RequestPanel id="listRequestPanel" ref="listRequestPanel"
                                  :header-data="headerData"
                                  :request-body="requestBody"
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

    export default {
        components: {
            RequestPanel, ResponsePanel
        },
        methods: {
            // 面板大小改变，重新计算CodeMirror的高度
            handleSplitResize(data) {
                this.panelPercent = data
                let dataNum = data / 100
                let size = document.documentElement.clientHeight - 60
                //
                this.$refs.listRequestPanel.doLayout(size * dataNum)
                this.$refs.listResponsePanel.doLayout(size * (1 - dataNum) + 10)
            },
            // 选择了 Api 中的一个，确保只能选一个
            handleApiDataChange(row) {
                for (let i = 0; i < this.tableData.length; i++) {
                    this.tableData[i].checked = row.id === this.tableData[i].id
                }
            },
            // 执行调用
            handleRun() {
                this.$message({message: 'Run ->' + this.requestBody, type: 'success'})
            },
            //
            tableRowTagClassName(row) {
                if (row.status === 0) {
                    return {'css': 'info', 'title': 'Editor'}
                }
                if (row.status === 1) {
                    return {'css': 'success', 'title': 'Published'}
                }
                if (row.status === 2) {
                    return {'css': 'warning', 'title': 'Changes'}
                }
                if (row.status === 3) {
                    return {'css': 'danger', 'title': 'Disable'}
                }
                return {'css': '', 'title': ''}
            }
        },
        mounted() {
            if (this.tableData || this.tableData.length > 0) {
                this.tableData[0].checked = true
                this.$refs.interfaceTable.setCurrentRow(this.tableData[0])
            }
            this.handleSplitResize(this.panelPercent)
            //
            let _this = this
            this._resize = () => {
                return (() => {
                    _this.handleSplitResize(_this.panelPercent)
                })()
            }
            window.addEventListener('resize', this._resize)
        },
        beforeDestroy() {
            window.removeEventListener('resize', this._resize)
        },
        data() {
            return {
                headerPanelHeight: '100%',
                panelPercent: 50,
                //
                apiSearch: '',
                requestBody: '{}',
                responseBody: '"empty."',
                //
                //
                tableData: [
                    {id: 1, checked: false, path: '/demos/db/show_tables/', status: 0, desc: '现实所有表。'}, // 编辑中 0
                    {id: 2, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'}, // 已发布 1
                    {id: 3, checked: false, path: '/demos/db/show_tables/', status: 2, desc: '现实所有表。'}, // 有变更 2
                    {id: 4, checked: false, path: '/demos/db/show_tables/', status: 3, desc: '现实所有表。'}, // 不可用 3
                    {id: 5, checked: false, path: '/demos/db/show_tables/', status: 0, desc: '现实所有表。'},
                    {id: 6, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
                    {id: 7, checked: false, path: '/demos/db/show_tables/', status: 2, desc: '现实所有表。'},
                    {id: 8, checked: false, path: '/demos/db/show_tables/', status: 3, desc: '现实所有表。'},
                    {id: 9, checked: false, path: '/demos/db/show_tables/', status: 0, desc: '现实所有表。'},
                    {id: 0, checked: false, path: '/demos/db/show_tables/', status: 1, desc: '现实所有表。'},
                    {id: 11, checked: false, path: '/demos/db/show_tables/', status: 2, desc: '现实所有表。'},
                    {id: 12, checked: false, path: '/demos/db/show_tables/', status: 3, desc: '现实所有表。'},
                    {id: 13, checked: false, path: '/demos/db/show_tables/', status: 0, desc: '现实所有表。'}
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

</style>
