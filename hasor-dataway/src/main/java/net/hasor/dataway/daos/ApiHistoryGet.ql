hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@sql(historyId)<%
        select * from interface_release where pub_id = #{historyId}
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec(${historyId}) => {
    "select"    : pub_method,
    "codeType"  : pub_type,
    "codeInfo"  : {
        "codeValue"   : pub_script_ori,
        "requestBody" : json.fromJson(pub_sample).requestBody,
        "headerData"  : json.fromJson(pub_sample).headerData
    },
    "optionData"  : json.fromJson((pub_option == null) ? '{}' : pub_option )
};