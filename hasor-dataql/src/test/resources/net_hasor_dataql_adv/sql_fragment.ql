
var a = @@sql(item_code) <%

    select * from category where co_code like '%:item_code%'

%>

return a();