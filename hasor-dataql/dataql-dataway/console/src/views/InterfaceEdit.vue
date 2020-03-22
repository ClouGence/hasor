<template>
    <div>
        <div class="monacoEditorHeader">
            <div style="width: 50%; margin-top: 2px; display: inline-table;">
                <el-tooltip class="item" effect="dark" placement="bottom" :content="apiInfo.comment || defaultComment" :disabled="showComment">
                    <el-input placeholder="the path to access this Api" v-model="apiInfo.apiPath" class="input-with-select" size="mini" :disabled="!apiPathEdit">
                        <el-select v-model="apiInfo.select" slot="prepend" placeholder="Choose" :disabled="!apiPathEdit">
                            <el-option label="POST" value="POST"/>
                            <el-option label="PUT" value="PUT"/>
                            <el-option label="GET" value="GET"/>
                        </el-select>
                        <el-button slot="append" icon="el-icon-edit" v-if="!newCode && !apiPathEdit" @click.native="handleEnableEditPath"/>
                        <el-button slot="append" icon="el-icon-check" v-if="!newCode && apiPathEdit" @click.native="handleModifyEditPath"/>
                        <el-button slot="append" icon="el-icon-close" v-if="!newCode && apiPathEdit" @click.native="handleCancelEditPath"/>
                        <el-button slot="append" icon="el-icon-info" v-if="newCode && !showComment" @click.native="handleShowComment"/>
                    </el-input>
                </el-tooltip>
            </div>
            <div class="comment" v-if="showComment">
                <el-input placeholder="Api's comment." size="mini" v-model="apiInfo.comment">
                    <template slot="prepend">Comment</template>
                    <el-button slot="append" icon="el-icon-check" v-if="newCode" @click.native="handleShowComment"/>
                </el-input>
            </div>
            <div style="display: inline-table;padding-left: 5px;">
                <el-radio-group v-model="apiInfo.codeType" size="mini" @change="loadEditorMode">
                    <el-tooltip class="item" effect="dark" placement="bottom" content="DataQL language.">
                        <el-radio border label="DataQL"/>
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" placement="bottom" content="SQL language.">
                        <el-radio border label="SQL"/>
                    </el-tooltip>
                </el-radio-group>
            </div>
            <div style="float: right;">
                <EditerActions :api-info="apiInfo"
                               :request-body="requestBody"
                               :request-header="headerData"
                               :new-mode="newCode"
                               @onAfterSave="onAfterSave" @onPublish="onAfterSave" @onDisable="onAfterSave"
                               @onExecute="onExecute" @onSmokeTest="onExecute"
                               @onRecover="onRecover"/>
                <div style="display: inline-table;padding-left: 5px;">
                    <el-tooltip class="item" effect="dark" placement="top" content="Current Api Status">
                        <el-tag size="mini" style="width: 65px;text-align: center;" :type="tagInfo.css">{{tagInfo.title}}</el-tag>
                    </el-tooltip>
                </div>
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
    import EditerActions from '../components/EditerActions';
    import RequestPanel from '../components/RequestPanel';
    import ResponsePanel from '../components/ResponsePanel';
    import request from "../utils/request";
    import {ApiUrl} from "../utils/api-const";
    import {tagInfo} from "../utils/utils"

    export default {
        components: {
            RequestPanel, ResponsePanel, EditerActions
        },
        mounted() {
            if (this.$route.path.startsWith('/new')) {
                this.apiInfo.apiID = -1;
                this.newCode = true;
                this.apiPathEdit = true;
                this.showComment = true;
            } else {
                this.apiInfo.apiID = this.$route.params.id;
                this.newCode = false;
                this.apiPathEdit = false;
                this.showComment = false;
                this.loadApiDetail();
            }
            //
            this.initMonacoEditor();
            this.layoutMonacoEditor();
            this._resize = () => {
                return (() => {
                    this.layoutMonacoEditor();
                })();
            };
            window.addEventListener('resize', this._resize);
        },
        beforeDestroy() {
            window.removeEventListener('resize', this._resize);
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
                this.tempPathInfo = {
                    select: this.apiInfo.select,
                    apiPath: this.apiInfo.apiPath,
                    comment: this.apiInfo.comment
                };
                this.apiPathEdit = true;
                this.showComment = true;
            },
            // 取消路径编辑
            handleCancelEditPath() {
                this.apiInfo.select = this.tempPathInfo.select;
                this.apiInfo.apiPath = this.tempPathInfo.apiPath;
                this.apiInfo.comment = this.tempPathInfo.comment;
                this.apiPathEdit = false;
                this.showComment = false;
            },
            // 递交路径修改的内容
            handleModifyEditPath() {
                if (this.apiInfo.apiPath.toLowerCase() === this.tempPathInfo.apiPath.toLowerCase() &&
                    this.apiInfo.select === this.tempPathInfo.select &&
                    this.apiInfo.comment === this.tempPathInfo.comment
                ) {
                    this.apiPathEdit = false;
                    this.showComment = false;
                    this.$message({message: 'Api path has not changed.', type: 'success'});
                    return
                }
                //
                const self = this;
                request(ApiUrl.checkPath + "?id=" + self.apiInfo.apiID, {
                    "method": "POST",
                    "data": {
                        "newPath": self.apiInfo.apiPath.toLowerCase(),
                        "newSelect": self.apiInfo.select.toUpperCase()
                    }
                }, response => {
                    if (response.data.result) {
                        self.apiPathEdit = false;
                        self.showComment = false;
                        self.apiInfo.editorSubmitted = false;
                        self.$message({message: 'Api path verify pass.', type: 'success'});
                    } else {
                        self.$alert('result is false.', 'Failed', {confirmButtonText: 'OK'});
                    }
                });
            },
            // 显示隐藏Comment
            handleShowComment() {
                this.showComment = !this.showComment;
            },
            //
            // 初始化编辑器
            initMonacoEditor() {
                this.monacoEditor = monaco.editor.create(this.$refs.container, {
                    value: this.apiInfo.codeValue,
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
                const self = this;
                // let contextmenu = this.monacoEditor.getContribution('editor.contrib.contextmenu')
                // let actions = this.monacoEditor.getActions()
                this.monacoEditor.updateOptions({contextmenu: false});
                this.monacoEditor.updateOptions({minimap: {enabled: false}});
                this.monacoEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
                    self.apiInfo.codeValue = self.monacoEditor.getValue();
                    self.apiInfo.editorSubmitted = false;
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
                const self = this;
                request(ApiUrl.apiDetail + "?id=" + self.apiInfo.apiID, {
                    "method": "GET"
                }, response => {
                    let data = response.data.result;
                    self.apiInfo.select = data.select;
                    self.apiInfo.apiPath = data.path;
                    self.apiInfo.comment = data.apiComment;
                    self.apiInfo.apiStatus = data.status;
                    self.apiInfo.codeType = data.codeType;
                    self.apiInfo.codeValue = data.codeInfo.codeValue;
                    //
                    self.requestBody = data.codeInfo.requestBody;
                    self.headerData = data.codeInfo.headerData;
                    //
                    self.apiPathEdit = false;
                    self.tagInfo = tagInfo(self.apiInfo.apiStatus);
                    self.loadEditorMode();
                    //
                    self.$nextTick(function () {
                        self.monacoEditor.setValue(self.apiInfo.codeValue);
                        self.apiInfo.editorSubmitted = true;
                        self.$refs.editerRequestPanel.doUpdate();
                        self.$refs.editerResponsePanel.doUpdate();
                    });
                });
            },
            // 刷新编辑器模式
            loadEditorMode() {
                this.apiInfo.editorSubmitted = false;
                if (this.apiInfo.codeType.toLowerCase() === 'dataql') {
                    this.monacoEditor.updateOptions({language: 'javascript'});
                }
                if (this.apiInfo.codeType.toLowerCase() === 'sql') {
                    this.monacoEditor.updateOptions({language: 'sql'});
                }
            },
            //
            onAfterSave() {
                const self = this;
                this.$nextTick(function () {
                    self.loadApiDetail();
                });
            },
            onExecute(resultValue) {
                const self = this;
                this.responseBody = JSON.stringify(resultValue, null, 2);
                self.$nextTick(function () {
                    self.$refs.editerResponsePanel.doUpdate();
                });
            },
            onRecover(historyId) {
                const self = this;
                request(ApiUrl.apiHistoryInfo + "?historyId=" + historyId, {
                    "method": "GET"
                }, response => {
                    let data = response.data.result;
                    self.apiInfo.select = data.select;
                    self.apiInfo.codeType = data.codeType;
                    self.apiInfo.codeValue = data.codeInfo.codeValue;
                    self.requestBody = data.codeInfo.requestBody;
                    self.headerData = data.codeInfo.headerData;
                    //
                    self.loadEditorMode();
                    self.$nextTick(function () {
                        self.monacoEditor.setValue(self.apiInfo.codeValue);
                        self.apiInfo.editorSubmitted = false;
                        self.$refs.editerRequestPanel.doUpdate();
                        self.$refs.editerResponsePanel.doUpdate();
                    });
                });
            }
        },
        data() {
            return {
                apiInfo: {
                    apiID: 1,
                    select: 'POST',
                    apiPath: '',
                    comment: '',
                    apiStatus: 0,
                    codeType: 'DataQL',
                    codeValue: '// a new DataQL Query.\nreturn ${message};',
                    editorSubmitted: true,
                },
                //
                tagInfo: {css: 'info', title: 'Editor'},
                defaultComment: "There is no comment, Click 'info' icon to add comment",
                showComment: false,
                newCode: false,
                apiPathEdit: true,
                tempPathInfo: null,
                //
                //
                headerData: [],
                requestBody: '{"message":"Hello DataQL."}',
                responseBody: '"empty."',
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
        overflow-x: hidden;
        overflow-y: hidden;
        padding: 5px;
    }

    .comment {
        width: 50%;
        position: absolute;
        z-index: 1000;
    }

    .el-radio {
        margin-right: 1px;
        width: 85px;
    }

    .el-radio--mini.is-bordered {
        padding: 3px 10px 0 5px;
        height: 25px;
    }

    .el-input-group__append, .el-input-group__prepend {
        padding: 0 13px !important;
    }

    .el-radio.is-bordered + .el-radio.is-bordered {
        margin-left: 1px;
    }
</style>