// 递归：利用有状态集合，把一个多维数组打平成为一维数组

import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;

var data = [
    [1,2,3,[4,5]],
    [6,7,8,9,0]
]

var foo = (dat, arrayObj) -> {
    var tmpArray = dat => [ # ];  // 符号 '#' 相当于在循环 dat 数组期间的，当前元素。
    if (tmpArray[0] == dat) {
        run arrayObj.addLast(dat);// 末级元素直接加到最终的集合中，否则就继续遍历集合
    } else {
        run tmpArray => [ foo(#,arrayObj) ];
    }
    return arrayObj;
}

return foo(data,collect.new()).data();