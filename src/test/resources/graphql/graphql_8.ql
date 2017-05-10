fragment fOrderQL on queryOrder ("accountID"  = uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
]

fragment fUserQL on findUserByID ("userID"  = uid) {
    "userID",
    "name",
    "age",
    "nick"
}

{
    "user" : fUserQL,
    "orderList" : fOrderQL
}