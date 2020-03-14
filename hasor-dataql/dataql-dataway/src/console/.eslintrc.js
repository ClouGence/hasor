module.exports = {
    root: true,
    env: {
        node: true
    },
    'extends': [
        'plugin:vue/essential',
        'eslint:recommended'
    ],
    parserOptions: {
        parser: 'babel-eslint'
    },
    rules: {
        'no-console': 'off',
        'no-debugger': 'off',
        "no-unused-vars": "off",
        "vue/no-unused-vars": "off",
        "generator-star-spacing": "off",
        "no-tabs": "off",
        "no-irregular-whitespace": "off",
    }
};