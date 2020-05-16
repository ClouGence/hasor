hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var queryMap = {
    "default" : @@sql()<%
        select * from interface_info order by api_create_time asc
    %>
};

var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];

return queryExec() => [
    {
        "id"      : api_id,
        "checked" : false,
        "select"  : api_method,
        "path"    : api_path,
        "status"  : api_status,
        "comment" : api_comment
    }
];