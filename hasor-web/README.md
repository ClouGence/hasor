# Web 框架

&emsp;&emsp;Web 轻量化 Web MVC 框架。

----------
## 特性
01. 提供 RESTful 风格的 mvc 开发方式。
02. 提供请求验证接口、验证支持场景化。
03. 开放的模版渲染接口，支持各种类型的模版引擎。
04. 内置文件上传组件，无需引入任何jar包。

# 样例

web.xml 配置
```xml
<!-- 框架启动 -->
<listener>
    <listener-class>net.hasor.web.startup.RuntimeListener</listener-class>
</listener>
<!-- 全局拦截器 -->
<filter>
    <filter-name>rootFilter</filter-name>
    <filter-class>net.hasor.web.startup.RuntimeFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>rootFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
<!-- (建议)启动模块 -->
<context-param>
    <param-name>hasor-root-module</param-name>
    <param-value>com.xxx.you.project.StartModule</param-value>
</context-param>
<!-- (可选)如果有配置文件在这里指定 -->
<context-param>
    <param-name>hasor-hconfig-name</param-name>
    <param-value>hasor-config.xml</param-value>
</context-param>
```

框架启动入口
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        System.out.println("You Project Start.");
    }
}
```

请求接收
```java
@MappingTo("/hello.jsp")
public class HelloMessage {
    public void execute(Invoker invoker) {
        invoker.put("message", "this message form Project.");
    }
}
```

视图渲染
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head><title>Hello Word</title></head>
    <body>${message}</body>
</html>
```
