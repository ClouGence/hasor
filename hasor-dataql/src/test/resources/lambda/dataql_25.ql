
var decSexName = lambda: (userInfo) -> {
    return userInfo -> "title" ~
}

var userInfo = {
    "sex"   : 0,
    "title" : "title"
}

var foo = lambda: (userName) -> {

    return {
        "userName" : "'" + userName + ",二楼'",
        "sex"      : decSexName(userInfo)~
    }
}

return foo