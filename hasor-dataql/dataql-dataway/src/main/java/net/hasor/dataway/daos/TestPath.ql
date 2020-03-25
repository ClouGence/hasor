var queryMap = {
    "mysql"     : @@inner_dataway_sql(apiPath)<% select count(*) from interface_info where api_path= :apiPath; %>,
    "postgresql": @@inner_dataway_sql(apiPath)<% select count(*) from interface_info where api_path= :apiPath; %>,
    "oracle"    : @@inner_dataway_sql(apiPath)<% select count(*) from interface_info where api_path= :apiPath; %>
};

return queryMap[`net.hasor.dataway.config.DataBaseType`](${apiPath}) > 0