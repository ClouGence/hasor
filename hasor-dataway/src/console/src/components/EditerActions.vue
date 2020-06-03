<template>
  <div style="display: inline;">
    <el-button-group>
      <!-- 保存 -->
      <el-tooltip class="item" effect="dark" content="Save" placement="bottom-end">
        <el-button size="mini" round :disabled="disabledBtn('saveAction')" @click.native="handleSaveAction">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#iconsave" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 执行 -->
      <el-tooltip class="item" effect="dark" content="Execute Query" placement="bottom-end">
        <el-button size="mini" round :disabled="disabledBtn('executeAction')" @click.native="handleExecuteAction">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#iconexecute" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 冒烟 -->
      <el-tooltip class="item" effect="dark" content="Smoke Test" placement="bottom-end">
        <el-button size="mini" round :disabled="disabledBtn('testAction')" @click.native="handleTestAction">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icontest" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 发布 -->
      <el-tooltip class="item" effect="dark" content="Publish" placement="bottom-end">
        <el-button size="mini" round :disabled="disabledBtn('publishAction')" @click.native="handlePublishAction">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#iconrelease" />
          </svg>
        </el-button>
      </el-tooltip>
    </el-button-group>
    <div style="padding-left: 10px;display: inline;" />
    <el-button-group>
      <!-- 历史 -->
      <el-tooltip class="item" effect="dark" content="Release History List" placement="bottom-end">
        <el-button v-popover:releaseHistoryPopover size="mini" round :disabled="disabledBtn('historyAction')" @click.native="handleHistoryAction">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#iconhistory" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 下线 -->
      <el-tooltip class="item" effect="dark" content="Disable the published Api." placement="bottom-end">
        <el-button
          v-if="apiInfo.apiStatus===1 || apiInfo.apiStatus===2"
          size="mini"
          round
          :disabled="disabledBtn('disableAction')"
          @click.native="handleDisableAction"
        >
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icondisable" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 删除 -->
      <el-tooltip class="item" effect="dark" content="Permanently delete the Api but keep release history." placement="bottom-end">
        <el-button
          v-if="apiInfo.apiStatus===0 || apiInfo.apiStatus===3"
          size="mini"
          round
          :disabled="disabledBtn('deleteAction')"
          @click.native="handleDeleteAction"
        >
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icondelete" />
          </svg>
        </el-button>
      </el-tooltip>
    </el-button-group>
    <div style="display: block;position: absolute;z-index: 1000;">
      <el-popover ref="releaseHistoryPopover" placement="bottom" title="History Version" width="250">
        <el-timeline style="max-height: 300px;overflow-y: scroll; padding-top: 5px;">
          <el-timeline-item v-for="history in historyList" :key="history.historyId" :hide-timestamp="true" size="large">
            <span>{{ history.time }}</span>
            <el-button size="mini" circle icon="el-icon-edit" style="float:right;margin-top: 5px;" @click.native="handleRecoverAction(history.historyId)" />
          </el-timeline-item>
        </el-timeline>
      </el-popover>
    </div>
  </div>
