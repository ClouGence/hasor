// 基础功能演示
//  - u.userList() 提供了一个 测试数据集，取第一条数据做结果转换

import "net.hasor.test.dataql.udfs.UserOrderUdfSource" as u

return u.userList() => {
    "name" : name2,
    "age",
    "nick"
}