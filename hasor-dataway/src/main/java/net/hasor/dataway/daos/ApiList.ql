var queryMap = {
    "default"   : @@inner_dataway_sql()<% select * from interface_info order by api_create_time asc; %>
};

return queryMap[dbMapping]() => [
    {
        "id"      : api_id,
        "checked" : false,
        "select"  : api_method,
        "path"    : api_path,
        "status"  : api_status,
        "comment" : api_comment
    }
];