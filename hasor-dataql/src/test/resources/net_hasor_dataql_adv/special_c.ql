var data = {
    "userInfo" : {
        "username" : "xxxxx",
        "password" : "pass"
    },
    "basicInfo" : {
        "name" : "马三",
        "sex" : "F"
    },
    "id" : 12345667
}

return data => {
    "userInfo" : userInfo => {
        "username",
        "password",
        "userId" : $.id,
        "id2" : @[0].id
    },
    "name"     : basicInfo.name
}