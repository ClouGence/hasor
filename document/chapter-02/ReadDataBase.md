&emsp;&emsp;例如我们使用 c3p0 作为数据库连接池，连接并操作我们的数据库，第一步是创建 c3p0 数据源。
```java
ComboPooledDataSource dataSource = new ComboPooledDataSource();
dataSource.setDriverClass("......");
dataSource.setJdbcUrl("......");
dataSource.setUser("......");
dataSource.setPassword("......");
........
```

&emsp;&emsp; 接下来第二步初始化 Hasor 的数据库框架。
```java
AppContext appContext = Hasor.createAppContext(new Module() {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ......
        ComboPooledDataSource dataSource = ...
        apiBinder.tryCast(DataApiBinder.class).addDataSource(dataSource);
    }
});
```

&emsp;&emsp; 如果您想同时连接多个数据源，那么只需要在 `addDataSource` 的时候通过给定一个名称来加以区分。
```java
DataApiBinder dataBinder = apiBinder.tryCast(DataApiBinder.class);
dataBinder.addDataSource("dataSource1" ,dataSource1);
dataBinder.addDataSource("dataSource2" ,dataSource2);
```

&emsp;&emsp; 最后一步，在程序中使用数据库框架操作我们的数据库。Hasor 操作数据库主要是通过 JdbcOperations接口 或者 JdbcTemplate类来实现。当然您也可以结合其它数据库框架一同使用。例如 Hasor的官方网站就是使用 Hasor + MyBatis ORM 的方案。下面是通过 JdbcTemplate 形式操作数据库：
```java
public class MyDAO {
    @Inject
    private JdbcTemplate jdbcTemplate;
};
```

&emsp;&emsp; 如果之前您的环境中用的是多数据源，那么在 @Inject 时候通过名称绑定一下数据源即可使用。
```java
public class MyDAO {
    @Inject("dataSource1")
    private JdbcTemplate jdbcTemplateA;
    @Inject("dataSource2")
    private JdbcTemplate jdbcTemplateB;
};
```