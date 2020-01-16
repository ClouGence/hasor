import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
import 'net.hasor.dataql.sdk.IdentifierUdfSource' as ids;
import 'net.hasor.dataql.sdk.DateTimeUdfSource' as time;
import @'ReadOption.ql' as readOption

var data = readOption(${_0});

if (data.ID == null) {
    var newData = [ {
        "id"    : ids.uuid(),
        "key"   : ${_0},
        "value" : ${_1},
        "desc"  : (${_0} + " - desc"),
        "create_time" : time.now(),
        "modify_time" : time.now()
    } ];
    run jdbc.batchInsert('MyOption',newData);
    return "insert ok.";
} else {
    return "insert failed, key exist."
}