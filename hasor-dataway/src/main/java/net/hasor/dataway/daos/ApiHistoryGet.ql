import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@sql(historyId)<% select * from interface_release where pub_id= #{historyId}; %>
};

return queryMap[dbMapping](${historyId}) => {
    "select"    : pub_method,
    "codeType"  : pub_type,
    "codeInfo"  : {
        "codeValue"   : pub_script_ori,
        "requestBody" : json.fromJson(pub_sample).requestBody,
        "headerData"  : json.fromJson(pub_sample).headerData
    }
};