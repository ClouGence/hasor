return findUserByID (12345) [
    {
        "name",
        "age",
        "nick"
    }
]

/*
    LDC_D  12345
    CALL    "findUserByID",1
    ASA
    NO
    ROU     "name"
    PUT     "name"
    ROU     "age"
    PUT     "age"
    ROU     "nick"
    PUT     "nick"
    PUSH
    ASE
    END
*/