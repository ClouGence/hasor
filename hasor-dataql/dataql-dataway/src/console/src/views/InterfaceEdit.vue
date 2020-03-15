<template>
    <div>
        <div class="monacoEditorHeader">
            <div style="width: 50%; margin-top: 2px; display: inline-table;">
                <el-tooltip class="item" effect="dark" placement="bottom" :content="apiComment || defaultApiComment" :disabled="showComment">
                    <el-input placeholder="the path to access this Api" v-model="apiPath" class="input-with-select" size="mini" :disabled="!apiPathEdit">
                        <el-select v-model="select" slot="prepend" placeholder="Choose" :disabled="!apiPathEdit">
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
                <el-input placeholder="Api's comment." size="mini" v-model="apiComment">
                    <template slot="prepend">Comment</template>
                    <el-button slot="append" icon="el-icon-check" v-if="newCode" @click.native="handleShowComment"/>
                </el-input>
            </div>
            <div style="display: inline-table;padding-left: 5px;">
                <el-radio-group v-model="codeType" size="mini" @change="loadEditorMode">
                    <el-tooltip class="item" effect="dark" placement="bottom" content="DataQL language.">
                        <el-radio border label="DataQL"/>
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" placement="bottom" content="SQL language.">
                        <el-radio border label="SQL"/>
                    </el-tooltip>
                </el-radio-group>
            </div>
            <div style="float: right;">
                <el-button-group>
                    <!-- 保存 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconsave"></use>
                        </svg>
                    </el-button>
                    <!-- 执行 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconexecute"></use>
                        </svg>
                    </el-button>
                    <!-- 发布 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconrelease"></use>
                        </svg>
                    </el-button>
                </el-button-group>
                <div style="padding-left: 10px;display: inline;"/>
                <el-button-group>
                    <!-- 下线 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#icondisable"></use>
                        </svg>
                    </el-button>
                    <!-- 删除 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#icondelete"></use>
                        </svg>
                    </el-button>
                    <!-- 历史 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconhistory"></use>
                        </svg>
                    </el-button>
                    <!-- 冒烟 -->
                    <el-button size="mini" round>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#icontest"></use>
                        </svg>
                    </el-button>
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
            if (this.$route.path.startsWith('/new')) {
                this.newCode = true;
                this.apiPathEdit = true;
                this.showComment = true;
                this.apiID = -1;
            } else {
                this.newCode = false;
                this.apiPathEdit = false;
                this.showComment = false;
                this.apiID = this.$route.params.id;
                this.loadApiDetail();
            }
            //
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
                this.tempPathInfo = {
                    select: this.select,
                    apiPath: this.apiPath,
                    apiComment: this.apiComment
                };
                this.apiPathEdit = true;
                this.showComment = true;
            },
            // 取消路径编辑
            handleCancelEditPath() {
                this.select = this.tempPathInfo.select;
                this.apiPath = this.tempPathInfo.apiPath;
                this.apiComment = this.tempPathInfo.apiComment;
                this.apiPathEdit = false;
                this.showComment = false;
            },
            // 递交路径修改的内容
            handleModifyEditPath() {
                if (this.apiPath.toLowerCase() === this.tempPathInfo.apiPath.toLowerCase() &&
                    this.select === this.tempPathInfo.select &&
                    this.apiComment === this.tempPathInfo.apiComment
                ) {
                    this.apiPathEdit = false;
                    this.showComment = false;
                    this.$message({message: 'Api path has not changed.', type: 'success'});
                    return
                }
                //
                request(ApiUrl.modifyPath + "?id=" + this.apiID, {
                    "method": "POST",
                    "data": {
                        "id": this.apiID,
                        "newPath": this.apiPath.toLowerCase(),
                        "newSelect": this.select.toUpperCase()
                    }
                }, response => {
                    if (response.data.result) {
                        this.apiPathEdit = false;
                        this.showComment = false;
                        this.$message({message: 'Api path modified successfully.', type: 'success'});
                    } else {
                        this.$alert(response.data.message, 'Failed', {
                            confirmButtonText: 'OK'
                        });
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
                    background: 'rgba(0, 0, 0, 0.5)'
                });
                request(ApiUrl.apiDetail + "?id=" + this.apiID, {
                    "method": "GET"
                }, response => {
                    this.select = response.data.select;
                    this.apiPath = response.data.path;
                    this.apiComment = response.data.apiComment;
                    this.apiPathEdit = false;
                    this.codeType = response.data.codeType;
                    this.codeValue = response.data.codeInfo.codeValue;
                    this.requestBody = response.data.codeInfo.requestBody;
                    this.responseBody = response.data.codeInfo.responseBody;
                    this.headerData = response.data.codeInfo.headerData;
                    this.loadEditorMode();
                    loading.close();
                    this.$nextTick(function () {
                        this.monacoEditor.setValue(this.codeValue);
                        this.$refs.editerRequestPanel.doUpdate();
                        this.$refs.editerResponsePanel.doUpdate();
                    });
                }, response => {
                    this.$alert('Not Fount Api.', 'Error', {
                        confirmButtonText: 'OK'
                    });
                    loading.close();
                });
            },
            // 刷新编辑器模式
            loadEditorMode() {
                if (this.codeType.toLowerCase() === 'dataql') {
                    this.monacoEditor.updateOptions({language: 'javascript'});
                }
                if (this.codeType.toLowerCase() === 'sql') {
                    this.monacoEditor.updateOptions({language: 'sql'});
                }
            }
        },
        data() {
            return {
                apiID: 1,
                select: 'POST',
                apiPath: '',
                apiComment: '',
                defaultApiComment: "There is no comment, Click 'info' icon to add comment",
                showComment: false,
                newCode: false,
                apiPathEdit: true,
                tempPathInfo: null,
                codeType: 'DataQL',
                responseBody: '"empty."',
                //
                //
                codeValue: '// a new DataQL Query.\nreturn ${message};',
                requestBody: '{"message":"Hello DataQL."}',
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
        overflow-x: hidden;
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