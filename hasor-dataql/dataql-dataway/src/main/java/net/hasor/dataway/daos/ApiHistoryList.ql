import 'net.hasor.dataql.fx.DateTimeUdfSource' as time;

var queryMap = {
    "mysql"  : @@sql_exec(apiId)<% select * from interface_release where pub_api_id= :apiId order by pub_release_time desc; %>,
    "pg"     : @@sql_exec()<% s %>,
    "oracle" : @@sql_exec()<% s %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiId}) => [
    {
        "historyId" : pub_id,
        "time"      : time.format(pub_release_timem, "yyyy-MM-dd hh:mm:ss")
    }
];