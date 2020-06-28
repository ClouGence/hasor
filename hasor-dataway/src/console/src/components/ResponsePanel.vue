<template>
  <div class="responsePanel">
    <div class="response-btns">
      <el-tooltip class="item" effect="dark" content="use Result Structure" placement="top-end">
        <el-checkbox v-if="onEditPage" v-model="optionInfoCopy['resultStructure']" style="padding: 3px 5px;z-index: 1000">Structure</el-checkbox>
      </el-tooltip>
      <el-button-group>
        <el-tooltip class="item" effect="dark" content="Copy to Clipboard" placement="top-end">
          <el-button v-clipboard:copy="responseBodyCopy" v-clipboard:success="handleJsonResultCopySuccess" v-clipboard:error="handleJsonResultCopyError"
                     class="z-index-top" size="mini" round
          >
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconcopy"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="Format Result" placement="top-end">
          <el-button v-if="panelActiveName ==='result_view' && resultType ==='json'"
                     class="z-index-top" size="mini" round @click.native="handleJsonResultFormatter"
          >
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconformat"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="Save As Download" placement="top-end">
          <el-button v-if="panelActiveName ==='result_view' && resultType ==='bytes'"
                     class="z-index-top" size="mini" round @click.native="handleResultDownload"
          >
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icondownload"></use>
            </svg>
          </el-button>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="Format Structure" placement="top-end">
          <el-button v-if="onEditPage && panelActiveName ==='result_format'"
                     class="z-index-top" size="mini" round @click.native="handleStructureFormatter"
          >
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconformat"></use>
            </svg>
          </el-button>
        </el-tooltip>
      </el-button-group>
    </div>
    <el-tabs v-model="panelActiveName" class="response-tabs" type="card">
      <el-tab-pane name="result_view" label="Result">
        <div ref="responsePanel" />
      </el-tab-pane>
      <el-tab-pane v-if="onEditPage" name="result_format" label="Structure" :disabled="!optionInfoCopy['resultStructure']">
        <div ref="responseFormatPanel" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import {defineMonacoEditorFoo} from '../utils/editorUtils';
import {formatDate} from '../utils/utils';
import {defaultOptionData} from '../utils/api-const';

export default {
    props: {
        optionInfo: {
            type: Object,
            default: function () {
                return {};
            }
        },
        responseBody: {
            type: String,
            default: function () {
                return '"empty."';
            }
        },
        onEditPage: {
            type: Boolean,
            default: function () {
                return false; // 是否在 编辑 页面
            }
        },
        resultType: {
            type: String,
            default: function () {
                return 'json';
            }
        }
    },
    data() {
        return {
            optionInfoCopy: {},
            responseBodyCopy: '',
            panelActiveName: 'result_view',
            height: '10px'
        };
    },
    watch: {
        'optionInfoCopy': {
            handler(val, oldVal) {
                if (!this.optionInfoCopy['resultStructure']) {
                    const self = this;
                    self.$nextTick(function () {
                        self.panelActiveName = 'result_view';
                    });
                }
                this.$emit('onOptionChange', this.optionInfoCopy);
            },
            deep: true
        },
        'responseBodyCopy': {
            handler(val, oldVal) {
                this.$emit('onResponseBodyChange', this.responseBodyCopy);
            }
        }
    },
    mounted() {
        const self = this;
        this.monacoDataEditor = defineMonacoEditorFoo(this.$refs.responsePanel, {});
        this.monacoDataEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
            self.responseBodyCopy = self.monacoDataEditor.getValue();
        });
        //
        if (this.onEditPage) {
            this.monacoForamtEditor = defineMonacoEditorFoo(this.$refs.responseFormatPanel, {});
            this.monacoForamtEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
                self.optionInfoCopy['responseFormat'] = self.monacoForamtEditor.getValue();
            });
        }
        //
        this.responseBodyCopy = this.responseBody;
        this.optionInfoCopy = { ...defaultOptionData, ...this.optionInfo};
        this.doUpdate();
    },
    methods: {
        // 响应结果格式化
        handleJsonResultFormatter() {
            try {
                this.responseBodyCopy = JSON.stringify(JSON.parse(this.responseBodyCopy), null, 2);
                this.monacoDataEditor.setValue(this.responseBodyCopy);
            } catch (e) {
                this.$message.error('JsonResult Format Error : ' + e);
            }
        },
        // 响应结果格式化
        handleStructureFormatter() {
            try {
                this.optionInfoCopy['responseFormat'] = JSON.stringify(JSON.parse(this.optionInfoCopy['responseFormat']), null, 2);
                this.monacoForamtEditor.setValue(this.optionInfoCopy['responseFormat']);
            } catch (e) {
                this.$message.error('Structure Format Error : ' + e);
            }
        },
        // 拷贝结果
        handleJsonResultCopySuccess() {
            this.$message({message: 'JsonResult Copy to Copied', type: 'success'});
        },
        handleJsonResultCopyError() {
            this.$message.error('JsonResult Copy to Copied Failed');
        },
        // 下载
        handleResultDownload() {
            // 把十六进制转换为bytes
            const localResponseBody = this.responseBody;
            const localArrays = localResponseBody.replace(/\n/g, ' ').split(' ');
            const byteArray = [];
            for (let i = 0; i < localArrays.length; i++) {
                byteArray.push(parseInt(localArrays[i], 16));
            }
            const byteUint8Array = new Uint8Array(byteArray);
            // 创建隐藏的可下载链接
            const eleLink = document.createElement('a');
            eleLink.download = formatDate(new Date()) + '.result';
            eleLink.style.display = 'none';
            // 字符内容转变成blob地址
            const blob = new Blob([byteUint8Array]);
            eleLink.href = URL.createObjectURL(blob);
            // 触发点击
            document.body.appendChild(eleLink);
            eleLink.click();
            // 然后移除
            document.body.removeChild(eleLink);
        },
        //
        // 执行布局
        doLayout(height, width) {
            this.monacoDataEditor.layout({height: (height - 47), width: width});
            this.onEditPage && this.monacoForamtEditor.layout({height: (height - 47), width: width});
        },
        doUpdate() {
            this.responseBodyCopy = this.responseBody;
            this.monacoDataEditor.setValue(this.responseBodyCopy);
            this.onEditPage && this.monacoForamtEditor.setValue(this.optionInfoCopy['responseFormat']);
        }
    }
};
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
