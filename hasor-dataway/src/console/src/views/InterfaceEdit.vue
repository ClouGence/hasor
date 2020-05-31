<template>
    <div>
        <div class="monacoEditorHeader">
            <div style="width: 50%; margin-top: 2px; display: inline-table;">
                <el-tooltip class="item" effect="dark" placement="bottom" :content="apiInfo.comment || defaultComment" :disabled="showComment">
                    <el-input placeholder="the path to access this Api" v-model="apiInfo.apiPath" class="input-with-select" size="mini" :disabled="!newCode">
                        <el-select v-model="apiInfo.select" slot="prepend" placeholder="Choose" :disabled="!newCode" style="width: 90px;">
                            <el-option label="POST" value="POST"/>
                            <el-option label="PUT" value="PUT"/>
                            <el-option label="GET" value="GET"/>
                            <el-option label="DELETE" value="DELETE"/>
                        </el-select>
                        <el-button slot="append" icon="el-icon-info" @click.native="handleShowComment"/>
                    </el-input>
                </el-tooltip>
            </div>
            <div class="comment" v-if="showComment">
                <el-input placeholder="Api's comment." size="mini" v-model="apiInfo.comment" @input="handleCommentOnchange">
                    <template slot="prepend">Comment</template>
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
                               :option-info="optionData"
                               @onAfterSave="onAfterSave" @onPublish="onAfterSave" @onDisable="onAfterSave"
                               @onExecute="onExecute" @onSmokeTest="onExecute"
                               @onRecover="onRecover" @onDelete="onDelete"/>
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
                            <RequestPanel ref="editerRequestPanel"
                                          :header-data="headerData"
                                          :request-body="requestBody"
                                          :hide-run-btn="true"
                                          @onHeaderChange="(data)=> { this.headerData = data}"
                                          @onRequestBodyChange="(data)=> { this.requestBody = data}"/>
                        </template>
                        <template slot="paneR">
                            <ResponsePanel ref="editerResponsePanel"
                                           :response-body="responseBody"
                                           :on-edit-page="true"
                                           :result-type="responseType"
                                           :result-structure="optionData['resultStructure']"
                                           :response-format="optionData['responseFormat']"
                                           @onResponseBodyChange="(data)=> { this.responseBody = data}"
                                           @onResultStructureChange="(data) => {this.optionData['resultStructure'] = data}"
                                           @onResultStructureFormatChange="(data) => {this.optionData['responseFormat'] = data}"/>
                        </template>
                    </SplitPane>
                </template>
            </SplitPane>
        </div>
    </div>
