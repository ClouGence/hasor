var addrList_1 = findUserByID (12345) -> "addressList" [
    {
        "code",
        "address"
    }
]

var data = findUserByID (12345)~;
var addrList_2 = data -> "addressList" [
    {
        "code",
        "address"
    }
]

return {
    "addrA" : addrList_1,
    "addrB" : addrList_2
};