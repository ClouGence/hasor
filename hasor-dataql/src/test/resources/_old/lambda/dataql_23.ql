//
// 说明：闭包 lambda函数。

var title = "性别："
var sexConse = {
    "W" : "男",
    "F" : "女"
}

var sexName= (sex) -> {

    var foo = () -> return "^_^ ：";

    if ( sex == 0 )
        return sexConse["W"] + foo()
    else
        return sexConse["F"] + foo()
}

return sexName