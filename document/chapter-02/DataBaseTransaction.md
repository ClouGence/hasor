&emsp;&emsp; 在您使用 DataApiBinder 添加数据源时候，它会为您自动的配置相关的数据库事务管理器，您不需要为了事务管理做任何多余配置。Hasor 的事务管理十分强大，它支持多达七种事务传播属性以及全部的事务隔离级别。即便是配置了多数据源下也可以很好的为每个数据源提供独立的事务控制功能。

&emsp;&emsp; 在 Hasor 中进行事务控制有三个途径：第一种，通过 `@Transactional` 注解方式。第二种，通过 `TransactionTemplate` 接口。第三种，通过 `TransactionManager` 事务管理器接口手动控制事务。在本节会展示第一种注解方式的事务控制。

&emsp;&emsp; 注解方式。这种方式用途比较广泛，用起来也十分方便。只要在方法上加上一个注解，当方法之行完毕，同时没有异常抛出时，事务就会被递交到数据库。具体示例如下：
```java
public class TradeService {
    @Transactional
    public boolean payItem(long itemId , CreditCard creditCard){
        ....
    }
}
```

&emsp;&emsp; 嵌套事务，Hasor 的事务管理是支持嵌套事务的。使用嵌套事务时，您不需要额外配置任何信息。