&emsp;&emsp;首先开始一个项目中会有各种类，类与类之间还有复杂的依赖关系。如果没有 IoC 框架的情况下，我们需要手工的创建它们。通常在你脑海里会呈现一张庞大的对象图，你必须要保证它们依赖没有错误。但这是十分繁琐而且是很容易出错的。

&emsp;&emsp;为了说明如何使用 Hasor 的 IoC 我们启动一个 TradeService 类用来实现下单操作，而该类会依赖到 InventoryService 库存服务，PayService 付款服务以及 ItemService 商品服务。
```java
public class TradeService {
    @Inject
    private PayService      payService;
    @Inject
    private ItemService     itemService;

    public boolean payItem(long itemId , CreditCard creditCard){
        ...
    }
}
```

&emsp;&emsp;接下来我们创建 AppContext 并通过它将 TradeService 所需要的服务对象注入进去即可。接下来您只需要通过下面这个代码完成 TradeService 对象的创建，Hasor 会自动完成依赖注入。
```java
AppContext appContext = Hasor.createAppContext();
TradeService tradeService = appContext.getInstance(TradeService.class);
....
tradeService.payItem(......);
```

&emsp;&emsp;如果 PayService 为一个接口而非具体的实现类，那么您可以在 PayService 接口上通过 @ImplBy 注释标记出它的实现类即可。
```java
@ImplBy(PayServiceImpl.class)
public interface PayService {
    ...
}
```
&emsp;&emsp;如果我们需要 TradeService 是一个单列那么在类上标记 @Singleton 注解即可。
```java
@Singleton
public class TradeService {
    ...
}
```

&emsp;&emsp;接下来如果我们希望 TradeService 在创建时做一些初始化工作，您只需要在初始化方法上加上 @Init 注解即可。
```java
@Singleton
public class TradeService {
    @Init
    public void initMethod(){
        ...
    }
}
```
&emsp;&emsp;同样的例子，我们还可以通过 Module 的方式进行实现 `无侵入` 的注入。
```java
AppContext appContext = Hasor.createAppContext(new Module() {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(TradeService.class)
                .inject("payService", apiBinder.bindType(PayService.class).toInfo())
                .inject("itemService", apiBinder.bindType(ItemService.class).toInfo())
                .initMethod("initMethod")
                .asEagerSingleton();
    }
});
```