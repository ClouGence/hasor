return {
    "user" : userManager.findUserByID (uid) {
        "uid" : userID,
        "name",
        "age",
        "nick"
    },
    "orderList" : queryOrder ( %{$.user.uid} ) [
        {
            "orderID",
            "itemID",
            "itemName"
        }
    ]
}