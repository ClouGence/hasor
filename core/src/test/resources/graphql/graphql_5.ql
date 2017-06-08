findUserByID ("userID"  = uid, "status"  = 1, "oriData"  =  {
        "self" : true,
        "testID" : 222
    }) {
    "info" :  {
        "userID",
        "nick" : nick
    },
    "orderList" : queryOrder ("accountID"  = $.info.userID) [
        {
            "orderID",
            "itemID",
            "itemName",
            "nick" : $.nick
        }
    ]
}