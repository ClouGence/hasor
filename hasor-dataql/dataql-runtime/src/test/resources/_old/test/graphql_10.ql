
// 1.使用 readTxt 函数分别读取 "Contract.txt" 和 "Client.txt" 数据。
// 2.使用 mapJoin 函数连接两个数据为一个整体，关联字段为："clientID", "id"
// 3.过滤 1998 年的数据
var data = filter( mapJoin( readTxt("Contract.txt"), readTxt("Client.txt"), [ "clientID", "id" ]) => [
        {
            "clientID"  : clientID ,
            "amount"    : Amount ,   // "amount" mapping to "Amount"
            "year"      : year ,
            "name"      : name
        }
    ], (obj) -> return obj.year == 1998 )


// 按照 name 将 data 数据集合进行分组，最终数据格式为： { "name" : "xxx" , "amount" : 12345 }
var groupDs = groupBy(data , [ "name" ] , { "amount" : "sum"} ) => [
    {
        "name",
        "amount"
    }
]

// 取得全年度 25% 价格，该价格作为大客户的标尺。
var markAmount = null;

var tempAmount = groupDs => [
    update(#.amount)
]


 sum(groupDs, "amount")~ / 2

// 按照 25 % 标尺搜索符合预期的大客户，并对搜索结果进行倒序排序，排序字段为 "amount"
return sort(filter(groupDs , lambda : (obj) -> {
            return (obj.amount > markAmount) && (markAmount > 0)
        })~, {"amount" : "desc" })~;