hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@sql(apiId)<%
        select * from interface_info where api_id = #{apiId}
    %>,
    "postgresql" : @@sql(apiId)<%
        select * from interface_info where api_id = cast(#{apiId} as integer)
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

var dataFilter = (dat) -> {
    return dat.checked != null && dat.checked;
};

return queryExec(${apiId}) => {
    "id"         : api_id,
    "select"     : api_method,
    "path"       : api_path,
    "status"     : api_status,
    "codeType"   : api_type,
    "optionData" : json.fromJson((api_option == null) ? '{}' : api_option )
};