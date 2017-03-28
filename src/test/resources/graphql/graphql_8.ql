fragment fUser on findUserByID( "userID" = uid ) {
    userID,
    name,
    age,
    nick,
}
fragment fOrder on queryOrder( "accountID" = uid , ... ) [
    {
        orderID,
        itemID,
        itemName
    }
]

{
    user      : fUser{},
    orderList : fOrder{},
}