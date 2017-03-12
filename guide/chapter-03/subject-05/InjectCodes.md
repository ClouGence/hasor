&emsp;&emsp;前面我们讲了很多依赖注入的功能都是基于注解实现，现在我们回归原始在这一小节，专门讲解如何通过代码形式完成依赖注入的配置。

&emsp;&emsp;在开始之前我们先把场景列出来。现在我们有一个 “OrderManager” 类，它在初始化时会根据一些业务逻辑来决定注入的细节。例如：具体使用哪一套库存系统。

&emsp;&emsp;这个时候前面讲到的各种依赖注入方式，可能都因为没有太多的灵活性而导致无法完成场景需要。这个时候你可以通过 Hasor 的 `InjectMembers` 接口完成更加灵活的对象依赖注入控制。

&emsp;&emsp;具体请看代码：
```java
public class OrderManager implements InjectMembers {
    @Inject  // <-因为实现了InjectMembers接口，因此@Inject注解将会失效。
    public StockManager stockBeanTest; 
    public StockManager stockBean;
    //
    public void doInject(AppContext appContext) throws Throwable {
        boolean useCaseA = ...
        if (useCaseA){
            this.iocBean = appContext.findBindingBean(
                "caseA",PojoBean.class);
        }else{
            this.iocBean = appContext.findBindingBean(
                "caseB",PojoBean.class);
        }
        //
    }
}
```

&emsp;&emsp;您要注意的是，为了避免 InjectMembers 接口注入和注解注入同时生效造成的混乱。一旦您打算使用 InjectMembers 方式进行注入 Hasor 就不会在解析注解进行注入。

---
&emsp;&emsp;接下来我们在介绍一种代码形式的依赖注入，还是以上面的例子为样本。我们可以在 Module 中进行预先配置。
```java
public class OrderManager {
    public StockManager stockBean;
    ...
}
```

&emsp;&emsp;实现注入的 Module 这样编写：
```java
public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
        boolean useCaseA = ...;
        BindInfo<StockManager> injectTo = null;
        if (useCaseA){
            injectTo = apiBinder.bindType(StockManager.class)
                    .to(StockManagerCaseA.class).toInfo();
        }else{
            injectTo = apiBinder.bindType(StockManager.class)
                    .to(StockManagerCaseB.class).toInfo();
        }
        apiBinder.bindType(OrderManager.class).inject("stockBean",injectTo);
    }
}
```
