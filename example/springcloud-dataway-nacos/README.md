# Spring Cloud Dataway with Nacos

- 工程
    - spring-cloud-gateway
    - spring-cloud-consumer
    - spring-cloud-provider
- 特点
    - 使用 Nacos 作为接口配置信息的存储
    - 连接了另外两个 MySQL 数据源
- consumer
    - http://localhost:8081/consumer/echo/abc
    - http://localhost:8081/consumer/api/abc?message=HelloWord
- provider
    - http://localhost:8082/provider/echo/abc
    - http://localhost:8082/api/abc?message=Helloword (需要到Dataway里面新建)
    - http://localhost:8082/interface-ui/ (Dataway界面)
- gateway
    - http://localhost:8080/consumer/echo/abc (代理consumer)
    - http://localhost:8080/consumer/api/abc?message=HelloWord (代理consumer)
    - http://localhost:8080/provider/echo/abc (代理provider)
    - http://localhost:8080/api/abc?message=abc (代理provider)
    - http://localhost:8080/interface-ui/ (代理provider)

try run

```js
hint
FRAGMENT_SQL_DATA_SOURCE = "ds2"
var dd = @@sql() < %
    show tables;
%>
return [
    dd(),
    myName()
]
```