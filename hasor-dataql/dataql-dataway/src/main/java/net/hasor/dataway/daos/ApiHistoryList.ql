import 'net.hasor.dataql.fx.DateTimeUdfSource' as time;

var queryMap = {
    "mysql"  : @@inner_dataway_sql(apiId)<% select * from interface_release where pub_api_id= :apiId order by pub_release_time desc; %>,
    "pg"     : @@inner_dataway_sql()<% s %>,
    "oracle" : @@inner_dataway_sql()<% s %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiId}) => [
    {
        "historyId" : pub_id,
        "time"      : time.format(pub_release_timem, "yyyy-MM-dd hh:mm:ss")
    }
];