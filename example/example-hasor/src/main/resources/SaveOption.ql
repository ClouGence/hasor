import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
import 'net.hasor.dataql.sdk.IdentifierUdfSource' as ids;
import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;
import @'ReadOption.ql' as readOption


var insertData = @@sql_exec(data)<%
    insert into my_option (
        `id`,`key`,`value`,`desc`,`create_time`,`modify_time`
    ) values (
        #{data.id},
        #{data.key},
        #{data.value},
        #{data.desc},
        now(),
        now()
    );
%>

var data = readOption(${_0});
if (data.ID == null) {
    var newData = {
        "id"    : ids.uuid(),
        "key"   : ${_0},
        "value" : ${_1},
        "desc"  : (${_0} + " - desc")
    };
    var result = insertData(newData);
    return "insert ok. -> " + result;
} else {
    return "insert failed, key exist."
}