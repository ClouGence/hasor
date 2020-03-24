import 'net.hasor.dataql.fx.JsonUdfSource' as json;

var queryMap = {
    "mysql"  : @@sql_exec(apiId)<% select * from interface_info where api_id= :apiId; %>,
    "pg"     : @@sql_exec()<% s %>,
    "oracle" : @@sql_exec()<% s %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiId}) => {
    "id"          : api_id,
    "select"      : api_method,
    "path"        : api_path,
    "status"      : api_status,
    "apiComment"  : api_comment,
    "codeType"    : api_type,
    "codeInfo"    : {
        "codeValue"   : api_script,
        "requestBody" : json.fromJson(api_sample).requestBody,
        "headerData"  : json.fromJson(api_sample).headerData
    }
};