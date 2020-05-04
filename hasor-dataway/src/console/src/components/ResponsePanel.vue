<template>
    <div class="responsePanel">
        <div class="response-btns">
            <el-tooltip class="item" effect="dark" content="use Result Structure" placement="right-start">
                <el-checkbox style="padding: 3px 5px;z-index: 1000" v-model="resultStructureCopy" v-if="onEditPage">Structure</el-checkbox>
            </el-tooltip>
            <el-button-group>
                <el-tooltip class="item" effect="dark" content="Copy to Clipboard" placement="top-end">
                    <el-button class="z-index-top" size="mini" round
                               v-clipboard:copy="responseBodyCopy"
                               v-clipboard:success="handleJsonResultCopySuccess"
                               v-clipboard:error="handleJsonResultCopyError">
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconcopy"></use>
                        </svg>
                    </el-button>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="Format Result" placement="top-end">
                    <el-button class="z-index-top" size="mini" round
                               @click.native='handleJsonResultFormatter'>
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#iconformat"></use>
                        </svg>
                    </el-button>
                </el-tooltip>
            </el-button-group>
        </div>
        <el-tabs class="response-tabs" type="card">
            <el-tab-pane label="JsonResult">
                <div :id="id + '_responseBodyRef'">
                    <codemirror v-model="responseBodyCopy" :options="defaultOption"/>
                </div>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>
<script>
    import 'codemirror'

    export default {
        props: {
            id: {
                type: String,
                default: function () {
                    return 'responsePanel'
                }
            },
            responseBody: {
                type: String,
                default: function () {
                    return '"empty."'
                }
            },
            resultStructure: {
                type: Boolean,
                default: function () {
                    return true
                }
            },
            onEditPage: {
                type: Boolean,
                default: function () {
                    return false; // 是否在 编辑 页面
                }
            }
        },
        data() {
            return {
                defaultOption: {
                    tabSize: 4,
                    styleActiveLine: true,
                    lineNumbers: true,
                    line: true,
                    mode: 'text/javascript'
                },
                responseBodyCopy: '',
                resultStructureCopy: true
            }
        },
        mounted() {
            this.responseBodyCopy = this.responseBody;
            this.resultStructureCopy = (this.resultStructure === undefined) ? true : this.resultStructure;
        },
        watch: {
            'responseBodyCopy': {
                handler(val, oldVal) {
                    this.$emit('onResponseBodyChange', this.responseBodyCopy)
                }
            },
            'resultStructureCopy': {
                handler(val, oldVal) {
                    this.$emit('onResultStructureChange', this.resultStructureCopy)
                }
            }
        },
        methods: {
            // 响应结果格式化
            handleJsonResultFormatter() {
                try {
                    this.responseBodyCopy = JSON.stringify(JSON.parse(this.responseBodyCopy), null, 2)
                } catch (e) {
                    this.$message.error('JsonResult Format Error : ' + e)
                }
            },
            // 拷贝结果
            handleJsonResultCopySuccess() {
                this.$message({message: 'JsonResult Copy to Copied', type: 'success'})
            },
            handleJsonResultCopyError() {
                this.$message.error('JsonResult Copy to Copied Failed')
            },
            //
            // 执行布局
            doLayout(height) {
                let responseBodyID = '#' + this.id + '_responseBodyRef';
                let responseBody = document.querySelectorAll(responseBodyID + ' .CodeMirror')[0];
                responseBody.style.height = (height - 47) + 'px'
            },
            doUpdate() {
                this.responseBodyCopy = this.responseBody;
                this.resultStructureCopy = (this.resultStructure === undefined) ? true : this.resultStructure;
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

    .responsePanel {
    }

    .response-tabs {
        top: -33px;
        position: relative;
    }

    .response-btns {
        padding: 2px 5px;
        display: flex;
        justify-content: flex-end;

    }

    .z-index-top {
        z-index: 1000;
    }
</style>
