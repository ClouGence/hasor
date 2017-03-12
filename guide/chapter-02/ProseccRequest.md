&emsp;&emsp;Hasor 套件中携带了 web 子框架，通过前面的工程配置，您的项目已经工作在 Web 子框架之下。对于一个 web 应用程序第一件重要的事就是接收 Request 请求并处理。下面我们创建一个 Hasor 的请求处理器(Action)处理 `/my/my.htm` 请求，`execute` 方法是 Hasor 处理请求的执行入口。
```java
import net.hasor.web.WebController;
public class My extends WebController {
    public void execute(){
        ...
    }
}
```

&emsp;&emsp;接下来将我们的请求处理类配置到 Hasor 框架中。
```java
package net.demo.core;
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.mappingTo("/my/my.htm").with(My.class);
        ...
    }
}
```

&emsp;&emsp;上面这种配置方式的优点是可以统一管理所有 Action 的注册，缺点是每新增一个 Action 都要进行注册，这会比较麻烦。因此 Hasor 提供了另外一种简化的方式，通过标记 `@MappingTo` 注解来替代 `apiBinder.mappingTo` 方法调用。
```java
import net.hasor.web.WebController;
@MappingTo("/my/my.htm")
public class My extends WebController {
   ...
}
```

&emsp;&emsp;在使用了 `@MappingTo` 注解之后，还需要让 Hasor 框架启用这个功能，下面在 StartModule 启动类里通过下面代码启用 MappingTo 功能。
```java
apiBinder.scanMappingTo();
```