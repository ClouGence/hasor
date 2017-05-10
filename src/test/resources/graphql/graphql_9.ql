fragment fOrder on queryOrder ("accountID"  = uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
]

fragment fUser on  {
    "userInfo" : findUserByID ("userID"  = uid) {

    },
    "source" : "DataQL"
}

{
    "user" : fUser,
    "orderList" : fOrder
}