
// 说明：基于 “dataql_1.ql”、“dataql_2.ql” 两个例子上稍微复杂一点的查询语句。

return findUserByID ({"userID" : uid, "status" : 1, "oriData" : {
        "self" : true,
        "testID" : 222
    }}) [
    {
        "info" :  {
            "userID",
            "nick"
        },
        "orderList" : queryOrder ({"accountID" : userID , "oriList" :  [ "self" ,"testID" ] }) [
            {
                "orderID",
                "itemID",
                "itemName",
                "nick"
            }
        ]
    }
]