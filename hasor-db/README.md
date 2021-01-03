# JDBC 框架

&emsp;&emsp;Hasor-DB 是一款基于jdbc的数据库框架，针对 Spring-JDBC 做了80% 缩减之后在进行大量重构才有了它。可以说是翻版的 SpringJDBC。

----------
## 特性
01. 提供 JDBC 操作接口 90% 兼容 SpringJDBC。
02. 与 Spring 一样，提供七种事务传播属性的控制。
03. 支持多种事务控制方式包括：手动事务控制、注解式声明事务、TransactionTemplate模板事务。
04. 支持多数据源（不支持分布式事务）

## 样例

```java
public class JdbcDemo {
    public static void main(String[] args) {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            DataSource dataSource = ... //创建数据源
            apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
        });

        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        jdbcTemplate.queryForInt("select count(1) from TB_User");
    }
}
```

## 源码说明

- 单一数据库无法满足 hasor-db 的单测试要求。因此建议同时启动下列在开发环境中使用了4 个主流数据库
- 例：MySQL 8 驱动层面不支持 JDBC 时区类型，因此采用 Oracle 替代测试。
- 
- docker-compose.yml（MySQL、PG、Oracle、MSSQL）
