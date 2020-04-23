import 'net.hasor.dataql.fx.basic.DateTimeUdfSource' as time;

var queryMap = {
    "default"   : @@sql(apiId)<% select * from interface_release where pub_api_id= #{apiId} order by pub_release_time desc limit 10; %>
};

return queryMap[dbMapping](${apiId}) => [
    {
        "historyId" : pub_id,
        "time"      : time.format(pub_release_time, "yyyy-MM-dd HH:mm:ss")
    }
];