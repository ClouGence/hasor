import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;
import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;

var queryMap = {
    "default"   : @@inner_dataway_sql(apiMethod, apiPath)<%select * from interface_release where pub_method = #{apiMethod} and pub_path = #{apiPath} and pub_status = 0 order by pub_release_time desc limit 1;%>
};

var dataTmp = queryMap[dbMapping](${apiMethod}, ${apiPath}) => [ # ];
if (!collect.isEmpty(dataTmp)) {
    return dataTmp => {
        "releaseID" : pub_id,
        "apiID"     : pub_api_id,
        "apiMethod" : pub_method,
        "apiPath"   : pub_path,
        "scriptType": pub_type,
        "script"    : pub_script
    };
} else {
    throw 404 , "not found api.";
}