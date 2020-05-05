hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@sql(apiMethod, apiPath)<%
        select * from interface_release where (pub_method = #{apiMethod} or pub_method = 'ANY') and pub_path = #{apiPath} and pub_status = 0 order by pub_release_time desc limit 1
    %>,
    "oracle"   : @@sql(apiMethod, apiPath)<%
        select * from (
            select * from interface_release where (pub_method = #{apiMethod} or pub_method = 'ANY') and pub_path = #{apiPath} and pub_status = 0 order by pub_release_time desc
        ) t where rownum <= 1
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

var dataTmp = queryExec(${apiMethod}, ${apiPath}) => [ # ];
if (!collect.isEmpty(dataTmp)) {
    return dataTmp => {
        "releaseID" : pub_id,
        "apiID"     : pub_api_id,
        "apiMethod" : pub_method,
        "apiPath"   : pub_path,
        "scriptType": pub_type,
        "script"    : pub_script,
        "optionData": json.fromJson((pub_option == null) ? '{}' : pub_option )
    };
} else {
    throw 404 , "not found api.";
}