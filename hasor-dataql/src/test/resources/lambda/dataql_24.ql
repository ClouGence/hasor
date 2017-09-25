//
// 说明：闭包 lambda函数。

var title = "性别："
var sexConse = {
    "W" : "男",
    "F" : "女"
}

var sexName= lambda: (sex) -> {

    var foo = lambda : () -> return "^_^ ：";

    if ( sex == 0 )
        return sexConse -> "W" ~ + foo()~   // 男
    else
        return sexConse -> "F" ~  + foo()~  // 女
    end
}

return sexName