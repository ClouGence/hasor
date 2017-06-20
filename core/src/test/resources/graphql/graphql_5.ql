return findUserByID ({"userID" : uid, "status" : 1, "oriData" : {
        "self" : true,
        "testID" : 222
    }}) {
    "info" :  {
        "userID",
        "nick" : nick
    },
    "orderList" : queryOrder ({"accountID" : %{$.info.userID} , "oriList" :  [ "self" ,"testID" ] }) [
        {
            "orderID",
            "itemID",
            "itemName",
            "nick" : $.nick
        }
    ]
}

/*
    NO
    ROU     "uid"
    PUT     "userID"
    INSN_N  1
    PUT     "status"
    NO
    INSN_B  true
    PUT     "self"
    INSN_N  222
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
    ROU     "%{$.info.userID}"
    PUT     "accountID"
    NA
    INSN_S  "self"
    PUSH
    INSN_S  "testID"
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
    ROU     "$.nick"
    PUT     "nick"
    PUSH
    ASE
    PUT     "orderList"
    ASE
*/