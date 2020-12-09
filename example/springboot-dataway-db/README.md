#Hasor 例子

try run 
```js
hint FRAGMENT_SQL_DATA_SOURCE = "ds2"
var dd = @@sql()<%
    show tables;
%>
return [
    dd(),
    myName()
]
```