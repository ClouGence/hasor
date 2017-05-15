&emsp;&emsp;通过 @ReqParam 注解，获取请求参数。

```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker,
                        @ReqParam("name") String userName,
                        @ReqParam("pwd") String pwd) {
        ...
    }
}
```

&emsp;&emsp;请求URL地址：`http://localhost:8080/helloAcrion.do?name=userA&pwd=123456`

&emsp;&emsp;如果页面上使用了 checkbox 来表示一组值。那么可以使用下面这种方式获取这一阻值。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker,
                        @ReqParam("values") String[] vars) {
        ...
    }
}
```

&emsp;&emsp;当然除了 @ReqParam 注解之外您还可以使用 WebController 类提供的工具方法获取 请求参数。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker) {
        String var = this.getPara("name");
    }
}
```

----
&emsp;&emsp;除了获取一组参数，Hasor Web 框架提供的 @ReqParam 还可以帮助你进行简单的类型转换。例如：
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker,
                        @ReqParam("name") String name,
                        @ReqParam("age") int age) {
        ...
    }
}
```