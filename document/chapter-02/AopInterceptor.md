&emsp;&emsp;在一个已经存在的方法上，如果我们想附加一些行为。这个时候最有力的方式就是通过 Aop。下面这段代码就是给我们 TradeService 类增加 aop 的例子。
```java
@Aop(CountInterceptor.class)
public class TradeService {
    public boolean subStore(long itemId , int count){
        ...
    }
}
```

&emsp;&emsp;接下来我们要编写自己的 Aop 切面代码。在 Hasor 中 Aop 的工作原理类似于拦截器，您可以配置一个或多个 Aop切面。如果是多个 Aop 切面，它们会被组装成拦截器的形式执行链式调用。
```java
public class CountInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            System.out.println("before... " + invocation.getMethod().getName());
            Object returnData = invocation.proceed();
            System.out.println("after...");
            return returnData;
        } catch (Exception e) {
            System.out.println("throw...");
            throw e;
        }
    }
}
```

&emsp;&emsp;上面的Aop例子是做用到整个类上的，如果我们只想对某一个方法做 Aop 而不是一整个类。那么可以把 @Aop 注解标记到方法上而非类上，这样 Aop切面 将只会在这个方法上起效。您不必编写复杂的匹配表达式：例如：
```java
public class TradeService {
    @Aop(CountInterceptor.class)
    public boolean subStore(long itemId , int count);
}
```

&emsp;&emsp;您也可以在类和方法上都标记上 Aop切面，Hasor 遇到这种情况会自动按照（类优先于方法）的规则执行您的 Aop 切面。