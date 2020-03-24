var queryMap = {
    "mysql"  : @@sql_exec()<% select * from interface_info order by api_create_time asc; %>,
    "pg"     : @@sql_exec()<% s %>,
    "oracle" : @@sql_exec()<% s %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`]() => [
    {
        "id"      : api_id,
        "checked" : false,
        "select"  : api_method,
        "path"    : api_path,
        "status"  : api_status,
        "comment" : api_comment
    }
];