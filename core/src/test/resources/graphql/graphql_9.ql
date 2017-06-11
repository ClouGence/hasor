var fOrder = queryOrder (uid) [
    {
        "orderID",
        "itemID",
        "itemName"
    }
];

var fUser = {
    "userInfo" : findUserByID (uid) {
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