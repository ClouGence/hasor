import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var updateMap = {
    "default"   : @@sql(data, apiSample)<%
        update interface_info set
            api_status   = #{data.newStatus},
            api_comment  = #{data.comment},
            api_type     = #{data.codeType},
            api_script   = #{data.codeValue},
            api_sample   = #{apiSample},
            api_gmt_time = now()
        where
            api_id       = #{data.id}
    %>
};

var res = updateMap[dbMapping](
    ${postData},
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