import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;

if (${_0} == null) {
    return "wrong args number."
}

var dataQuery = @@sql_exec(dataKey)<%
    select * from my_option where `key` = :dataKey
%>
var data = dataQuery(${_0}) => [ # ];

if ( collect.isEmpty(data) ) {
    return "not found key";
} else {
    return data[0]
}