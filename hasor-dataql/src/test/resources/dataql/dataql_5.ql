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