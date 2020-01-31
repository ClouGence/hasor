// 说明：定义lambda函数，返回 lambda 函数交给例子程序，例子程序通过 UDF 接口调用 DataQL 中定义的 lambda 函数。

var title = "性别："
var sexConstant = {
    "F" : "男",
    "M" : "女"
}

var sexName= (sex) -> {
    if ( sex == 0 )
        return title + sexConstant["M"]
    else
        return title + sexConstant["F"]
}

return sexName