&emsp;&emsp;在 Hasor 中，一共有三种不同的方式实现请求拦截。

1. 通过 Aop 实现请求拦截器
2. 通过 InvokerFilter 接口拦截请求
3. 通过 javax.servlet.Filter 接口拦截请求

&emsp;&emsp; 其中 InvokerFilter 接口和 Filter 接口的工作方式和原理是等价的，只是用了不同的接口结构。

&emsp;&emsp; 第一种 Aop 方式，这种方式是利用了 Hasor 原本的 Aop 功能充当拦截器。好处是用起来最简单，例如下面这段例子：
```java
@Aop(CountInterceptor.class)
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @Params() ParamsFormBean formBean){
        ...
    }
}
```

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

&emsp;&emsp; 第二种，用前一章节介绍的 InvokerFilter 接口。这种方式的最大优点是您可以在拦截器中直接拿到 Invoker 接口对象，例如：
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

&emsp;&emsp; 第三种，用传统的 J2EE的 Filter 充当拦截器。例如：
```java
public class MyFilter implements Filter {
    ...
}
```

&emsp;&emsp; 第二种和第三种，方式都需要您对拦截器进行声明注册：
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.filter("/*").through(MyInvokerFilter.class);
        apiBinder.jeeFilter("/*").through(MyFilter.class);
        ...
    }
}
```