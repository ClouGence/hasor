import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.DateTimeUdfSource' as time;
import 'net.hasor.dataql.fx.basic.StateUdfSource' as state;


var dataQuery = @@sql(dataKey)<%
    select * from my_option where "key" = #{dataKey}
%>
var data = dataQuery(${_0}.key) => [ # ];

if ( !collect.isEmpty(data) ) {
    return "insert failed, key exist."
}


var insertData = @@sql(data)<%
    insert into my_option (
        "id","key","value","desc","create_time","modify_time"
    ) values (
        #{data.id},
        #{data.key},
        #{data.value},
        #{data.desc},
        now(),
        now()
    );
%>

var newData = {
    "id"    : state.uuid(),
    "key"   : ${_0}.key,
    "value" : ${_0}.value,
    "desc"  : ${_0}.desc
};
var result = insertData(newData);
return "insert ok. -> " + result;
