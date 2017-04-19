&emsp;&emsp;我们通过一个 MVC 的例子作为 Hasor Web 框架的这一个小结通过 Web MVC 例子，来展示使用 Hasor 接收一个 Web 请求然后交给 jsp 去显示。

&emsp;&emsp;为了保证请求和响应的编码正确，我们要在启动入口中配置做一下声明。配置好以后 Hasor 框架会帮助我们设置 request/response 的编码为 UTF-8。
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //设置请求响应编码
        apiBinder.setEncodingCharacter("utf-8", "utf-8");
    }
}
```

&emsp;&emsp;接着创建请求处理器，一个请求处理器可以简单的只包含一个 `execute` 方法。
```java
public class HelloMessage {
    public void execute(Invoker invoker) {
        invoker.put("message", "this message form Project.");
    }
}
```

&emsp;&emsp;将请求处理器注册到框架中有两种办法，下面是通过手动注册的方式来集中管理。
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.mappingTo("/hello.jsp").with(HelloMessage.class);
        ...
    }
}
```

&emsp;&emsp;另一种是，通过 `@MappingTo` 注解让框架自动发现。第二种办法的好处是：方便，不需要将每个请求控制器都进行注册。使用更加简单。
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        //扫描所有 @MappingTo 注解
        apiBinder.scanMappingTo();
        ...
    }
}

@MappingTo("/hello.jsp")
public class HelloMessage {
    ...
}
```


&emsp;&emsp;最后创建 `hello.jsp` 视图文件，我们把 `message` 打印出来：
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello Word</title>
</head>
<body>
${message}
</body>
</html>
```

&emsp;&emsp;当上面的一切都做好之后，启动您的 web 工程，访问： `http://localhost:8080/hello.jsp` 即可得到结果。

----

&emsp;&emsp;在上面的例子中，我们的 `HelloMessage` 控制器并没有指定视图，Hasor 框架会自动根据请求路径来确定渲染视图。如果您的控制器根据不同的逻辑需要指定不同的视图，那么将它们分来开定义。例如：
```java
apiBinder.mappingTo("/forward.do").with(HelloMessage.class);
```

```java
public class HelloMessage {
    public void execute(RenderInvoker invoker) {
        invoker.put("message", "this message form Project.");
        if (test){
            invoker.renderTo("jsp","/hello.jsp");
        } else {
            invoker.renderTo("jsp","/error.jsp");
        }
    }
}
```

&emsp;&emsp;运行项目，请求 `http://localhost:8080/forward.do` 页面就会根据您的逻辑来渲染对应的视图。
