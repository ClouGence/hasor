import 'net.hasor.dataql.fx.JsonUdfSource' as json;

var queryMap = {
    "mysql"  : @@sql_exec(historyId)<% select * from interface_release where pub_api_id= :historyId; %>,
    "pg"     : @@sql_exec()<% s %>,
    "oracle" : @@sql_exec()<% s %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${historyId}) => {
    "select"    : pub_method,
    "codeType"  : pub_type,
    "codeInfo"  : {
        "codeValue"   : pub_script,
        "requestBody" : json.fromJson(pub_sample).requestBody,
        "headerData"  : json.fromJson(pub_sample).headerData
    }
};