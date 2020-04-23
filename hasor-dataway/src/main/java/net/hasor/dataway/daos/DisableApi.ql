var updateInfoMap = {
    "default"   : @@sql(apiId)<%update interface_info set api_status = 3, api_gmt_time = now() where api_id = #{apiId};%>
};

var updateReleaseMap = {
    "default"   : @@sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = #{apiId};%>
};

var upd1 = updateInfoMap[dbMapping](${apiId});
var upd2 = updateReleaseMap[dbMapping](${apiId});
return upd1 > 0 && upd2 > 0;