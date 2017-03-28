fragment fUser on {
    userInfo : findUserByID ( userID = uid ) {
        name,
        age,
        nick
    },
    source : "GraphQL"
}
fragment fOrder on queryOrder( "accountID" = uid , ... ) [
    {
        orderID,
        itemID,
        itemName
    }
]

{
    user      : fUser,
    orderList : fOrder,
}