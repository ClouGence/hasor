import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var updateMap = {
    "mysql"     : @@inner_dataway_sql(apiID, apiStatus, apiComment, apiType, apiScript, apiSample)<%
        update interface_info set
            api_status   = :apiStatus,
            api_comment  = :apiComment,
            api_type     = :apiType,
            api_script   = :apiScript,
            api_sample   = :apiSample,
            api_gmt_time = now()
        where
            api_id       = :apiID
    %>,
    "postgresql": @@inner_dataway_sql(apiID, apiStatus, apiComment, apiType, apiScript, apiSample)<%
        update interface_info set
            api_status   = :apiStatus,
            api_comment  = :apiComment,
            api_type     = :apiType,
            api_script   = :apiScript,
            api_sample   = :apiSample,
            api_gmt_time = now()
        where
            api_id       = :apiID
    %>,
    "oracle"    : @@inner_dataway_sql(apiID, apiStatus, apiComment, apiType, apiScript, apiSample)<%
        update interface_info set
            api_status   = :apiStatus,
            api_comment  = :apiComment,
            api_type     = :apiType,
            api_script   = :apiScript,
            api_sample   = :apiSample,
            api_gmt_time = now()
        where
            api_id       = :apiID
    %>
};

var res = updateMap[`net.hasor.dataway.config.DataBaseType`](
    ${postData}.id,
    ${postData}.newStatus,
    ${postData}.comment,
    ${postData}.codeType,
    ${postData}.codeValue,
    json.toJson({
        "requestBody" : ${postData}.requestBody,
        "headerData"  : ${postData}.headerData => [ # ]
    })
);

if (res == 1) {
    return ${postData}.id;
} else {
    throw 500 ,"update failed.";
}