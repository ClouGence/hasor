var dataQuery = @@sql()<%
    select * from my_option
%>

return dataQuery(${_0}) => [ key ];