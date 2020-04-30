hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var queryMap = {
    "default"   : @@sql(apiPath)<%
        select count(*) from interface_info where api_path = #{apiPath}
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec(${apiPath}) > 0