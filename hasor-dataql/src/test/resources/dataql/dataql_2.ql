
// 说明：构造一个包含 “userInfo、source” 两个属性的对象作为查询结果。其中 source 为固定值 "DataQL"，而 userInfo 属性值另外构造。
//      userInfo 的构造 同样有两个属性，其中 info 属性是 findUserByID 的返回值。
//      而另外一个 nick 属性值的来源是 info 属性中的 nick 字段。

return {
    "userInfo" :  {
        "info" : findUserByID (12345) {
            "name",
            "age",
            "nick"
        },
        "nick" : ${info.nick}
    },
    "source" : "DataQL"
}