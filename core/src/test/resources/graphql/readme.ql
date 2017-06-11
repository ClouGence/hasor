-- 调用
    <func> ( <参数1> , <参数2> )

-- 查询
    <func> ( <参数1> , <参数2> )
    <列表> or <对象>

-- 列表
    [ <值1> , <值2> ]

-- 对象
    {
        "<name>",
        "<name>" : <值> ,
        "<name>" : <值>
    }

-- 值
    (
        <null> | <boolean> | <string> | <number>
    ) | (
        <name>
    ) | (
        <查询>
    ) | (
        <列表>
    ) | (
        <对象>
    ) | (
        <表达式>
    ) | (
        <函数>
    )

-- 表达式
    (
        <一元操作符> <值>
    ) | (
        <值> <一元操作符>
    ) | (
        <值> <二元操作符> <值>
    ) | (
        <(> <值> <二元操作符> <值> <)>
    ) | (
        <值> ? <值> : <值>
    ) | (
        <值> <merge join> <值> <on> ( <append> | <offside> <number> )
    ) | (
        <值> <map join> <值> <on> <name> = <name>
    )

-- 函数
    <(>
        (
            <name>
        )?
        (
            <,> <name>
        )*
    <)> <->> <{>
        <值>
    <}>

-- 一元操作符
    ++  自增
    --  自减
    !   取非（按位取反）

-- 二元操作
    +   加法
    -   减法
    *   乘法
    /   除法
    %   求余
    \   整除

    >   大于
    >=  大于等于
    <   小于
    <=  小于等于
    ==  等于
    !=  不等于

    &   与
    |   或
    ^   异或
    <<  左移位
    >>  右移位
    >>> 无符号右移位

    &&  逻辑与
    ||  逻辑或

-- 变量
    <var> <name> <=> <值> ( <;> )?

-- 判断
        <if> <(> <值> <)>        <块>
    (
        <elseif> <(> <值> <)>    <块>
    )*
    (
        <else> <(> <值> <)>      <块>
    )?
        <end>

-- 终止
    <return> <值> ( <;> )?

-- 块
    (
        (
            <变量>
        ) | (
            <判断>
        ) | (
            <终止>
        )
    )*
    <EOF>







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

var groupData = group(data , [ "name" ] , { "amount" : "sum"} )[
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