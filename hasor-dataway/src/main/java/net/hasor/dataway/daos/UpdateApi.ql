hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var updateMap = {
    "default"   : @@sql(data, apiSample, optionInfo)<%
        update interface_info set
            api_status   = #{data.newStatus},
            api_comment  = #{data.comment},
            api_type     = #{data.codeType},
            api_script   = #{data.codeValue},
            api_sample   = #{apiSample},
            api_option   = #{optionInfo},
            api_gmt_time = now()
        where
            api_id       = #{data.id}
    %>,
    "oracle"    : @@sql(data, apiSample, optionInfo)<%
        update interface_info set
            api_status   = #{data.newStatus},
            api_comment  = #{data.comment},
            api_type     = #{data.codeType},
            api_script   = #{data.codeValue},
            api_sample   = #{apiSample},
            api_option   = #{optionInfo},
            api_gmt_time = sysdate
        where
            api_id       = #{data.id}
    %>
};

var updateExec = (updateMap[dbMapping] == null) ? updateMap["default"] : updateMap[dbMapping];

var res = updateExec(
    ${postData},
    json.toJson({
        "requestBody" : ${postData}.requestBody,
        "headerData"  : ${postData}.headerData => [ # ]
    }),
    json.toJson(${postData}.optionInfo)
);

if (res == 1) {
    return ${postData}.id;
} else {
    throw 500 ,"update failed.";
}