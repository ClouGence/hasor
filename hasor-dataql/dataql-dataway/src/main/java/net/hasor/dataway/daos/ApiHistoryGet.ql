import 'net.hasor.dataql.fx.JsonUdfSource' as json;

var queryMap = {
    "mysql"     : @@inner_dataway_sql(historyId)<% select * from interface_release where pub_api_id= :historyId; %>,
    "postgresql": @@inner_dataway_sql(historyId)<% select * from interface_release where pub_api_id= :historyId; %>,
    "oracle"    : @@inner_dataway_sql(historyId)<% select * from interface_release where pub_api_id= :historyId; %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${historyId}) => {
    "select"    : pub_method,
    "codeType"  : pub_type,
    "codeInfo"  : {
        "codeValue"   : pub_script_ori,
        "requestBody" : json.fromJson(pub_sample).requestBody,
        "headerData"  : json.fromJson(pub_sample).headerData
    }
};