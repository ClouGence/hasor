&emsp;&emsp;本节讲解 @CookieParam 注解，通过该注解获取 Cookie 数据。该注解的用法和 @ReqParam 一样。

```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker,
                        @CookieParam("name") String userName ,@CookieParam("pwd") String pwd) {
        ...
    }
}
```

&emsp;&emsp;如果cookie中存储了一组相同的数据，那么可以使用下面这种方式获取这一阻值。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @CookieParam("values") String[] vars) {
        ...
    }
}
```

&emsp;&emsp;当然除了 @CookieParam 注解之外您还可以使用 WebController 类提供的工具方法获取 Cookie 值。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker) {
        String var = this.getCookie("values");
    }
}
```

----
&emsp;&emsp;除了获取一组参数，Hasor Web 框架提供的 @CookieParam 还可以帮助你进行简单的类型转换。例如：
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @CookieParam("name") String name ,@CookieParam("age") int age) {
        ...
    }
}
```