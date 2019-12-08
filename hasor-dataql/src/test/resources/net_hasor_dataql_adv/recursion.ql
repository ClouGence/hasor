// 递归：把一个多维数组打平成为一维数组

var data = [
    [1,2,3,[4,5]],
    [6,7,8,9,0]
]

var foo = (dat, fun) -> {
    var tmpArray = dat => [ # ];    // 符号 '#' 相当于在循环 dat 数组期间的，当前元素。
    if (tmpArray[0] == dat) {
        return fun(dat);
    } else {
        return tmpArray => [ foo(#,fun) ];
    }
}

return foo(data,${addToArray})
