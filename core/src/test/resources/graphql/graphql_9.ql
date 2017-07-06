var fOrder = queryOrder (uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
];

var fUser = {
    "userInfo" : findUserByID (uid) {
        "userID",
        "status",
        "addressList" : foreach( addressList ) [
            {
                "zip",
                "address"
            }
        ]
    },
    "source" : "DataQL"
};

return {
    "user" : fUser,
    "orderList" : fOrder
};

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
    NO
    ROU     "uid"
    CALL    "findUserByID",1
    ASM
    ROU     "userID"
    PUT     "userID"
    ROU     "status"
    PUT     "status"
    ROU     "addressList"
    CALL    "foreach",1
    ASA
    NO
    ROU     "zip"
    PUT     "zip"
    ROU     "address"
    PUT     "address"
    PUSH
    ASE
    PUT     "addressList"
    ASE
    PUT     "userInfo"
    LDC_S   "DataQL"
    PUT     "source"
    STORE   2
    NO
    LOAD    1
    PUT     "user"
    LOAD    2
    PUT     "orderList"
    END
*/