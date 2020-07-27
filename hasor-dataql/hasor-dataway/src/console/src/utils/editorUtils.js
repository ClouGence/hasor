import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

const monacoDefaultOptions = {
    value: '',
    language: 'javascript',
    theme: 'vs', // vs, hc-black, or vs-dark
    editorOptions: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false, // 只读
        cursorStyle: 'line', // 光标样式
        automaticLayout: false, // 自动布局
        glyphMargin: true, // 字形边缘
        useTabStops: false,
        fontSize: 14, // 字体大小
        autoIndent: true, // 自动布局
        contextmenu: true
        // quickSuggestionsDelay: 500,   //代码提示延时
    }
};
const loadMonacoEditorSelfTheme = () => {
    monaco.editor.defineTheme('selfTheme', {
        base: 'vs',
        inherit: true,
        rules: [],
        colors: {
            'editor.lineHighlightBackground': '#fff8c5'
        }
    });
    // monaco.editor.setTheme('selfTheme');
};
const defineMonacoEditorFoo = (container, options) => {
    const newEditor = monaco.editor.create(container, {
        ...monacoDefaultOptions,
        ...options
    });
    newEditor.updateOptions({minimap: {enabled: false}});
    newEditor.updateOptions({contextmenu: false});
    return newEditor;
};

export {defineMonacoEditorFoo, loadMonacoEditorSelfTheme};
