# JDBC 框架

&emsp;&emsp;Hasor-DB 是一款基于jdbc的数据库访问框架，保留了大部分 Spring-JDBC 接口能力，完全重写了实现才有了它。可以说 Spring-JDBC + Lambda + ORM 等于 hasor-db。

----------
## 特性
01. 全面支持 JDBC 4.2 各种数据类型
02. 全面支持 Java8 中的各种时间类型
03. 注解化 ORM 能力
04. 七种事务传播行为的控制（同 Spring 一样）
05. 支持 MybatisPlus 风格的 Lambda SQL 生成器
06. JDBC 操作接口 90% 以上兼容 SpringJDBC 写法
07. 多种事务控制方式包括
08. 手动事务
09. 注解式声明
10. TransactionTemplate 模板事务
11. 支持多数据源（不支持分布式事务）
12. 可完全独立于 Hasor 之外单独使用

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