</template>
<script>
    import request from '../utils/request';
    import {ApiUrl} from '../utils/api-const';
    import {checkRequestBody, errorBox, fixGetRequestBody, headerData} from '../utils/utils';

    export default {
        props: {
            apiInfo: {
                type: Object,
                default: function () {
                    return {
                        apiID: 1,
                        select: 'POST',
                        apiPath: '',
                        comment: '',
                        apiStatus: 0,
                        codeType: 'DataQL',
                        codeValue: 'return true;',
                        editorSubmitted: true
                    }
                }
            },
            requestBody: {
                type: String,
                default: function () {
                    return '{}'
                }
            },
            requestHeader: {
                type: Array,
                default: function () {
                    return []
                }
            },
            optionInfo: {
                type: Object,
                default: function () {
                    return {}
                }
            },
            newMode: {
                type: Boolean,
                default: function () {
                    return true
                }
            }
        },
        data() {
            return {
                smokeTest: false,
                historyList: []
            }
        },
        watch: {
            'apiInfo': {
                handler(val, oldVal) {
                    this.smokeTest = false;
                },
                deep: true
            },
            'apiInfo.editorSubmitted': {
                handler(val, oldVal) {
                    if (!this.apiInfo.editorSubmitted) {
                        this.smokeTest = false;
                    }
                }
            }
        },
        methods: {
            disabledBtn(btnName) {
                if (btnName === 'saveAction') {
                    return this.newMode ? false : this.apiInfo.editorSubmitted;
                }
                if (btnName === 'executeAction') {
                    return false;
                }
                if (btnName === 'testAction') {
                    return this.newMode ||
                        (this.apiInfo.editorSubmitted && this.apiInfo.apiStatus === 1) ||
                        !(this.apiInfo.editorSubmitted && this.apiInfo.apiStatus !== 1 && !this.smokeTest);
                }
                if (btnName === 'publishAction') {
                    return this.newMode ||
                        !(this.apiInfo.apiStatus !== 1 && this.smokeTest);
                }
                if (btnName === 'historyAction') {
                    return this.newMode;
                }
                if (btnName === 'disableAction') {
                    return this.newMode ||
                        !(this.apiInfo.apiStatus === 1 || this.apiInfo.apiStatus === 2);
                }
                if (btnName === 'deleteAction') {
                    return this.newMode;
                }
                return false;
            },
            // 保存按钮
            handleSaveAction() {
                const self = this;
                request(ApiUrl.apiSave + '?id=' + self.apiInfo.apiID, {
                    'method': 'POST',
                    'data': {
                        'id': self.apiInfo.apiID,
                        'select': self.apiInfo.select,
                        'apiPath': self.apiInfo.apiPath,
                        'comment': self.apiInfo.comment,
                        'codeType': self.apiInfo.codeType,
                        'codeValue': self.apiInfo.codeValue,
                        'requestBody': self.requestBody,
                        'headerData': self.requestHeader,
                        'optionInfo': self.optionInfo
                    }
                }, response => {
                    if (!response.data.success) {
                        errorBox(`${response.data.code}: ${response.data.message}`);
                        return
                    }
                    if (!self.newMode) {
                        self.$message({message: 'Save successfully.', type: 'success'});
                        self.$emit('onAfterSave', self.apiInfo.apiStatus, response.data.status);
                    } else {
                        this.$router.push('/edit/' + response.data.result);
                    }
                });
            },
            // 执行按钮
            handleExecuteAction() {
                // test
                const testResult = checkRequestBody(this.apiInfo.select, this.apiInfo.codeType, this.requestBody);
                if (!testResult) {
                    return;
                }
                //
                const self = this;
                request(ApiUrl.perform + '?id=' + this.apiInfo.apiID, {
                    'method': 'POST',
                    'headers': {
                        ...headerData(this.requestHeader),
                        'X-InterfaceUI-Info': 'true'
                    },
                    'data': {
                        'id': self.apiInfo.apiID,
                        'select': self.apiInfo.select,
                        'apiPath': self.apiInfo.apiPath,
                        'codeType': self.apiInfo.codeType,
                        'codeValue': self.apiInfo.codeValue,
                        'requestBody': fixGetRequestBody(self.apiInfo.select, self.requestBody),
                        'optionInfo': self.optionInfo
                    }
                }, response => {
                    self.$emit('onExecute', response.data, response.dataTypeMode);
                });
            },
            // 冒烟按钮
            handleTestAction() {
                // test
                const testResult = checkRequestBody(this.apiInfo.select, this.apiInfo.codeType, this.requestBody);
                if (!testResult) {
                    return;
                }
                //
                const self = this;
                request(ApiUrl.smokeTest + '?id=' + this.apiInfo.apiID, {
                    'method': 'POST',
                    'headers': {
                        ...headerData(this.requestHeader),
                        'X-InterfaceUI-Info': 'true'
                    },
                    'data': {
                        'id': this.apiInfo.apiID,
                        'requestBody': fixGetRequestBody(this.apiInfo.select, this.requestBody),
                    }
                }, response => {
                    this.smokeTest = true;
                    self.$emit('onSmokeTest', response.data, response.dataTypeMode);
                });
            },
            // 发布按钮
            handlePublishAction() {
                const self = this;
                request(ApiUrl.publish + '?id=' + this.apiInfo.apiID, {
                    'method': 'POST',
                    'data': {
                        'id': self.apiInfo.apiID,
                    }
                }, response => {
                    if (!response.data.success) {
                        errorBox(`${response.data.code}: ${response.data.message}`);
                        return
                    }
                    self.$emit('onPublish', response.data.result);
                });
            },
            // 历史按钮
            handleHistoryAction() {
                const self = this;
                request(ApiUrl.apiHistory + '?id=' + this.apiInfo.apiID, {
                    'method': 'GET',
                }, response => {
                    if (!response.data.success) {
                        errorBox(`${response.data.code}: ${response.data.message}`);
                        return
                    }
                    self.historyList = response.data.result;
                });
            },
            // 恢复历史的某个版本
            handleRecoverAction(historyId) {
                this.$emit('onRecover', historyId);
            },
            // 禁用按钮
            handleDisableAction() {
                const self = this;
                request(ApiUrl.disable + '?id=' + this.apiInfo.apiID, {
                    'method': 'POST',
                    'data': {
                        'id': self.apiInfo.apiID,
                    }
                }, response => {
                    if (!response.data.success) {
                        errorBox(`${response.data.code}: ${response.data.message}`);
                        return
                    }
                    self.$emit('onDisable', response.data.result);
                });
            },
            // 删除按钮
            handleDeleteAction() {
                this.$emit('onDelete', this.apiInfo.apiID);
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    .is-disabled {
        background-color: #f3f5f9 !important;
        -webkit-filter: grayscale(1); /* Webkit */
        filter: grayscale(1); /* W3C */
    }

    .el-timeline-item {
        padding-bottom: 15px !important;
    }

    .el-timeline-item:hover {
        background-color: #f7f7f7;
        padding-bottom: 15px !important;
    }

    .el-timeline {
        padding-inline-start: 5px !important;
    }
</style>
