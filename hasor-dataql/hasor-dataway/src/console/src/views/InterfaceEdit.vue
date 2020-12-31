<template>
  <div>
    <div class="monacoEditorHeader">
      <div style="width: 50%; margin-top: 2px; display: inline-flex;">
        <el-select v-model="apiInfo.select" placeholder="Choose" style="width: 95px;padding-right: 5px;" size="mini">
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="GET" value="GET" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
        <el-tooltip class="item" effect="dark" placement="bottom" :content="apiInfo.comment || defaultComment" :disabled="showComment">
          <el-input v-model="apiInfo.apiPath" placeholder="the path to access this Api" class="input-with-select" size="mini" :disabled="!editerActions.newMode">
            <el-button slot="append" icon="el-icon-info" @click.native="handleShowComment" />
          </el-input>
        </el-tooltip>
      </div>
      <div v-if="showComment" class="comment">
        <el-input v-model="apiInfo.comment" placeholder="Api's comment." size="mini" @input="handleCommentOnchange">
          <template slot="prepend">Comment</template>
        </el-input>
      </div>
      <div style="display: inline-table;padding-left: 5px;">
        <el-radio-group v-model="apiInfo.codeType" size="mini" @change="loadEditorMode">
          <el-tooltip class="item" effect="dark" placement="bottom" content="DataQL language.">
            <el-radio border label="DataQL" />
          </el-tooltip>
          <el-tooltip class="item" effect="dark" placement="bottom" content="SQL language.">
            <el-radio border label="SQL" />
          </el-tooltip>
        </el-radio-group>
      </div>
      <div style="float: right;">
        <EditerActions ref="editerActionsPanel"
                       :api-info="apiInfo" :request-body="requestBody" :request-header="headerData" :action-status="editerActions"
                       :option-info="optionData" @onOptionChange="(data)=> { this.optionData = data}"
                       @onAfterSave="onAfterSave" @onPublish="onAfterSave" @onDisable="onAfterSave"
                       @onExecute="onExecute" @onSmokeTest="onSmokeTest"
                       @onRecover="onRecover" @onDelete="onDelete"
        />
        <div style="display: inline-table;padding-left: 5px;">
          <el-tooltip class="item" effect="dark" placement="top" content="Current Api Status">
            <el-tag size="mini" style="width: 65px;text-align: center;" :type="tagInfo.css">{{ tagInfo.title }}</el-tag>
          </el-tooltip>
        </div>
      </div>
    </div>
    <el-divider />
    <div :style="{height: panelHeight + 'px'}">
      <SplitPane :min-percent="10" :default-percent="panelPercentVertical" split="vertical" @resize="handleVerticalSplitResize">
        <template slot="paneL">
          <div ref="container" />
        </template>
        <template slot="paneR">
          <SplitPane :min-percent="10" :default-percent="panelPercentHorizontal" split="horizontal" @resize="handleHorizontalSplitResize">
            <template slot="paneL">
              <RequestPanel ref="editerRequestPanel"
                            :header-data="headerData" :request-body="requestBody" :hide-run-btn="true" :api-info="apiInfo"
                            :option-info="optionData" @onOptionChange="(data)=> { this.optionData = data}"
                            @onHeaderChange="(data)=> { this.headerData = data}" @onRequestBodyChange="(data)=> { this.requestBody = data}"
              />
            </template>
            <template slot="paneR">
              <ResponsePanel ref="editerResponsePanel"
                             :response-body="responseBody" :on-edit-page="true" :result-type="responseType"
                             :option-info="optionData" @onOptionChange="(data)=> { this.optionData = data}"
                             @onResponseBodyChange="(data)=> { this.responseBody = data}"
              />
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
import request from '../utils/request';
import {apiBaseUrl, ApiUrl, defaultOptionData} from '@/utils/api-const';
import {errorBox, statusTagInfo} from '@/utils/utils';
import {defineMonacoEditorFoo, loadMonacoEditorSelfTheme} from '@/utils/editorUtils';

