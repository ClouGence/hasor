const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const apiMocker = require('mocker-api');
const path = require('path');

module.exports = {
    assetsDir: 'static',
    publicPath: './',
    outputDir: 'dist/META-INF/hasor-framework/dataway-ui',
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