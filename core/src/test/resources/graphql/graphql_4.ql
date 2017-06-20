return findUserByID (12345) [
    name2
]

/*
    INSN_N  12345
    CALL    "findUserByID",1
    ASA
    ROU     "name2"
    PUSH
    ASE
    END
*/