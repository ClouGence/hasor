<template>
  <div>
    <div class="monacoEditorHeader">
      <div style="width: 50%;">
        <el-input placeholder="the path to access this Api" v-model="apiPath" class="input-with-select" size="mini">
          <el-select v-model="select" slot="prepend" placeholder="Choose">
            <el-option label="POST" value="POST"/>
            <el-option label="PUT" value="PUT"/>
            <el-option label="GET" value="GET"/>
          </el-select>
        </el-input>
      </div>
      <el-radio-group v-model="codeType" size="mini">
        <el-radio-button label="DataQL"/>
        <el-radio-button label="SQL"/>
        <el-radio-button label="Json"/>
      </el-radio-group>
      <!-- 格式化 -->
      <el-button-group>
        <el-button size="mini" type="success" icon="el-icon-s-promotion"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
        <el-button size="mini" type="warning" icon="el-icon-s-open"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
      </el-button-group>
    </div>
    <el-divider></el-divider>
    <div ref="container"/>
  </div>
</template>
<script>
import * as monaco from 'monaco-editor'

export default {
  data () {
    return {
      select: 'POST',
      apiPath: '',
      codeType: 'DataQL',
      codeValue: '<div>请编辑html内容</div>',
      //
      //
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
  },
  mounted () {
    this.monacoEditor = monaco.editor.create(this.$refs.container, {
      value: this.codeValue,
      language: 'javascript',
      theme: 'vs', // vs, hc-black, or vs-dark
      editorOptions: this.monacoEditorOptions
    })
    //
    monaco.editor.defineTheme('selfTheme', {
      base: 'vs',
      inherit: true,
      rules: [],
      colors: {
        'editor.lineHighlightBackground': '#fff8c5'
      }
    })
    monaco.editor.setTheme('selfTheme')
    //
    // let contextmenu = this.monacoEditor.getContribution('editor.contrib.contextmenu')
    // let actions = this.monacoEditor.getActions()
    // this.monacoEditor.updateOptions({ contextmenu: false })
    let self = this
    this.monacoEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
      self.codeValue = self.monacoEditor.getValue()
    })
    window.addEventListener('resize', function () {
      self.layoutMonacoEditor()
    })
    this.layoutMonacoEditor()
    // // 自定义键盘事件
    // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function () {
    //   self.$emit('onCommit', self.monacoEditor.getValue(), self.monacoEditor)
    // })
    // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_S, function () {
    //   // 自定义快捷操作
    // })
    // this.initEditor()
  },
  methods: {
    layoutMonacoEditor () {
      this.monacoEditor.layout({
        height: document.documentElement.clientHeight - 88,
        width: document.documentElement.clientWidth
      })
    }
    // initEditor () {
    // let self = this
    // self.$refs.container.innerHTML = ''
    // }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
  .monacoEditorHeader {
    display: flex;
    justify-content: space-between;
    justify-items: center;
    overflow-x: hidden;
    padding: 5px;
  }
  .el-divider--horizontal {
    margin: 0px !important;
  }

  .el-select .el-input {
    width: 90px;
  }

  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
</style>
