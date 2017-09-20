// 基础功能演示
//
//      查询 UDF 并筛选三个属性值返回，查询时入参为一个两个元素的 Map。
//

return findUserByID ({"userID" : 12345, "status" : 2}) {
    "name" : name2,
    "age",
    "nick"
}