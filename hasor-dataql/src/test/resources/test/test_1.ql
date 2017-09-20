

var args = "性别：";
var fo1 = lambda: (obj) -> {
    return args + "[女]";
}

var uuu = lambda : (a) -> {
    return fo1(a)~ + "END"
}

var ffff = lambda : (b) -> {
    return uuu(b)~ + "~~~~"
}


 return ffff;


// 说明：数据类型测试。
// 闭包特性
// 多维数组
// 各种路由表达式


var title = "性别："
var sexConse = {
    "W" : "男",
    "F" : "女"
}

var sexName= lambda: (sex) -> {
    if ( sex == 0 )
        return sexConse -> "W" ~    // 男
    else
        return sexConse -> "F" ~    // 女
    end
}

var decSexName = lambda: (userInfo) -> {
    return sexName(userInfo -> "sex" ~)~ + title
}


var userInfo = {
    "sex" : 0
}

var foo = lambda: (userName) -> {
    return {
        "userName" : userName,
        "sex"      : descSexName(userInfo)~
    }
}

return decSexName


