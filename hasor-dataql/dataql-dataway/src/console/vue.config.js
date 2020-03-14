const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')

module.exports = {
    assetsDir: 'static',
    runtimeCompiler: true,
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new MonacoWebpackPlugin()
        ]
    },
    chainWebpack: config => {
        config.output.filename('[name].[hash].js').end();
    },
}