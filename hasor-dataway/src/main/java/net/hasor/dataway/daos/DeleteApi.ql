hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var deleteInfoMap = {
    "default"   : @@sql(apiId)<%
        delete from interface_info where api_id = #{apiId}
    %>,
    "postgresql" : @@sql(apiId)<%
        delete from interface_info where api_id = cast(#{apiId} as integer)
    %>
};

var updateReleaseMap = {
    "default"   : @@sql(apiId)<%
        update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = #{apiId}
    %>,
    "postgresql" : @@sql(apiId)<%
        update interface_release set pub_status = 1 where pub_status = 0 and pub_api_id = cast(#{apiId} as integer)
    %>
};

var deleteInfoExec    = (deleteInfoMap[dbMapping]    == null) ? deleteInfoMap["default"]    : deleteInfoMap[dbMapping];
var updateReleaseExec = (updateReleaseMap[dbMapping] == null) ? updateReleaseMap["default"] : updateReleaseMap[dbMapping];

run updateReleaseExec(${apiId});
return deleteInfoExec(${apiId}) > 0;