return findUserByID ({"userID" : 12345, "status" : 2}) {
    "name" : name2,
    "age",
    "nick"
}

/*
    NO
    INSN_N  12345
    PUT     "userID"
    INSN_N  2
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