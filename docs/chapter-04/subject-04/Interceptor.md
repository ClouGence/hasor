&emsp;&emsp;拦截器是一个老生常谈的功能，在 Hasor 中它的地位和作用等同于 javax.servlet.Filter。

&emsp;&emsp;下面定义一个拦截器，拦截所有 Action 请求。它的定义和 javax.servlet.Filter 基本类似。
```java
public class MyInvokerFilter implements InvokerFilter {
    public void init(InvokerConfig config) throws Throwable {
        ...
    }
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        try {
            // before
            chain.doNext(invoker);
            // after
        } catch (Throwable e) {
            // error
            throw e;
        }
    }
    public void destroy() {
        ...
    }
}
```

&emsp;&emsp;声明好拦截器之后，我们将它注册到 Hasor 框架中即可。
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.filter("/*").through(MyInvokerFilter.class);
        ...
    }
}
```

&emsp;&emsp;当然您也可以使用传统的 `javax.servlet.Filter`，例如：
```java
public class MyFilter implements Filter {
    ...
}
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.jeeFilter("/*").through(MyFilter.class);
        ...
    }
}
```
