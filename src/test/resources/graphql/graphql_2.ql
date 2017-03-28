{
    userInfo : {
        info :findUserByID ( userID = 12345 ) {
            name,
            age,
            nick
        },
        nick : info.nick
    },
    source : "GraphQL"
}