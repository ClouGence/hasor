// 小数精度为 3
option MAX_DECIMAL_DIGITS   = 5


// 基础算数运算
var basicNumber = [
    5 + 3,
    5 - 3,
    5 * 3,
    5 / 3,
    5 \ 3,
    5 % 3
];

// double算数运算
var doubleNumber = [
    double()~ + 3,
    double()~ - 3,
    double()~ * 3,
    double()~ / 3,
    double()~ \ 3,
    double()~ % 3
];

return {
    "basic"  : basicNumber,
    "double" : doubleNumber
};