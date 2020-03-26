import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "mysql"     : @@inner_dataway_sql(apiId)<% select * from interface_info where api_id= :apiId; %>,
    "postgresql": @@inner_dataway_sql(apiId)<% select * from interface_info where api_id= :apiId; %>,
    "oracle"    : @@inner_dataway_sql(apiId)<% select * from interface_info where api_id= :apiId; %>
};

var dataFilter = (dat) -> {
    return dat.checked != null && dat.checked;
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiId}) => {
    "id"          : api_id,
    "select"      : api_method,
    "path"        : api_path,
    "status"      : api_status,
    "codeType"    : api_type,
    "requestBody" : json.fromJson(api_sample).requestBody,
    "headerData"  : collect.filter(json.fromJson(api_sample).headerData, dataFilter) => [
        {
            "checked" : true,
            "name"    : name,
            "value"   : value
        }
    ]
};