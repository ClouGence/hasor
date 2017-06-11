var fOrderQL = queryOrder (uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
]

var fUserQL = findUserByID (uid) {
    "userID",
    "name",
    "age",
    "nick"
}

return {
    "user" : fUserQL,
    "orderList" : fOrderQL
}