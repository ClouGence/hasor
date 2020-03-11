<template>
  <div>
    <div class="monacoEditorHeader">
      <div style="width: 50%; margin-top: 2px;">
        <el-input placeholder="the path to access this Api" v-model="apiPath" class="input-with-select" size="mini">
          <el-select v-model="select" slot="prepend" placeholder="Choose">
            <el-option label="POST" value="POST"/>
            <el-option label="PUT" value="PUT"/>
            <el-option label="GET" value="GET"/>
          </el-select>
        </el-input>
      </div>
      <el-button-group>
        <el-button size="mini" type="success" icon="el-icon-s-promotion"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
        <el-button size="mini" type="warning" icon="el-icon-s-open"/>
        <el-button size="mini" type="primary" icon="el-icon-edit"/>
      </el-button-group>
      <el-radio-group v-model="codeType" size="mini">
        <el-radio-button label="DataQL"/>
        <el-radio-button label="SQL"/>
        <el-radio-button label="Json"/>
      </el-radio-group>
    </div>
    <el-divider></el-divider>
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
import * as monaco from 'monaco-editor'
import RequestPanel from './RequestPanel'
import ResponsePanel from './ResponsePanel'

export default {
  components: {
    RequestPanel, ResponsePanel
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
    let _this = this
    this.monacoEditor.updateOptions({minimap: {enabled: false}})
    this.monacoEditor.onDidChangeModelContent(function (event) { // 编辑器内容changge事件
      _this.codeValue = _this.monacoEditor.getValue()
    })
    this.layoutMonacoEditor()
    this._resize = () => {
      return (() => {
        _this.layoutMonacoEditor()
      })()
    }
    window.addEventListener('resize', this._resize)

    // // 自定义键盘事件
    // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function () {
    //   self.$emit('onCommit', self.monacoEditor.getValue(), self.monacoEditor)
    // })
    // self.monacoEditor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_S, function () {
    //   // 自定义快捷操作
    // })
    // this.initEditor()
  },
  beforeDestroy () {
    window.removeEventListener('resize', this._resize)
  },
  methods: {
    layoutMonacoEditor () {
      this.panelHeight = document.documentElement.clientHeight - 88
      this.monacoEditor.layout({
        height: this.panelHeight,
        width: (document.documentElement.clientWidth * (this.panelPercentVertical / 100))
      })
      //
      this.panelPercent = this.panelPercentHorizontal
      let dataNum = this.panelPercentHorizontal / 100
      let size = document.documentElement.clientHeight - 88
      this.$refs.editerRequestPanel.doLayout(size * dataNum)
      this.$refs.editerResponsePanel.doLayout(size * (1 - dataNum))
    },
    handleVerticalSplitResize (data) {
      this.panelPercentVertical = data
      this.layoutMonacoEditor()
    },
    handleHorizontalSplitResize (data) {
      this.panelPercentHorizontal = data
      this.layoutMonacoEditor()
    }
  },
  data () {
    return {
      select: 'POST',
      apiPath: '',
      codeType: 'DataQL',
      codeValue: '<div>请编辑html内容</div>',
      requestBody: '{}',
      responseBody: '"empty."',
      headerData: [
        {checked: true, name: 'name1', value: 'value1'},
        {checked: false, name: 'name2', value: 'value2'},
        {checked: false, name: 'name3', value: 'value3'},
        {checked: true, name: 'name4', value: 'value4'},
        {checked: true, name: 'name5', value: 'value5'}
      ],
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
<style lang="scss">
  .monacoEditorHeader {
    display: flex;
    justify-content: space-between;
    justify-items: center;
    overflow-x: hidden;
    padding: 5px;
  }
</style>
