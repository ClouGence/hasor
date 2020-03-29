import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@inner_dataway_sql(apiId)<% select * from interface_info where api_id= #{apiId}; %>
};

return queryMap[dbMapping](${apiId}) => {
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