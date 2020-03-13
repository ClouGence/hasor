const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')

module.exports = {
    assetsDir: 'static',
    runtimeCompiler: true,
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new MonacoWebpackPlugin()
        ]
    }
}