hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var updateMap = {
    "default" : @@sql(apiID, apiSchema)<%
        update interface_info set
            api_schema  = #{apiSchema}, api_gmt_time = now()
        where
            api_id      = #{apiID}
    %>,
    "oracle" : @@sql(apiID, apiSchema)<%
        update interface_info set
            api_schema  = #{apiSchema}, api_gmt_time = sysdate
        where
            api_id      = #{apiID}
    %>,
    "sqlserver2012" : @@sql(apiID, apiSchema)<%
        update interface_info set
            api_schema  = #{apiSchema}, api_gmt_time = getdate()
        where
            api_id      = #{apiID}
    %>,
    "postgresql" : @@sql(apiID, apiSchema)<%
        update interface_info set
            api_schema  = #{apiSchema}, api_gmt_time = now()
        where
            api_id      = cast(#{apiID} as integer)
    %>
};

var updateExec = (updateMap[dbMapping] == null) ? updateMap["default"] : updateMap[dbMapping];

var res = updateExec(
    ${apiID},
    json.toJson({
        "requestSchema" : ${requestSchema},
        "responseSchema"  : ${responseSchema}
    })
);

if (res == 1) {
    return ${apiID};
} else {
    throw 500 ,"update failed.";
}