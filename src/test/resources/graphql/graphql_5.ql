findUserByIDAndType ( "userID" = uid, "status" = 1 ) {
    userID,
    nick,
    orderList : queryOrder ( "accountID" = uid) [
        {
            orderID,
            itemID,
            itemName
        }
    ]
}