hint FRAGMENT_SQL_OPEN_PACKAGE = "off"
hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var queryMap = {
    "default"   : @@sql(apiPath)<%
        select pub_script from interface_release where pub_path = #{apiPath} and pub_status = 0 order by pub_release_time desc
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec(${apiPath});