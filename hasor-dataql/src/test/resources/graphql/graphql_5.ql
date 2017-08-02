return findUserByID ({"userID" : uid, "status" : 1, "oriData" : {
        "self" : true,
        "testID" : 222
    }}) {
    "info" :  {
        "userID",
        "nick"
    },
    "orderList" : queryOrder ({"accountID" : userID , "oriList" :  [ "self" ,"testID" ] }) [
        {
            "orderID",
            "itemID",
            "itemName",
            "nick"
        }
    ]
}

/*
    NO
    ROU     "uid"
    PUT     "userID"
    LDC_D   1
    PUT     "status"
    NO
    LDC_B   true
    PUT     "self"
    LDC_D   222
    PUT     "testID"
    PUT     "oriData"
    CALL    "findUserByID",1
    ASM
    NO
    ROU     "userID"
    PUT     "userID"
    ROU     "nick"
    PUT     "nick"
    PUT     "info"
    NO
    ROU     "userID"
    PUT     "accountID"
    NA
    LDC_S   "self"
    PUSH
    LDC_S   "testID"
    PUSH
    PUT     "oriList"
    CALL    "queryOrder",1
    ASA
    NO
    ROU     "orderID"
    PUT     "orderID"
    ROU     "itemID"
    PUT     "itemID"
    ROU     "itemName"
    PUT     "itemName"
    ROU     "nick"
    PUT     "nick"
    PUSH
    ASE
    PUT     "orderList"
    ASE
*/