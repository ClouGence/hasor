&emsp;&emsp;首先用您习惯的 IDE 工具创建一个 Web 工程，然后在 web.xml 中加入 Hasor 的启动监听器和全局 Filter。
```xml
<listener>
    <listener-class>net.hasor.web.startup.RuntimeListener</listener-class>
</listener>
<filter>
    <filter-name>rootFilter</filter-name>
    <filter-class>net.hasor.web.startup.RuntimeFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>rootFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

&emsp;&emsp;接着需要您在您的项目中创建一个 Hasor 启动入口类。
```java
package net.demo.core;
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
    }
}
```

&emsp;&emsp;最后您需要将启动入口配置到 Hasor 启动参数中，在 Hasor 中配置启动入口有很多方式。下面我们采用在 web.xml 中配置的方式，在 web.xml 中加入如下配置：
```xml
<context-param>
    <param-name>startModule</param-name>
    <param-value>net.demo.core.StartModule</param-value>
</context-param>
```