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
        "addressList" : foreach( addressList ) [
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