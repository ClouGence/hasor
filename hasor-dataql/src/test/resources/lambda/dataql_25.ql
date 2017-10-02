
var decSexName = lambda: (u) -> {
    return u -> "title" ~
}

var userInfo = {
    "sex"   : 0,
    "title" : "男"
}

var foo = lambda: (userName) -> {

    return {
        "userName" : userName + ",二楼",
        "sex"      : decSexName(userInfo)~
    }
}

return foo('淘宝')~