//
// 说明：定义lambda函数，返回 lambda 函数交给例子程序，例子程序通过 UDF 接口调用引擎执行 lambda 函数。

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

return sexName