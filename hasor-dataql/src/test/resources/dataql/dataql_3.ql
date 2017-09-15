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