var queryMap = {
    "default"   : @@inner_dataway_sql(apiPath)<% select count(*) from interface_info where api_path= :apiPath; %>
};

return queryMap[dbMapping](${apiPath}) > 0