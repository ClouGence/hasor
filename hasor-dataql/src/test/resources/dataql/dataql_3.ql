
// 说明：查询两个list，然后组成一个查询结果返回。

var userList = findUserByID ( foo(54321)~ ) [
    {
        "name",
        "age",
        "nick"
    }
]

var nameList = findUserByID (12345) [
    name2
]

return {
    "users" : userList,
    "names" : nameList
};