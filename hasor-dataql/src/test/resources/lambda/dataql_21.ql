//
// 说明：定义多个lambda函数，lambda函数之间存在互相调用。


var args = "性别：";
var sexToStr = (obj) -> {
    if (obj == 'F')
        return args + "女";
    else
        return args + "男";
    end
}

var sexToStrAndOri = (a) -> {
    return sexToStr(a) + " - " + a
}

var convert =  (b) -> {
    return "~~~~" + sexToStrAndOri(b)
}

return findUserByID ({"userID" : 12345, "status" : 2}) => {
    "name"  : name2,
    "age",
    "nick",
    "sex"   : convert(sex)
}