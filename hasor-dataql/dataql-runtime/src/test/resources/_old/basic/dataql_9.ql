

return abc[123].abc['aaa'].abc.abc[123][132]



import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
import "net.hasor.test.dataql.udfs.UserOrderUdfSource" as u

return collect.filter(u.userList(), (obj) -> return ( obj.userID == 1 );) => {
    "name" : name2,
    "age",
    "nick"
}