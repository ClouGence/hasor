var deleteInfoMap = {
    "mysql"     : @@inner_dataway_sql(apiId)<%delete from interface_info where api_id = :apiId;%>,
    "postgresql": @@inner_dataway_sql(apiId)<%delete from interface_info where api_id = :apiId;%>,
    "oracle"    : @@inner_dataway_sql(apiId)<%delete from interface_info where api_id = :apiId;%>
};

var updateReleaseMap = {
    "mysql"     : @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>,
    "postgresql": @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>,
    "oracle"    : @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>
};

run updateReleaseMap[`net.hasor.dataway.config.DataBaseType`](${apiId});
return deleteInfoMap[`net.hasor.dataway.config.DataBaseType`](${apiId}) > 0;