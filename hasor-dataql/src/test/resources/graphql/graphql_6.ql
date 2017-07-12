return findUserByID ({"userID" : foo (sid)~ , "status" : 1}) {
    "userID",
    "nick"
}

/*
    NO
    ROU     "sid"
    CALL    "foo",1
    ASO
    ASE
    PUT     "userID"
    LDC_D   1
    PUT     "status"
    CALL    "findUserByID",1
    ASO
    ROU     "userID"
    PUT     "userID"
    ROU     "nick"
    PUT     "nick"
    ASE
    END
*/