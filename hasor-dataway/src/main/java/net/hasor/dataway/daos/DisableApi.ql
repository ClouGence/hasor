hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var updateInfoMap = {
    "default"     : @@sql(apiId)<%
        update interface_info set api_status = 3, api_gmt_time = now() where api_id = #{apiId}
    %>,
    "oracle"      : @@sql(apiId)<%
        update interface_info set api_status = 3, api_gmt_time = sysdate where api_id = #{apiId}
    %>,
    "sqlserver2012" : @@sql(apiId)<%
        update interface_info set api_status = 3, api_gmt_time = getdate() where api_id = #{apiId}
    %>
};

var updateReleaseMap = {
    "default"   : @@sql(apiId)<%
        update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = #{apiId}
    %>
};

var updateInfoExec    = (updateInfoMap[dbMapping]    == null) ? updateInfoMap["default"]    : updateInfoMap[dbMapping];
var updateReleaseExec = (updateReleaseMap[dbMapping] == null) ? updateReleaseMap["default"] : updateReleaseMap[dbMapping];

var upd1 = updateInfoExec(${apiId});
var upd2 = updateReleaseExec(${apiId});
return upd1 > 0 && upd2 > 0;