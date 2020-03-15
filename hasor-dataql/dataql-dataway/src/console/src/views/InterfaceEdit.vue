<template>
    <div>
        <div class="monacoEditorHeader">
            <div style="width: 50%; margin-top: 2px; display: inline-table;">
                <el-input placeholder="the path to access this Api" v-model="apiPath" class="input-with-select" size="mini" :disabled="!apiPathEditor">
                    <el-select v-model="select" slot="prepend" placeholder="Choose" :disabled="!apiPathEditor">
                        <el-option label="POST" value="POST"/>
                        <el-option label="PUT" value="PUT"/>
                        <el-option label="GET" value="GET"/>
                    </el-select>
                    <el-button slot="append" icon="el-icon-edit" v-if="!apiPathEditor" @click.native="handleEnableEditPath"/>
                    <el-button slot="append" icon="el-icon-check" v-if="apiPathEditor" @click.native="handleModifyEditPath"/>
                    <el-button slot="append" icon="el-icon-close" v-if="apiPathEditor" @click.native="handleCancelEditPath"/>
                </el-input>
            </div>
            <div style="display: inline-table;padding-left: 5px;">
                <el-radio-group v-model="codeType" size="mini">
                    <el-radio border label="DataQL"/>
                    <el-radio border label="SQL"/>
                </el-radio-group>
            </div>
            <div style="float: right;">
                <el-button-group>
                    <!-- 下线 -->
                    <!--          <el-button size="mini" type="danger" icon="iconfont iconjinyong"/>-->
                    <!-- 删除 -->
                    <el-button size="mini" type="danger" icon="iconfont iconshanchu" plain/>
                    <!-- 历史 -->
                    <el-button size="mini" type="primary" icon="iconfont iconlishi-copy" plain/>
                    <!-- 保存 -->
                    <el-button size="mini" type="primary" icon="iconfont iconsave"/>
                    <!-- 执行 -->
                    <el-button size="mini" type="primary" icon="el-icon-s-promotion"/>
                    <!-- 发布 -->
                    <el-button size="mini" type="primary" icon="iconfont iconrelease"/>
                    <!-- 冒烟 -->
                    <!--          <el-button size="mini" type="primary" icon="iconfont iconceshi3"/>-->
                    <!-- 格式化 -->
                    <!--          <el-button size="mini" type="primary" icon="el-icon-s-open"/>-->
                </el-button-group>
            </div>
        </div>
        <el-divider/>
        <div :style="{height: panelHeight + 'px'}">
            <SplitPane v-on:resize="handleVerticalSplitResize" :min-percent='10' :default-percent='panelPercentVertical' split="vertical">
                <template slot="paneL">
                    <div ref="container"/>
                </template>
                <template slot="paneR">
                    <SplitPane v-on:resize="handleHorizontalSplitResize" :min-percent='10' :default-percent='panelPercentHorizontal' split="horizontal">
                        <template slot="paneL">
                            <RequestPanel id="editerRequestPanel" ref="editerRequestPanel"
                                          :header-data="headerData"
                                          :request-body="requestBody"
                                          :hide-run-btn="true"
                                          @onHeaderChange="(data)=> { this.headerData = data}"
                                          @onRequestBodyChange="(data)=> { this.requestBody = data}"/>
                        </template>
                        <template slot="paneR">
                            <ResponsePanel id="editerResponsePanel" ref="editerResponsePanel"
                                           :response-body="responseBody"
                                           @onResponseBodyChange="(data)=> { this.responseBody = data}"/>
                        </template>
                    </SplitPane>
                </template>
            </SplitPane>
        </div>
    </div>
