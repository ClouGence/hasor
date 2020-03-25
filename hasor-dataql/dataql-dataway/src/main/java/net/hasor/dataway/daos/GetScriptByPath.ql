var queryMap = {
    "mysql"     : @@inner_dataway_sql(apiPath)<%select pub_script from interface_release where pub_path = :apiPath and pub_status = 0 order by pub_release_time desc limit 1;%>,
    "postgresql": @@inner_dataway_sql(apiPath)<%select pub_script from interface_release where pub_path = :apiPath and pub_status = 0 order by pub_release_time desc limit 1;%>,
    "oracle"    : @@inner_dataway_sql(apiPath)<%select pub_script from interface_release where pub_path = :apiPath and pub_status = 0 order by pub_release_time desc limit 1;%>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiPath});