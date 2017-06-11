    //---------------------------------------------------------------
    //                   The detailed steps
    //---------------------------------------------------------------

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

var merageData = mapJoin( dat1, dat2, [ "clientID", "id" ] ) [
    {
        "clientID"  : $0.clientID ,
        "amount"    : $0.amount ,
        "year"      : $0.year ,
        "name"      : $1.name
    }
]

var meraged = filter( merageData , lambda : (obj) -> return obj.year == 1998 )~

var groupDs = group(meraged , [ "name" ] , { "amount" : "sum"} )~

var markAmount = sum(groupDs, "amount")~ / 4

var bigOne = filter(groupDs , lambda : (obj) -> {
    return (obj.amount > markAmount) && (markAmount > 0)
})~;

var sortData =  sort(bigOne , {
    "Amount" : "desc"
})~;

return sortData;

    // ---------------------------------------------------------------
    //                   The Concise writing
    // ---------------------------------------------------------------

var data = filter(mapJoin( readTxt("Contract.txt")~, readTxt("Client.txt")~, [ "clientID", "id" ]) [
        {
            "clientID"  : $0.clientID ,
            "amount"    : $0.Amount ,   // "amount" mapping to "Amount"
            "year"      : $0.year ,
            "name"      : $1.name
        }
    ], lambda : (obj) -> return obj.year == 1998 )~

var groupData = group(data , [ "name" ] , { "amount" : "sum"} )[
    {
        "name",
        "amount"
    }
]

var markAmount = sum(groupDs, "amount")~ / 2

return sort(filter(groupDs , lambda : (obj) -> {
            return (obj.amount > markAmount) && (markAmount > 0)
        })~, {"amount" : "desc" })~;