</template>
<script>
    import * as monaco from 'monaco-editor';
    import RequestPanel from '../components/RequestPanel';
    import ResponsePanel from '../components/ResponsePanel';
    import request from "../utils/request";
    import {ApiUrl} from "../utils/api-const";

    export default {
        components: {
            RequestPanel, ResponsePanel
        },
        mounted() {
            this.loading = true;
            this.apiPathEditor = true;
            this.apiID = this.$route.params.id;
            //
            this.loadApiDetail();
            this.initMonacoEditor();
            this.layoutMonacoEditor();
            this._resize = () => {
                return (() => {
                    this.layoutMonacoEditor()
                })();
            };
            window.addEventListener('resize', this._resize);
        },
        beforeDestroy() {
            window.removeEventListener('resize', this._resize)
        },
        methods: {
            // 页面大小调整
            layoutMonacoEditor() {
                this.panelHeight = document.documentElement.clientHeight - 88;
                this.monacoEditor.layout({
                    height: this.panelHeight,
                    width: (document.documentElement.clientWidth * (this.panelPercentVertical / 100))
                });
                //
                this.panelPercent = this.panelPercentHorizontal;
                let dataNum = this.panelPercentHorizontal / 100;
                let size = document.documentElement.clientHeight - 88;
                this.$refs.editerRequestPanel.doLayout(size * dataNum);
                this.$refs.editerResponsePanel.doLayout(size * (1 - dataNum));
            },
            handleVerticalSplitResize(data) {
                this.panelPercentVertical = data;
                this.layoutMonacoEditor();
            },
            handleHorizontalSplitResize(data) {
                this.panelPercentHorizontal = data;
                this.layoutMonacoEditor();
            },
            //
            // 激活编辑路径
            handleEnableEditPath() {
                this.apiPathEditor = true;
                this.tempPathInfo = {
                    select: this.select,
                    apiPath: this.apiPath,
                };
            },
            // 取消路径编辑
            handleCancelEditPath() {
                this.select = this.tempPathInfo.select;
                this.apiPath = this.tempPathInfo.apiPath;
                this.apiPathEditor = false;
            },
            // 递交路径修改的内容
            handleModifyEditPath() {
                if (this.apiPath === this.tempPathInfo.apiPath && this.select === this.tempPathInfo.select) {
                    this.apiPathEditor = false;
                    this.$message({message: 'Api path has not changed.', type: 'success'})
                    return
                }
                //
                request(ApiUrl.modifyPath + "?id=" + this.apiID, {
                    "method": "POST",
                    "data": {
                        "id": this.apiID,
                        "newPath": this.apiPath,
                        "newSelect": this.select
                    }
                }, response => {
                    if (response.data.result) {
                        this.apiPathEditor = false;
                        this.$message({message: 'Api path modified successfully.', type: 'success'})
                    } else {
                        this.$alert(response.data.message, 'Failed', {
                            confirmButtonText: 'OK'
                        });
                    }
                });
            },
            //
            // 初始化编辑器
            initMonacoEditor() {
                this.monacoEditor = monaco.editor.create(this.$refs.container, {
                    value: this.codeValue,
                    language: 'javascript',
                    theme: 'vs', // vs, hc-black, or vs-dark
                    editorOptions: this.monacoEditorOptions
                });
                //
                monaco.editor.defineTheme('selfTheme', {
                    base: 'vs',
                    inherit: true,
                    rules: [],
                    colors: {
                        'editor.lineHighlightBackground': '#fff8c5'
                    }
                });
                monaco.editor.setTheme('selfTheme');
                //
                // let contextmenu = this.monacoEditor.getContribution('editor.contrib.contextmenu')
                // let actions = this.monacoEditor.getActions()
                this.monacoEditor.updateOptions({contextmenu: false});
                this.monacoEditor.updateOptions({minimap: {enabled: false}});
                let _this = this;
                this.monacoEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
                    _this.codeValue = _this.monacoEditor.getValue();
                });
                // // 自定义键盘事件
                // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function () {
                //   self.$emit('onCommit', self.monacoEditor.getValue(), self.monacoEditor)
                // })
                // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_S, function () {
                //   // 自定义快捷操作
                // })
            },
            // 加载Api的基本信息
            loadApiDetail() {
                const loading = this.$loading({
                    lock: true,
                    text: 'Loading',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                request(ApiUrl.apiDetail + "?id=" + this.apiID, {
                    "method": "GET"
                }, response => {
                    this.select = response.data.select;
                    this.apiPath = response.data.path;
                    this.apiPathEditor = false;
                    this.codeType = response.data.codeType;
                    loading.close();
                    this.loadEditorDetail();
                }, response => {
                    this.$alert('Not Fount Api.', 'Error', {
                        confirmButtonText: 'OK'
                    });
                    loading.close();
                });
            },
            // 加载编辑器信息
            loadEditorDetail() {
                alert('编辑器信息加载.');
                this.codeValue = 'json';
                this.requestBody = '[1,2,3,4]';
                this.headerData = [
                    {checked: true, name: 'name1', value: 'value1'},
                    {checked: false, name: 'name2', value: 'value2'},
                    {checked: false, name: 'name3', value: 'value3'},
                    {checked: true, name: 'name4', value: 'value4'},
                    {checked: true, name: 'name5', value: 'value5'}
                ];
                this.$nextTick(function () {
                    this.$refs.editerRequestPanel.doUpdate();
                });
            }
        },
        data() {
            return {
                apiID: 1,
                select: 'POST',
                apiPath: '',
                apiPathEditor: true,
                tempPathInfo: null,
                codeType: 'DataQL',
                responseBody: '"empty."',
                //
                //
                codeValue: '',
                requestBody: '{}',
                headerData: [],
                //
                //
                panelPercentVertical: 70,
                panelPercentHorizontal: 50,
                panelHeight: '100%',
                monacoEditorOptions: {
                    selectOnLineNumbers: true,
                    roundedSelection: false,
                    readOnly: false, // 只读
                    cursorStyle: 'line', // 光标样式
                    automaticLayout: false, // 自动布局
                    glyphMargin: true, // 字形边缘
                    useTabStops: false,
                    fontSize: 14, // 字体大小
                    autoIndent: true, // 自动布局
                    contextmenu: false
                    // quickSuggestionsDelay: 500,   //代码提示延时
                }
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    .monacoEditorHeader {
        /*display: flex;*/
        /*justify-content: space-between;*/
        /*justify-items: center;*/
        overflow-x: hidden;
        padding: 5px;
    }

    .el-radio {
        margin-right: 1px;
        width: 85px;
    }

    .el-radio--mini.is-bordered {
        padding: 3px 10px 0 5px;
        height: 25px;
    }

    .el-radio.is-bordered + .el-radio.is-bordered {
        margin-left: 1px;
    }
</style>
