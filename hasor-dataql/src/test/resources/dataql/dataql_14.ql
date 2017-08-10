// 计算数值采用 64 bit，long 模式
option NUMBER_PRECISION     = 32
option MAX_DECIMAL_DIGITS   = 3


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