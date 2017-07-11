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

/*
    NO
    ROU     "uid"
    CALL    "userManager.findUserByID"
    ASO
    ROU     "userID"
    PUT     "uid"
    ROU     "name"
    PUT     "name"
    ROU     "age"
    PUT     "age"
    ROU     "nick"
    PUT     "nick"
    ASE
    PUT     "user"
    ROU     "%{$.user.uid}"
    CALL    "queryOrder"
    ASA
    NO
    ROU     "orderID"
    PUT     "orderID"
    ROU     "itemID"
    PUT     "itemID"
    ROU     "itemName"
    PUT     "itemName"
    PUSH
    ASE
    PUT     "orderList"
    END
*/