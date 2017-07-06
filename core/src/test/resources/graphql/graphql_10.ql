//---------------------------------------------------------------
//                   The detailed steps
//---------------------------------------------------------------

// 使用 readTxt 分别装载数据到， dat1 ，dat2
var dat1 = readTxt("Contract.txt") [
    {
        "clientID",
        "amount" : Amount, // "amount" mapping to "Amount"
        "year"
    }
]
var dat2 = readTxt("Client.txt") [
    {
        "id",
        "name"
    }
]
// 使用 mapJoin 函数连接两个数据为一个整体，关联字段为："clientID", "id"
var merageData = mapJoin( dat1, dat2, [ "clientID", "id" ] ) [
    {
        "clientID"  : $0.clientID ,
        "amount"    : $0.amount ,
        "year"      : $0.year ,
        "name"      : $1.name
    }
]

// 过滤 1998 年的数据
var meraged = filter( merageData , lambda : (obj) -> return obj.year == 1998 )~
/*
    LOAD    3
    LAMBDA  1
    LOCAL   1,"obj"
    FRAME_S
    ROU     "obj.year"
    LDC_D   1998
    DO      "=="
    END
    FRAME_E
    CALL    "filter",2
    ASO
    ASE
    STORE   4
    ...
*/


// 按照 name 将 data 数据集合进行分组，最终数据格式为： { "name" : "xxx" , "amount" : 12345 }
var groupDs = group(meraged , [ "name" ] , { "amount" : "sum"} )~

// 取得全年度 25% 价格，该价格作为大客户的标尺。
var markAmount = sum(groupDs, "amount")~ / 4

// 搜索符合预期的大客户名单
var bigOne = filter(groupDs , lambda : (obj) -> {
    return (obj.amount > markAmount) && (markAmount > 0)
})~;

// 将搜索结果进行倒序排序，排序字段为： "amount"
var sortData =  sort(bigOne , {
    "Amount" : "desc"
})~;

return sortData;

    // ---------------------------------------------------------------
    //                   The Concise writing
    // ---------------------------------------------------------------

// 使用 readTxt 函数分别读取 "Contract.txt" 和 "Client.txt" 数据。
// 使用 mapJoin 函数连接两个数据为一个整体，关联字段为："clientID", "id"
// 过滤 1998 年的数据
var data = filter(mapJoin( readTxt("Contract.txt")~, readTxt("Client.txt")~, [ "clientID", "id" ]) [
        {
            "clientID"  : $0.clientID ,
            "amount"    : $0.Amount ,   // "amount" mapping to "Amount"
            "year"      : $0.year ,
            "name"      : $1.name
        }
    ], lambda : (obj) -> return obj.year == 1998 )~


// 按照 name 将 data 数据集合进行分组，最终数据格式为： { "name" : "xxx" , "amount" : 12345 }
var groupDs = group(data , [ "name" ] , { "amount" : "sum"} )[
    {
        "name",
        "amount"
    }
]

// 取得全年度 25% 价格，该价格作为大客户的标尺。
var markAmount = sum(groupDs, "amount")~ / 2

// 按照 25 % 标尺搜索符合预期的大客户，并对搜索结果进行倒序排序，排序字段为 "amount"
return sort(filter(groupDs , lambda : (obj) -> {
            return (obj.amount > markAmount) && (markAmount > 0)
        })~, {"amount" : "desc" })~;