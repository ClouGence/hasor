fragment fOrder on queryOrder ("accountID"  = uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
]

fragment fUser on  {
    "userInfo" : findUserByID ("userID"  = uid) {
        "userID",
        "status",
        "addressList" : foreach( "list" = addressList ) [
            {
                "zip",
                "address"
            }
        ]
    },
    "source" : "DataQL"
}

{
    "user" : fUser,
    "orderList" : fOrder
}