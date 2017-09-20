//
// 说明：定义lambda函数，并在查询时使用 lambda 函数来转化数据。


var args = "性别：";
var sexToStr = lambda: (obj) -> {
    if (obj == 'F')
        return args + "女";
    else
        return args + "男";
    end
}

return findUserByID ({"userID" : 12345, "status" : 2}) {
    "name"  : name2,
    "age",
    "nick",
    "sex"   : sexToStr(sex)~
}