var updateInfoMap = {
    "mysql"     : @@inner_dataway_sql(apiId)<%update interface_info set api_status = 3, api_gmt_time = now() where api_id = :apiId;%>,
    "postgresql": @@inner_dataway_sql(apiId)<%update interface_info set api_status = 3, api_gmt_time = now() where api_id = :apiId;%>,
    "oracle"    : @@inner_dataway_sql(apiId)<%update interface_info set api_status = 3, api_gmt_time = now() where api_id = :apiId;%>
};

var updateReleaseMap = {
    "mysql"     : @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>,
    "postgresql": @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>,
    "oracle"    : @@inner_dataway_sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = :apiId;%>
};

var upd1 = updateInfoMap[`net.hasor.dataway.config.DataBaseType`](${apiId});
var upd2 = updateReleaseMap[`net.hasor.dataway.config.DataBaseType`](${apiId});
return upd1 > 0 && upd2 > 0;