
// 说明：从 udf 查询结果中取一个属性作为最终结果进行处理（也可以用作在结果中取值）

// ------------------------------------------------------------ 方式1

var addrList_1 = findUserByID (12345) -> "addressList" [
    {
        "code",
        "address"
    }
]

// ------------------------------------------------------------ 方式2

var addrList_2 = findUserByID (12345) -> "addressList" ~

// ------------------------------------------------------------ 方式3

var dat = findUserByID (12345) ~
var addrList_3 = dat -> "addressList" [
    {
        "code"
    }
]

return {
    "addrA" : addrList_1 ,
    "addrB" : addrList_2 ,
    "addrC" : addrList_3
};