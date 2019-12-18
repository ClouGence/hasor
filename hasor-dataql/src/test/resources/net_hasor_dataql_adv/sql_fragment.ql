// 定一个名称为 dataSet 的 sql 外部代码片段，脚本最后执行这个SQL片段并对 SQL 执行结果进行转换

var dataSet = @@sql(item_code) <%

    select * from category where co_code like '%:item_code%'

%>

return dataSet() => [
    {
        "id","name","code"
    }
]