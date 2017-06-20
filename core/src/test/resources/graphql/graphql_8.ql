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

/*
    ROU     "uid"
    CALL    "queryOrder",1
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
    STORE   1
    ROU     "uid"
    CALL    "findUserByID",1
    ASM
    ROU     "userID"
    PUT     "userID"
    ROU     "name"
    PUT     "name"
    ROU     "age"
    PUT     "age"
    ROU     "nick"
    PUT     "nick"
    ASE
    STORE   2
    NO
    LOAD    2
    PUT     "user"
    LOAD    1
    PUT     "orderList"
    END
*/