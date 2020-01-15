// 定一个名称为 dataSet 的 sql 外部代码片段，脚本最后执行这个SQL片段并对 SQL 执行结果进行转换

var dataSet = @@sql(itemCode,status) <%

    select * from category where co_code like '%:itemCode%' and status = :status

%>

return dataSet("abc",true) => [
    {
        "id","name","code","body","params"
    }
]