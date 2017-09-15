
// 说明：构造一个对象作为参数穿递给 findUserByID 函数。
//      函数的返回值作为对象，只保留 name2、age、nick 三个属性。
//      其中 name2 属性更名为 name。

return findUserByID ({"userID" : 12345, "status" : 2}) {
    "name" : name2,
    "age",
    "nick"
}