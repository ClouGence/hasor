return findUserByID ({"userID" : 12345, "status" : 2}) {
    "name" : name2,
    "age",
    "nick"
}

/*
    NO
    LDC_D   12345
    PUT     "userID"
    LDC_D   2
    PUT     "status"
    CALL    "findUserByID",1
    ASM
    ROU     "name2"
    PUT     "name"
    ROU     "age"
    PUT     "age"
    ROU     "nick"
    PUT     "nick"
    ASE
    END
*/