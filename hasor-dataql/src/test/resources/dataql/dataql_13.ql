
// 基础比较运算
var basicNumber = [
    5 >  3,
    5 >= 3,
    5 <  3,
    5 <= 3,
    5 == 3,
    5 != 3
];

// double比较运算
var doubleNumber = [
    double()~ >  3,
    double()~ >= 3,
    double()~ <  3,
    double()~ <= 3,
    double()~ != 3,
    double()~ == 3
];

return {
    "basic"  : basicNumber,
    "double" : doubleNumber
};