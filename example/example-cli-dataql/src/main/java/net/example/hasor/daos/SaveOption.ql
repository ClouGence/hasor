import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.IdentifierUdfSource' as ids;
import 'net.hasor.dataql.fx.basic.DateTimeUdfSource' as time;
import @'ReadOption.ql' as readOption


var insertData = @@sql(data)<%
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
