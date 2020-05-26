hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default" : @@sql()<%
        select tab.*,info.api_comment from (
            select
                pub_id, pub_api_id, pub_method, pub_path, pub_type, pub_sample, pub_schema, pub_option
            from interface_release where pub_id in (
                select max(pub_id) from interface_release where pub_status = 0 group by pub_api_id
            )
        ) tab left join interface_info info on tab.pub_api_id = info.api_id
        order by tab.pub_api_id asc
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec() => [
    {
        "releaseID" : pub_id,
        "apiID"     : pub_api_id,
        "apiMethod" : pub_method,
        "apiPath"   : pub_path,
        "scriptType": pub_type,
        "sample"    : json.fromJson((pub_sample == null) ? '{}' : pub_sample ),
        "comment"   : api_comment,
        "optionData": json.fromJson((pub_option == null) ? '{}' : pub_option ),
        "apiSchema" : json.fromJson((pub_schema == null) ? '{}' : pub_schema )
    }
];