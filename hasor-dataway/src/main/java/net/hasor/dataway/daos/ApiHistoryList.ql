hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.DateTimeUdfSource' as time;

var queryMap = {
    "default" : @@sql(apiId)<%
        select * from interface_release where pub_api_id = #{apiId} order by pub_release_time desc limit 10
    %>,
    "oracle" : @@sql(apiId)<%
        select * from (
            select * from interface_release where pub_api_id = #{apiId} order by pub_release_time desc
        ) t where rownum < 10
    %>,
    "sqlserver2012" : @@sql(apiId)<%
        select * from interface_release where pub_api_id = #{apiId} order by pub_release_time desc offset 0 rows fetch next 10 rows only
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec(${apiId}) => [
    {
        "historyId" : pub_id,
        "time"      : time.format(pub_release_time, "yyyy-MM-dd HH:mm:ss")
    }
];