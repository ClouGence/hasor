&emsp;&emsp;前面章节介绍了一下 Aop 原理和实现机制。在这个小节，我我们讲解一下如何在 Hasor 中使用 Aop。先来看一个最简单的Aop使用例子：
```java
@Aop(SimpleInterceptor.class)
public class AopBean {
    public String echo(String sayMessage) {
        return "echo :" + sayMessage;
    }
}
```

&emsp;&emsp;接下来 `SimpleInterceptor` 拦截器的代码如下：
```java
public class SimpleInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            System.out.println("before... ");
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

&emsp;&emsp;最后我们测试一下代码：
```java
AppContext appContext = Hasor.createAppContext();
AopBean fooBean = appContext.getInstance(AopBean.class);
System.out.println("aopBean : " + fooBean.echo("sayMessage"));
```