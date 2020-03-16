<template>
    <div style="display: inline;">
        <el-button-group>
            <!-- 保存 -->
            <el-button size="mini" round @click.native="handleSaveAction" :disabled="disabledBtn('saveAction')">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconsave"></use>
                </svg>
            </el-button>
            <!-- 执行 -->
            <el-button size="mini" round @click.native="handleExecuteAction" :disabled="disabledBtn('executeAction')">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconexecute"></use>
                </svg>
            </el-button>
            <!-- 冒烟 -->
            <el-button size="mini" round @click.native="handleTestAction" :disabled="disabledBtn('testAction')">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icontest"></use>
                </svg>
            </el-button>
            <!-- 发布 -->
            <el-button size="mini" round @click.native="handlePublishAction" :disabled="disabledBtn('publishAction')">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconrelease"></use>
                </svg>
            </el-button>
        </el-button-group>
        <div style="padding-left: 10px;display: inline;"/>
        <el-button-group>
            <!-- 历史 -->
            <el-button size="mini" round @click.native="handleHistoryAction" :disabled="disabledBtn('historyAction')">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconhistory"></use>
                </svg>
            </el-button>
            <!-- 下线 -->
            <el-button size="mini" round @click.native="handleDisableAction" :disabled="disabledBtn('disableAction')"
                       v-if="apiInfo.apiStatus===1 || apiInfo.apiStatus===2">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icondisable"></use>
                </svg>
            </el-button>
            <!-- 删除 -->
            <el-button size="mini" round @click.native="handleDeleteAction" :disabled="disabledBtn('deleteAction')"
                       v-if="apiInfo.apiStatus===0 || apiInfo.apiStatus===3">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icondelete"></use>
                </svg>
            </el-button>
        </el-button-group>
    </div>
</template>
<script>
    import request from "../utils/request";
    import {ApiUrl} from "../utils/api-const";

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
                    //this.updateBasicInfo();
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
                        "headerData": self.requestHeader
                    }
                }, response => {
                    if (response.data.result) {
                        if (!self.newMode) {
                            self.$message({message: 'Save successfully.', type: 'success'});
                            self.$emit('onAfterSave', self.apiInfo.apiStatus, response.data.status);
                        } else {
                            this.$router.push("/edit/" + response.data.id);
                        }
                    } else {
                        self.$alert(response.data.message, 'Failed', {
                            confirmButtonText: 'OK'
                        });
                    }
                });
            },
            // 执行按钮
            handleExecuteAction() {
                let requestHeaderData = {};
                for (let i = 0; i < this.requestHeader.length; i++) {
                    if (this.requestHeader[i].checked && this.requestHeader[i].name !== '') {
                        requestHeaderData[this.requestHeader[i].name] = encodeURIComponent(this.requestHeader[i].value);
                    }
                }
                const self = this;
                request(ApiUrl.perform + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "headers": requestHeaderData,
                    "data": {
                        "id": self.apiInfo.apiID,
                        "select": self.apiInfo.select,
                        "apiPath": self.apiInfo.apiPath,
                        "comment": self.apiInfo.comment,
                        "codeType": self.apiInfo.codeType,
                        "codeValue": self.apiInfo.codeValue,
                        "requestBody": self.requestBody,
                    }
                }, response => {
                    self.$emit('onExecute', response.data);
                });
            },
            // 冒烟按钮
            handleTestAction() {
                let requestHeaderData = {};
                for (let i = 0; i < this.requestHeader.length; i++) {
                    if (this.requestHeader[i].checked && this.requestHeader[i].name !== '') {
                        requestHeaderData[this.requestHeader[i].name] = encodeURIComponent(this.requestHeader[i].value);
                    }
                }
                const self = this;
                request(ApiUrl.smokeTest + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "headers": requestHeaderData,
                    "data": {
                        "id": self.apiInfo.apiID,
                        "requestBody": self.requestBody,
                    }
                }, response => {
                    if (response.data.success === true) {
                        this.smokeTest = true;
                        self.$emit('onSmokeTest', response.data);
                    } else {
                        self.$alert('Smoke Test Failed, result success is false.', 'Error', {
                            confirmButtonText: 'OK'
                        });
                    }
                });
            },
            // 发布按钮
            handlePublishAction() {
            },
            // 历史按钮
            handleHistoryAction() {
            },
            // 禁用按钮
            handleDisableAction() {
            },
            // 删除按钮
            handleDeleteAction() {
            }
        },
        data() {
            return {
                smokeTest: false
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
</style>