export default {
    components: {
        RequestPanel, ResponsePanel, EditerActions
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
            },
            //
            //
            editerActions: {
                newMode: false,
                disablePublish: true
            },
            //
            //
            tagInfo: {css: 'info', title: 'Editor'},
            defaultComment: "There is no comment, Click 'info' icon to add comment",
            showComment: false,
            apiBaseUrl: apiBaseUrl('/'),
            //
            //
            headerData: [],
            optionData: { ...defaultOptionData },
            requestBody: '{"message":"Hello DataQL."}',
            responseBody: '"empty."',
            responseType: 'json',
            //
            //
            panelPercentVertical: 50,
            panelPercentHorizontal: 50,
            panelHeight: '100%'
        };
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
    mounted() {
        if (this.$route.path.startsWith('/new')) {
            this.apiInfo.apiID = -1;
            this.apiInfo.apiPath = this.apiBaseUrl;
            this.editerActions.newMode = true;
            this.showComment = false; // 新增模式下也隐藏 备注输入框
        } else {
            this.apiInfo.apiID = this.$route.params.id;
            this.editerActions.newMode = false;
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
            const monacoEditorWidth = (document.documentElement.clientWidth * (this.panelPercentVertical / 100));
            this.monacoEditor.layout({
                height: this.panelHeight,
                width: monacoEditorWidth
            });
            //
            const dataNum = this.panelPercentHorizontal / 100;
            const heightSize = document.documentElement.clientHeight - 88;
            const widthSize = document.documentElement.clientWidth - monacoEditorWidth - 2;
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
            // console.log('handleCommentOnchange -> apiInfo.editorSubmitted = false');
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
            request(ApiUrl.apiDetail + '?id=' + self.apiInfo.apiID, {
                'method': 'GET'
            }, response => {
                // 如果服务端失败那么弹出错误消息，在回到列表页面
                if (!response.data.success) {
                    self.$alert(`${response.data.code}: ${response.data.message}`, 'Error', {
                        confirmButtonText: 'OK.',
                        callback: action => {
                            self.$router.push('/');
                        }
                    });
                    return;
                }
                // 加载 API 信息
                const data = response.data.result;
                self.apiInfo.select = data.select;
                self.apiInfo.apiPath = data.path;
                self.apiInfo.comment = data.apiComment;
                self.apiInfo.apiStatus = data.status;
                self.apiInfo.codeType = data.codeType;
                self.apiInfo.codeValue = data.codeInfo.codeValue || '';
                //
                self.requestBody = data.codeInfo.requestBody || '{}';
                self.headerData = data.codeInfo.headerData || [];
                self.optionData = {
                    ...defaultOptionData,
                    ...data.optionData
                };
                //
                self.tagInfo = statusTagInfo(self.apiInfo.apiStatus);
                self.loadEditorMode();
                self.monacoEditor.setValue(self.apiInfo.codeValue);
                self.editerActions.disablePublish = true;
                // console.log('loadApiDetail -> editerActions.disablePublish = true');
                self.doNextTickUpdate();
            });
        },
        // 刷新编辑器模式
        loadEditorMode() {
            this.editerActions.disablePublish = false;
            // console.log('loadApiDetail -> editerActions.disablePublish = true');
            if (this.apiInfo.codeType.toLowerCase() === 'dataql') {
                this.monacoEditor.updateOptions({language: 'javascript'});
                if (this.editerActions.newMode && this.monacoEditor.getValue().trim() === '-- a new Query.\nselect #{message};') {
                    this.monacoEditor.setValue('// a new Query.\nreturn ${message};');
                }
            }
            if (this.apiInfo.codeType.toLowerCase() === 'sql') {
                this.monacoEditor.updateOptions({language: 'sql'});
                if (this.editerActions.newMode && this.monacoEditor.getValue().trim() === '// a new Query.\nreturn ${message};') {
                    this.monacoEditor.setValue('-- a new Query.\nselect #{message};');
                }
            }
        },
        // 下一个周期更新页面
        doNextTickUpdate() {
            const self = this;
            self.$nextTick(function () {
                self.$refs.editerRequestPanel.doUpdate();
                self.$refs.editerResponsePanel.doUpdate();
                self.$refs.editerActionsPanel.doUpdate();
            });
        },
        //
        onAfterSave() {
            this.loadApiDetail();
        },
        onSmokeTest(resultValue, dataTypeMode) {
            this.onExecute(resultValue, dataTypeMode);
            this.editerActions.disablePublish = false;
            this.doNextTickUpdate();
            // console.log('loadApiDetail -> editerActions.disablePublish = false');
        },
        onExecute(resultValue, dataTypeMode) {
            this.responseType = dataTypeMode;
            if (dataTypeMode === 'json') {
                this.responseBody = JSON.stringify(resultValue, null, 2);
            } else {
                this.responseBody = resultValue;
            }
            this.doNextTickUpdate();
        },
        onRecover(historyId) {
            const self = this;
            request(ApiUrl.apiHistoryInfo + '?id=' + this.apiInfo.apiID + '&historyId=' + historyId, {
                'method': 'GET'
            }, response => {
                const data = response.data.result;
                self.apiInfo.select = data.select;
                self.apiInfo.codeType = data.codeType;
                self.apiInfo.codeValue = data.codeInfo.codeValue || '';
                self.requestBody = data.codeInfo.requestBody || '{}';
                self.headerData = data.codeInfo.headerData || [];
                self.optionData = {
                    ...defaultOptionData,
                    ...data.optionData
                };
                //
                self.loadEditorMode();
                self.monacoEditor.setValue(self.apiInfo.codeValue);
                self.editerActions.disablePublish = false;
                // console.log('loadApiDetail -> editerActions.disablePublish = false');
                //
                this.doNextTickUpdate();
            });
        },
        onDelete(apiId) {
            const self = this;
            request(ApiUrl.deleteApi + '?id=' + apiId, {
                'method': 'POST',
                'data': {
                    'id': apiId,
                }
            }, response => {
                if (response.data.result) {
                    self.$message({message: 'Api Delete finish.', type: 'success'});
                    this.$router.push('/');
                } else {
                    errorBox('result is false.');
                }
            });
        },
    }
};
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
        width: 80px;
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

    .input-with-select .el-input-group__prepend {
        cursor: copy !important;
    }

</style>
