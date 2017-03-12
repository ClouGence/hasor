&emsp;&emsp;经过前面的介绍常规的 Hasor Aop 使用已经介绍清楚，下面对于 @Aop 注解来这里在介绍一个特殊的功能，复合拦截器。

&emsp;&emsp;复合拦截器，有两层含义
- 一、可以写多个拦截器，同时拦截同一个切点。
- 二、可以有多个拦截器同时生效在不同的位置。

&emsp;&emsp;我们先说第一点，多个拦截器同时拦截一个切点。它们的生效顺序是先 A 后 B。代码如下：
```java
@Aop({ SimpleInterceptorA.class, SimpleInterceptorB.class })
public class AopBean {
    ...
}
```

&emsp;&emsp;第二点，多个拦截器生效在不同位置，类似这样：
```java
@Aop(ClassInterceptor.class)
public class AopBean {
    public String print() {
        ...
    }
    @Aop(MethodInterceptor.class)
    public String echo(String sayMessage) {
        return "echo :" + sayMessage;
    }
}
```

&emsp;&emsp;在这个 case 中 print 方法调用时 `ClassInterceptor` 拦截器生效，当调用 `echo` 方法时 `ClassInterceptor` 和 `MethodInterceptor` 会同时生效，生效顺序为，先 Class 后 Method。

&emsp;&emsp;如果同时还有一个全局 Aop 也在配置中，那么拦截器的生效顺序是：
- **全局级 -> 类级 -> 方法级**