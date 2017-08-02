return {
    "userInfo" :  {
        "info" : findUserByID (12345) {
            "name",
            "age",
            "nick"
        },
        "nick" : ${info.nick}
    },
    "source" : "DataQL"
}

/*
    NO
    NO
    LDC_D   12345
    CALL    "findUserByID",1
    ASM
    ROU     "name"
    PUT     "name"
    ROU     "age"
    PUT     "age"
    ROU     "nick"
    PUT     "nick"
    ASE
    PUT     "info"
    ROU     "info.nick"
    PUT     "nick"
    PUT     "userInfo"
    LDC_S   "DataQL"
    PUT     "source"
    END
*/
