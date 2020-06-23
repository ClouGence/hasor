<template>
  <div style="display: inline;">
    <!--    <el-button-group>-->
    <!--      &lt;!&ndash; 设置 &ndash;&gt;-->
    <!--      <el-tooltip class="item" effect="dark" content="More Settings" placement="bottom-end">-->
    <!--        <el-button size="mini" round @click.native="handleMoreAction">-->
    <!--          <svg class="icon" aria-hidden="true">-->
    <!--            <use xlink:href="#iconsave" />-->
    <!--          </svg>-->
    <!--        </el-button>-->
    <!--      </el-tooltip>-->
    <!--    </el-button-group>-->
    <div style="padding-left: 10px;display: inline;" />
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
        <el-button v-if="apiInfo.apiStatus===1 || apiInfo.apiStatus===2" size="mini" round :disabled="disabledBtn('disableAction')"
                   @click.native="handleDisableAction"
        >
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icondisable" />
          </svg>
        </el-button>
      </el-tooltip>
      <!-- 删除 -->
      <el-tooltip class="item" effect="dark" content="Permanently delete the Api but keep release history." placement="bottom-end">
        <el-button v-if="apiInfo.apiStatus===0 || apiInfo.apiStatus===3" size="mini" round :disabled="disabledBtn('deleteAction')"
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
    <!--    <el-drawer :visible.sync="moreConfig" :with-header="false">-->
    <!--      <div style="padding: 20px 10px 0px 10px;">-->
    <!--        <el-form>-->
    <!--          <el-form-item label="Wrap All Parameters">-->
    <!--            <el-switch inactive-text="Enable" active-text="Disabled"></el-switch>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item label="活动名称">-->
    <!--            <el-input size="mini"></el-input>-->
    <!--          </el-form-item>-->
    <!--          <el-form-item>-->
    <!--            <el-button size="mini" type="primary">立即创建</el-button>-->
    <!--            <el-button size="mini">取消</el-button>-->
    <!--          </el-form-item>-->
    <!--        </el-form>-->
    <!--        <el-collapse v-model="drawerConfig.activeNames">-->
    <!--          <el-collapse-item title="Cross" name="1">-->
    <!--            <div>与现实生活一致：与现实生活的流程、逻辑保持一致，遵循用户习惯的语言和概念；</div>-->
    <!--            <div>在界面中一致：所有的元素和结构需保持一致，比如：设计样式、图标和文本、元素的位置等。</div>-->
    <!--          </el-collapse-item>-->
    <!--          <el-collapse-item title="反馈 Feedback" name="2">-->
    <!--            <div>控制反馈：通过界面样式和交互动效让用户可以清晰的感知自己的操作；</div>-->
    <!--            <div>页面反馈：操作后，通过页面元素的变化清晰地展现当前状态。</div>-->
    <!--          </el-collapse-item>-->
    <!--          <el-collapse-item title="效率 Efficiency" name="3">-->
    <!--            <div>简化流程：设计简洁直观的操作流程；</div>-->
    <!--            <div>清晰明确：语言表达清晰且表意明确，让用户快速理解进而作出决策；</div>-->
    <!--            <div>帮助用户识别：界面简单直白，让用户快速识别而非回忆，减少用户记忆负担。</div>-->
    <!--          </el-collapse-item>-->
    <!--          <el-collapse-item title="可控 Controllability" name="4">-->
    <!--            <div>用户决策：根据场景可给予用户操作建议或安全提示，但不能代替用户进行决策；</div>-->
    <!--            <div>结果可控：用户可以自由的进行操作，包括撤销、回退和终止当前操作等。</div>-->
    <!--          </el-collapse-item>-->
    <!--        </el-collapse>-->

    <!--      </div>-->
    <!--    </el-drawer>-->
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
                };
            }
        },
        requestBody: {
            type: String,
            default: function () {
                return '{}';
            }
        },
        requestHeader: {
            type: Array,
            default: function () {
                return [];
            }
        },
        optionInfo: {
            type: Object,
            default: function () {
                return {};
            }
        },
        newMode: {
            type: Boolean,
            default: function () {
                return true;
            }
        }
    },
    data() {
        return {
            smokeTest: false,
            moreConfig: false,
            historyList: [],
            drawerConfig: {
                activeNames: ['1', '2', '3']
            }
        };
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
        // 更多配置按钮
        handleMoreAction() {
            this.moreConfig = true;
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
                    return;
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
                    return;
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
                    return;
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
                    return;
                }
                self.$emit('onDisable', response.data.result);
            });
        },
        // 删除按钮
        handleDeleteAction() {
            this.$emit('onDelete', this.apiInfo.apiID);
        }
    }
};
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