</template>
<script>
    import EditerActions from '../components/EditerActions';
    import RequestPanel from '../components/RequestPanel';
    import ResponsePanel from '../components/ResponsePanel';
    import request from "../utils/request";
    import {apiBaseUrl, ApiUrl} from "../utils/api-const";
    import {errorBox, statusTagInfo} from "../utils/utils"
    import {defineMonacoEditorFoo, loadMonacoEditorSelfTheme} from "../utils/editorUtils"

    let defaultOptionData = {
        resultStructure: true
    };

    export default {
        components: {
            RequestPanel, ResponsePanel, EditerActions
        },
        mounted() {
            if (this.$route.path.startsWith('/new')) {
                this.apiInfo.apiID = -1;
                this.apiInfo.apiPath = this.apiBaseUrl;
                this.newCode = true;
                this.showComment = false; // 新增模式下也隐藏 备注输入框
            } else {
                this.apiInfo.apiID = this.$route.params.id;
                this.newCode = false;
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
        watch: {
            'headerData': {
                handler(val, oldVal) {
                    this.handleCommentOnchange();
                },
                deep: true
            },
            'requestBody': {
                handler(val, oldVal) {
                    this.handleCommentOnchange();
                }
            },
            'optionData': {
                handler(val, oldVal) {
                    this.handleCommentOnchange();
                },
                deep: true
            }
        },
        methods: {
            // 页面大小调整
            layoutMonacoEditor() {
                this.panelHeight = document.documentElement.clientHeight - 88;
                let monacoEditorWidth = (document.documentElement.clientWidth * (this.panelPercentVertical / 100))
                this.monacoEditor.layout({
                    height: this.panelHeight,
                    width: monacoEditorWidth
                });
                //
                this.panelPercent = this.panelPercentHorizontal;
                let dataNum = this.panelPercentHorizontal / 100;
                let heightSize = document.documentElement.clientHeight - 88;
                let widthSize = document.documentElement.clientWidth - monacoEditorWidth - 2;
                this.$refs.editerRequestPanel.doLayout(heightSize * dataNum, widthSize);
                this.$refs.editerResponsePanel.doLayout(heightSize * (1 - dataNum), widthSize);
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
            // 显示隐藏Comment
            handleShowComment() {
                this.showComment = !this.showComment;
            },
            handleCommentOnchange() {
                this.apiInfo.editorSubmitted = false;
            },
            //
            // 初始化编辑器
            initMonacoEditor() {
                loadMonacoEditorSelfTheme();
                this.monacoEditor = defineMonacoEditorFoo(this.$refs.container, {
                    value: this.apiInfo.codeValue,
                    language: 'javascript',
                    theme: 'selfTheme'
                });
                this.monacoEditor.updateOptions({contextmenu: true});
                //
                // let contextmenu = this.monacoEditor.getContribution('editor.contrib.contextmenu')
                // let actions = this.monacoEditor.getActions()
                const self = this;
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
                    self.apiInfo.codeValue = data.codeInfo.codeValue || "";
                    //
                    self.requestBody = data.codeInfo.requestBody || "{}";
                    self.headerData = data.codeInfo.headerData || [];
                    self.optionData = {
                        ...defaultOptionData,
                        ...data.optionData
                    };
                    //
                    self.tagInfo = statusTagInfo(self.apiInfo.apiStatus);
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
                    if (this.newCode && this.monacoEditor.getValue().trim() === '-- a new Query.\nselect #{message};') {
                        this.monacoEditor.setValue('// a new Query.\nreturn ${message};');
                    }
                }
                if (this.apiInfo.codeType.toLowerCase() === 'sql') {
                    this.monacoEditor.updateOptions({language: 'sql'});
                    if (this.newCode && this.monacoEditor.getValue().trim() === '// a new Query.\nreturn ${message};') {
                        this.monacoEditor.setValue('-- a new Query.\nselect #{message};');
                    }
                }
            },
            //
            onAfterSave() {
                const self = this;
                this.$nextTick(function () {
                    self.loadApiDetail();
                });
            },
            onExecute(resultValue, dataTypeMode) {
                this.responseType = dataTypeMode;
                if (dataTypeMode === 'json') {
                    this.responseBody = JSON.stringify(resultValue, null, 2);
                } else {
                    this.responseBody = resultValue;
                }
                const self = this;
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
                    self.apiInfo.codeValue = data.codeInfo.codeValue || "";
                    self.requestBody = data.codeInfo.requestBody || "{}";
                    self.headerData = data.codeInfo.headerData || [];
                    self.optionData = {
                        ...defaultOptionData,
                        ...data.optionData
                    };
                    //
                    self.loadEditorMode();
                    self.$nextTick(function () {
                        self.monacoEditor.setValue(self.apiInfo.codeValue);
                        self.apiInfo.editorSubmitted = false;
                        self.$refs.editerRequestPanel.doUpdate();
                        self.$refs.editerResponsePanel.doUpdate();
                    });
                });
            },
            onDelete(apiId) {
                const self = this;
                request(ApiUrl.deleteApi + "?id=" + apiId, {
                    "method": "POST",
                    "data": {
                        "id": apiId,
                    }
                }, response => {
                    if (response.data.result) {
                        self.$message({message: 'Api Delete finish.', type: 'success'});
                        this.$router.push("/");
                    } else {
                        errorBox('result is false.');
                    }
                });
            },
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
                    codeValue: '// a new Query.\nreturn ${message};',
                    editorSubmitted: true,
                },
                //
                tagInfo: {css: 'info', title: 'Editor'},
                defaultComment: "There is no comment, Click 'info' icon to add comment",
                showComment: false,
                newCode: false,
                apiBaseUrl: apiBaseUrl('/'),
                //
                //
                headerData: [],
                optionData: defaultOptionData,
                requestBody: '{"message":"Hello DataQL."}',
                responseBody: '"empty."',
                responseType: 'json',
                //
                //
                panelPercentVertical: 50,
                panelPercentHorizontal: 50,
                panelHeight: '100%'
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