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