import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;

// 查询是否还存在
var dataQuery = @@sql(optKey)<%
    select * from my_option where "key" = #{optKey}
%>
var data = dataQuery(${_0}) => [ # ];

if ( collect.isEmpty(data) ) {
    return "there is no data.";
}

// 删除
var dataDelete = @@sql(optKey)<%
    delete from my_option where "key" = #{optKey}
%>
run dataDelete(${_0})

// 检测删除是否成功
var data = dataQuery(${_0}) => [ # ];
if ( collect.isEmpty(data) ) {
    return "delete ok.";
} else {
    return "delete failed.";
}