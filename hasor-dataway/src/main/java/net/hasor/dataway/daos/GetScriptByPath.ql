var queryMap = {
    "default"   : @@inner_dataway_sql(apiPath)<%select pub_script from interface_release where pub_path = :apiPath and pub_status = 0 order by pub_release_time desc limit 1;%>
};

return queryMap[dbMapping](${apiPath});