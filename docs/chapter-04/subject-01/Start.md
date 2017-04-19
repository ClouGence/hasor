&emsp;&emsp;在 Web 开发开始前，请先检查您的 工程是否是一个 Web 工程。一个正确的 Web 工程应该包含一个存有 web.xml 文件的 webapp 目录。

&emsp;&emsp;一些使用了 Servlet3.0 高级特性的项目可能通过解化，省去了 web.xml 配置文件，包括 SpringBoot 都是属于这个范畴。没有 web.xml 没关系，您只要保证下面这段 在您的项目中生效即可。

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

&emsp;&emsp;接着您需要创建 “hasor-config.xml” 并放入您的 classpath 中，内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor>
        <!-- 项目所属包：减少类扫描范围 -->
        <loadPackages>com.xxx.you.project.*</loadPackages>
        <!-- 框架启动入口 -->
        <startup>com.xxx.you.project.StartModule</startup>
        <!-- 环境变量 -->
        <environmentVar>

            <!-- 启用站点文件布局 -->
            <!--
                <HASOR_RESTFUL_LAYOUT>true</HASOR_RESTFUL_LAYOUT>
            -->
        </environmentVar>
    </hasor>
    ...
</config>
```

&emsp;&emsp;然后在创建一个 “env.config” 的属性文件，也放到 classpath 下。文件编码为 UTF-8，内容为空。这个配置文件留给多环境导入配置使用。如果不需要删掉也可以。

&emsp;&emsp;最后创建包 “com.xxx.you.project” 并在包中新增一个类 “StartModule” 该类，内容如下：
```java
package com.xxx.you.project;
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        System.out.println("You Project Start.");
    }
}
```

&emsp;&emsp;启动您的的 Web 工程，如果控制台上看到 “You Project Start.” 则证明框架成功配置。