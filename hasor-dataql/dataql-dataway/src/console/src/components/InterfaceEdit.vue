<template>
  <el-container>
    <el-header>Header</el-header>
    <el-main>
      <el-tabs type="border-card">
        <el-tab-pane :lazy="true">
          <span slot="label"><i class="el-icon-document"></i> HTML</span>
          <MyEditor
            :language="'html'"
            :codes="htmlCodes"
            @onMounted="htmlOnMounted"
            @onCodeChange="htmlOnCodeChange"/>
        </el-tab-pane>
        <el-tab-pane label="Javascript" :lazy="true">
          <MyEditor
            :language="'javascript'"
            :codes="javascriptCodes"
            @onMounted="javascriptOnMounted"
            @onCodeChange="javascriptOnCodeChange"/>
        </el-tab-pane>
        <el-tab-pane label="CSS" :lazy="true">
          <MyEditor
            :language="'css'"
            :codes="cssCodes"
            @onMounted="cssOnMounted"
            @onCodeChange="cssOnCodeChange"/>
        </el-tab-pane>
      </el-tabs>

    </el-main>
    <el-footer>Footer</el-footer>
  </el-container>
</template>

<script>
import MyEditor from './MyEdit'

export default {
  components: {
    MyEditor
  },
  data () {
    return {
      htmlCodes: '<div>This is html</div>',
      javascriptCodes: 'console.log("This is javascript")',
      cssCodes: 'body{}',
      htmlEditor: null,
      jsEditor: null,
      cssEditor: null
    }
  },
  methods: {
    htmlOnMounted (edit) {
      this.htmlEditor = edit
    },
    javascriptOnMounted (edit) {
      this.jsEditor = edit
    },
    cssOnMounted (edit) {
      this.cssEditor = edit
    },
    htmlOnCodeChange (value, event) {
    },
    javascriptOnCodeChange (value, event) {
    },
    cssOnCodeChange (value, event) {
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
