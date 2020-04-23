var queryMap = {
    "default"   : @@sql(apiPath)<% select count(*) from interface_info where api_path= #{apiPath}; %>
};

return queryMap[dbMapping](${apiPath}) > 0