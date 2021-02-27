# Hasor 例子

- 使用数据库作为接口配置信息的存储（Demo 中使用的是 H2 内存数据库）
- 连接了另外两个 MySQL 数据源

try run

```js
hint FRAGMENT_SQL_DATA_SOURCE = "ds2"
var dd = @@sql() <%
    show tables;
%>
return [
    dd(),
    myName()
]
```