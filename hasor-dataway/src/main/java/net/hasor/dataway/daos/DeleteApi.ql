var deleteInfoMap = {
    "default"   : @@sql(apiId)<%delete from interface_info where api_id = #{apiId};%>
};

var updateReleaseMap = {
    "default"   : @@sql(apiId)<%update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = #{apiId};%>
};
run updateReleaseMap[dbMapping](${apiId});
return deleteInfoMap[dbMapping](${apiId}) > 0;