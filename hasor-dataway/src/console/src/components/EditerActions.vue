<template>
    <div style="display: inline;">
        <el-button-group>
            <!-- 保存 -->
            <el-tooltip class="item" effect="dark" content="Save" placement="bottom-end">
                <el-button size="mini" round @click.native="handleSaveAction" :disabled="disabledBtn('saveAction')">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#iconsave"/>
                    </svg>
                </el-button>
            </el-tooltip>
            <!-- 执行 -->
            <el-tooltip class="item" effect="dark" content="Execute Query" placement="bottom-end">
                <el-button size="mini" round @click.native="handleExecuteAction" :disabled="disabledBtn('executeAction')">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#iconexecute"/>
                    </svg>
                </el-button>
            </el-tooltip>
            <!-- 冒烟 -->
            <el-tooltip class="item" effect="dark" content="Smoke Test" placement="bottom-end">
                <el-button size="mini" round @click.native="handleTestAction" :disabled="disabledBtn('testAction')">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icontest"/>
                    </svg>
                </el-button>
            </el-tooltip>
            <!-- 发布 -->
            <el-tooltip class="item" effect="dark" content="Publish" placement="bottom-end">
                <el-button size="mini" round @click.native="handlePublishAction" :disabled="disabledBtn('publishAction')">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#iconrelease"/>
                    </svg>
                </el-button>
            </el-tooltip>
        </el-button-group>
        <div style="padding-left: 10px;display: inline;"/>
        <el-button-group>
            <!-- 历史 -->
            <el-tooltip class="item" effect="dark" content="Release History List" placement="bottom-end">
                <el-button size="mini" round @click.native="handleHistoryAction" :disabled="disabledBtn('historyAction')" v-popover:releaseHistoryPopover>
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#iconhistory"/>
                    </svg>
                </el-button>
            </el-tooltip>
            <!-- 下线 -->
            <el-tooltip class="item" effect="dark" content="Disable the published Api." placement="bottom-end">
                <el-button size="mini" round @click.native="handleDisableAction" :disabled="disabledBtn('disableAction')"
                           v-if="apiInfo.apiStatus===1 || apiInfo.apiStatus===2">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icondisable"/>
                    </svg>
                </el-button>
            </el-tooltip>
            <!-- 删除 -->
            <el-tooltip class="item" effect="dark" content="Permanently delete the Api but keep release history." placement="bottom-end">
                <el-button size="mini" round @click.native="handleDeleteAction" :disabled="disabledBtn('deleteAction')"
                           v-if="apiInfo.apiStatus===0 || apiInfo.apiStatus===3">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icondelete"/>
                    </svg>
                </el-button>
            </el-tooltip>
        </el-button-group>
        <div style="display: block;position: absolute;z-index: 1000;">
            <el-popover ref="releaseHistoryPopover" placement="bottom" title="History Version" width="250">
                <el-timeline style="max-height: 300px;overflow-y: scroll; padding-top: 5px;">
                    <el-timeline-item v-for="history in historyList" v-bind:key="history.historyId" :hide-timestamp="true" size="large">
                        <span>{{history.time}}</span>
                        <el-button size="mini" circle @click.native="handleRecoverAction(history.historyId)" icon="el-icon-edit" style="float:right;margin-top: 5px;"/>
                    </el-timeline-item>
                </el-timeline>
            </el-popover>
        </div>
    </div>
</template>
<script>
    import request from "../utils/request";
    import {ApiUrl} from "../utils/api-const";
    import {checkRequestBody, headerData} from "../utils/utils";

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
                if ('saveAction' === btnName) {
                    return this.newMode ? false : this.apiInfo.editorSubmitted;
                }
                if ('executeAction' === btnName) {
                    return false;
                }
                if ('testAction' === btnName) {
                    return this.newMode ||
                        (this.apiInfo.editorSubmitted && this.apiInfo.apiStatus === 1) ||
                        !(this.apiInfo.editorSubmitted && this.apiInfo.apiStatus !== 1 && !this.smokeTest);
                }
                if ('publishAction' === btnName) {
                    return this.newMode ||
                        !(this.apiInfo.apiStatus !== 1 && this.smokeTest);
                }
                if ('historyAction' === btnName) {
                    return this.newMode;
                }
                if ('disableAction' === btnName) {
                    return this.newMode ||
                        !(this.apiInfo.apiStatus === 1 || this.apiInfo.apiStatus === 2);
                }
                if ('deleteAction' === btnName) {
                    return this.newMode;
                }
                return false;
            },
            // 保存按钮
            handleSaveAction() {
                debugger
                const self = this;
                request(ApiUrl.apiSave + "?id=" + self.apiInfo.apiID, {
                    "method": "POST",
                    "data": {
                        "id": self.apiInfo.apiID,
                        "select": self.apiInfo.select,
                        "apiPath": self.apiInfo.apiPath,
                        "comment": self.apiInfo.comment,
                        "codeType": self.apiInfo.codeType,
                        "codeValue": self.apiInfo.codeValue,
                        "requestBody": self.requestBody,
                        "headerData": self.requestHeader,
                        "optionInfo": self.optionInfo
                    }
                }, response => {
                    if (!self.newMode) {
                        self.$message({message: 'Save successfully.', type: 'success'});
                        self.$emit('onAfterSave', self.apiInfo.apiStatus, response.data.status);
                    } else {
                        this.$router.push("/edit/" + response.data.result);
                    }
                });
            },
            // 执行按钮
            handleExecuteAction() {
                debugger
                // test
                let testResult = checkRequestBody(this.apiInfo.select, this.apiInfo.codeType, this.requestBody);
                if (!testResult) {
                    return;
                }
                //
                const self = this;
                request(ApiUrl.perform + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "headers": headerData(this.requestHeader),
                    "data": {
                        "id": self.apiInfo.apiID,
                        "select": self.apiInfo.select,
                        "apiPath": self.apiInfo.apiPath,
                        "codeType": self.apiInfo.codeType,
                        "codeValue": self.apiInfo.codeValue,
                        "requestBody": JSON.parse(self.requestBody),
                        "optionInfo": self.optionInfo
                    }
                }, response => {
                    self.$emit('onExecute', response.data.result);
                });
            },
            // 冒烟按钮
            handleTestAction() {
                // test
                let testResult = checkRequestBody(this.apiInfo.select, this.apiInfo.codeType, this.requestBody);
                if (!testResult) {
                    return;
                }
                //
                const self = this;
                request(ApiUrl.smokeTest + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "headers": headerData(this.requestHeader),
                    "data": {
                        "id": this.apiInfo.apiID,
                        "requestBody": JSON.parse(this.requestBody),
                    }
                }, response => {
                    this.smokeTest = true;
                    self.$emit('onSmokeTest', response.data.result);
                });
            },
            // 发布按钮
            handlePublishAction() {
                const self = this;
                request(ApiUrl.publish + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "data": {
                        "id": self.apiInfo.apiID,
                    }
                }, response => {
                    self.$emit('onPublish', response.data.result);
                });
            },
            // 历史按钮
            handleHistoryAction() {
                const self = this;
                request(ApiUrl.apiHistory + "?id=" + this.apiInfo.apiID, {
                    "method": "GET",
                }, response => {
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
                request(ApiUrl.disable + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "data": {
                        "id": self.apiInfo.apiID,
                    }
                }, response => {
                    self.$emit('onDisable', response.data.result);
                });
            },
            // 删除按钮
            handleDeleteAction() {
                this.$emit('onDelete', this.apiInfo.apiID);
            }
        },
        data() {
            return {
                smokeTest: false,
                historyList: []
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