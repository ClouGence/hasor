const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const apiMocker = require('mocker-api');
const path = require('path');

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
    devServer: {
        before(app) {
            apiMocker(app, path.resolve('./src/mocker/index.js'))
        }
    }
};