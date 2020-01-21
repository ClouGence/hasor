var dataQuery = @@sql_exec()<%
    select * from my_option
%>

return dataQuery(${_0}) => [ key ];