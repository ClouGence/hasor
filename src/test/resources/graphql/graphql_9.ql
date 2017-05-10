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
    "source" : "GraphQL"
}

{
    "user" : fUser,
    "orderList" : fOrder
}