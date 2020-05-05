hint FRAGMENT_SQL_COLUMN_CASE = "lower";
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var insertMap = {
    "default"   : @@sql(data, apiSample, optionInfo)<%
        insert into interface_info (
            api_method,      api_path,       api_status,
            api_comment,     api_type,       api_script,
            api_schema,      api_sample,     api_option,
            api_create_time, api_gmt_time
        ) values (
            #{data.select},  #{data.apiPath},   0,
            #{data.comment}, #{data.codeType},  #{data.codeValue},
            '{}',            #{apiSample},      #{optionInfo},
            now(),           now()
        )
    %>,
    "oracle"   : @@sql(data, apiSample, optionInfo)<%
        insert into interface_info (
            api_method,      api_path,       api_status,
            api_comment,     api_type,       api_script,
            api_schema,      api_sample,     api_option,
            api_create_time, api_gmt_time
        ) values (
            #{data.select},  #{data.apiPath},   0,
            #{data.comment}, #{data.codeType},  #{data.codeValue},
            '{}',            #{apiSample},      #{optionInfo},
            sysdate,         sysdate
        )
    %>
};

var insertExec = (insertMap[dbMapping] == null) ? insertMap["default"] : insertMap[dbMapping];

var res = insertExec(
    ${postData},
    json.toJson({
        "requestBody" : ${postData}.requestBody,
        "headerData"  : ${postData}.headerData => [ # ]
    }),
    json.toJson(${postData}.optionInfo)
);

var queryMap = {
    "default"   : @@sql(apiMethod, apiPath)<%
        select api_id from interface_info where api_method= #{apiMethod} and api_path = #{apiPath} limit 1
    %>,
    "oracle"    : @@sql(apiMethod, apiPath)<%
        select * from (
            select api_id from interface_info where api_method= #{apiMethod} and api_path = #{apiPath}
        ) t where rownum <= 1
    %>
};

if (res == 1) {
    var queryExec = (queryMap[dbMapping] == null) ? queryMap["default"] : queryMap[dbMapping];
    return queryExec(${postData}.select, ${postData}.apiPath);
} else {
    throw 500 ,"insert failed.";
}