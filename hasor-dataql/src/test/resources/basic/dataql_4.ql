
// 说明：演示从结果中取值
//    DataQL 采用的是一切皆为数据，同时它不支持对象。因此单独获取数据中某个字段的值就需要用到取值语法。
// 格式：
//      ( <UDF调用> | <标识符> ) "->" <结果类型>
//

// ------------------------------------------------------------ 演示1：从 UDF 返回值中取值，然后进行处理结果

var addrList_1 = findUserByID (12345) -> "addressList" [
    {
        "code",
        "address"
    }
]

// ------------------------------------------------------------ 演示2：先执行udf拿到完整的返回值，然后使用取值方式处理结果（相当于把演示1拆成2步）

var dat = findUserByID (12345) ~

var addrList_2 = dat -> "addressList" [
    {
        "code"
    }
]

// ------------------------------------------------------------ 演示3：与演示1、演示2 相同，不同的是取值的结果采用原始类型

var addrList_3 = findUserByID (12345) -> "addressList" ~

var dat = findUserByID (12345) ~
var addrList_4 = dat -> "addressList" ~

// ------------------------------------------------------------ 演示4：取值之后直接参与运算

var user = findUserByID (12345) ~

var dat = user -> "age" ~ + 31


return {
    "addrA" : addrList_1 ,
    "addrB" : addrList_2 ,
    "addrC" : addrList_3 ,
    "addrD" : addrList_4 ,
    "age"   : dat
};