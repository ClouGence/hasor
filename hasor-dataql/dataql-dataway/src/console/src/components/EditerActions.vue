<template>
    <div style="display: inline;">
        <el-button-group>
            <!-- 保存 -->
            <el-button size="mini" round @click.native="handleSaveAction">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconsave"></use>
                </svg>
            </el-button>
            <!-- 执行 -->
            <el-button size="mini" round @click.native="handleExecuteAction">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconexecute"></use>
                </svg>
            </el-button>
            <!-- 冒烟 -->
            <el-button size="mini" round @click.native="handleTestAction">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icontest"></use>
                </svg>
            </el-button>
            <!-- 发布 -->
            <el-button size="mini" round @click.native="handlePublishAction" :disabled="this.apiInfo.apiStatus===1">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconrelease"></use>
                </svg>
            </el-button>
        </el-button-group>
        <div style="padding-left: 10px;display: inline;"/>
        <el-button-group>
            <!-- 历史 -->
            <el-button size="mini" round @click.native="handleHistoryAction">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#iconhistory"></use>
                </svg>
            </el-button>
            <!-- 下线 -->
            <el-button size="mini" round @click.native="handleDisableAction" :disabled="!(this.apiInfo.apiStatus===1 || this.apiInfo.apiStatus===2)">
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icondisable"></use>
                </svg>
            </el-button>
            <!-- 删除 -->
            <el-button size="mini" round @click.native="handleDeleteAction">
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
    import {tagInfo} from "../utils/utils";

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
            }
        },
        watch: {
            'apiInfo': {
                handler(val, oldVal) {
                    //this.updateBasicInfo();
                },
                deep: true
            },
            // 'requestBodyCopy': {
            //     handler(val, oldVal) {
            //         this.$emit('onRequestBodyChange', this.requestBodyCopy);
            //     }
            // }
        },
        methods: {
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
                        self.$message({message: 'Save successfully.', type: 'success'});
                        if (response.data.status !== self.apiInfo.apiStatus) {
                            self.$emit('onApiStatusChange', self.apiInfo.apiStatus, response.data.status);
                        }
                    } else {
                        self.$alert(response.data.message, 'Failed', {
                            confirmButtonText: 'OK'
                        });
                    }
                }, response => {
                    self.$alert('Not Fount Api.', 'Error', {
                        confirmButtonText: 'OK'
                    });
                });
            },
            // 执行按钮
            handleExecuteAction() {
                request(ApiUrl.apiSave + "?id=" + this.apiInfo.apiID, {
                    "method": "POST",
                    "data": {
                        "apiID": this.apiID,
                        "select": this.select,
                        "apiPath": this.apiPath,
                        "codeType": this.codeType,
                        "codeValue": this.monacoEditor.getValue(),
                        "requestBody": this.requestBody,
                        "headerData": this.headerData
                    }
                }, response => {
                    alert('');
                }, response => {
                    this.$alert('Not Fount Api.', 'Error', {
                        confirmButtonText: 'OK'
                    });
                });
            },
            // 冒烟按钮
            handleTestAction() {
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
            return {}
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