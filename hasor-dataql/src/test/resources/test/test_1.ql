var title = "性别："
var sexConse = {
    "W" : "男",
    "F" : "女"
}

var sexName= lambda: (sex) -> {
    if ( sex == -1 )
        return sexConse -> "W" ~    // 男
    else
        return sexConse -> "F" ~    // 女
    end
}

var decSexName = lambda: (userInfo) -> {

    var track = track(userInfo)~


    return sexName(userInfo -> "sex" ~)~ + title
}

var userInfo = {
    "sex" : 0
}

var foo = lambda: (userName) -> {

    return {
        "userName" : "'" + userName + ",二楼'",
        "sex"      : decSexName(userInfo)~
    }
}

return foo


// 说明：数据类型测试。
// 多维数组
// 各种路由表达式




