
// 说明：基于 “dataql_1.ql”、“dataql_2.ql” 两个例子上稍微复杂一点的查询语句。

var user_id = 12345;

var fOrder = queryOrder ( user_id ) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
];

var fUser = {
    "userInfo" : findUserByID (user_id) {
        "userID",
        "status",
        "addressList" : addressList -> [   // 使用取值的方法来处理属性数据
            {
                "zip",
                "address"
            }
        ]
    },
    "source" : "DataQL"
};

return {
    "user" : fUser,
    "orderList" : fOrder
};