{
    "user" : userManager.findUserByID ("userID"  = uid) {
        "uid" : userID,
        "name",
        "age",
        "nick"
    },
    "orderList" : queryOrder ("accountID"  = %{$.user.uid}) [
        {
            "orderID",
            "itemID",
            "itemName"
        }
    ]
}