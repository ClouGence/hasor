var dataQuery = @@sql_query()<%
    select * from MyOption
%>

return dataQuery(${_0}) => [ KEY ];