const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');

module.exports = {
    assetsDir: 'static',
    publicPath: './',
    outputDir: 'dist/META-INF/hasor-framework/dataway-ui',
    runtimeCompiler: true,
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new MonacoWebpackPlugin({
                "languages": ["javascript", "sql"],
                "features": [
                    'quickCommand',         // F1指令提示框
                    'contextmenu',          // 右键菜单
                    'clipboard',            // 剪切板
                    'colorDetector',
                    'comment',              // 注释相关：多行注释、单行注释
                    'coreCommands',
                    'find', 'suggest',      // 查找/替换 (suggest 不加的话会导致 查找框图标丢失)
                    'fontZoom',             // 字号放大缩小
                    'wordHighlighter',      // 相同词组高亮
                ]
            })
        ]
    },
    chainWebpack: config => {
        config.output.filename('[name].[hash].js').end();
    },
    devServer: {
        host: 'localhost',//target host
        port: 8888,
        publicPath: '/interface-ui/',
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: true
            }
        },
        open: true,
        openPage: '/interface-ui/'
    }
};
