Hasor

    Hasor 是一款开源的轻量级Java应用程序基础框架体系，它的核心目标是提供一个以统一、高效的、友好的方式构造整个应用程序。它由多个不同功能的软件包组合而成。您可以根据需要去选择它们。

    Hasor 的本质与 Struts,Hibernate 等单层框架不同，它是由一个及其微小的核心和强有力的外围插件扩展组合而成。Hasor 将应用程序的启动分为 init、start 两个阶段。通过插件丰富 Hasor 功能，而作为插件是可以随时被剔除的。Hasor可以将诸多技术整合起来，建立起一个连贯的体系，可以说Hasor是一个搭建开发环境的框架。

Hasor-Core

    作为整个Hasor 体系的基石，Hasor-Core 通过 Module 提供了统一的扩展接口。对于Module的启动顺序还支持了基于依赖的排序检测。它内置了事件机制，应用程序可以根据需要引发“同步事件”和“异步事件”。

    Hasor-Core 提供了一套独特的类型绑定机制以方便应用程序声明Bean等信息，并且它还支持Scope、单例概念。

    Hasor 的配置文件使用的是Xml，但是您无需编写Xml解析器就可以方便的读取自定义配置信息。同时 Hasor 还会实时检测配置文件是否更改已通知应用程序做更新。尽管如此您依然可以不需要任何配置文件，真正的零配置。

Hasor-Web

    Hasor 为支持 Web 的扩展软件包，使用 Hasor-Web 可以以编码形式声明 Servlet/Filter/Listener 从而省去了配置 web.xml 的麻烦。Hasor-Web为它们建立了统一的 Dispatcher。下面是基于它的插件列表：

Hasor-JDBC
    该部件是参考 Spring JDBC 接口设计做的轻量化实现，它支持复杂嵌套事务、支持多数据源。

----------------------------------------------

源码说明：源码文件编码格式为 UTF-8。

项目首页：http://www.oschina.net/p/hasor

参考手册：http://www.hasor.net/Hasor-Guide-v0.0.1.pdf (旧版)

API文档：

Maven仓库：http://search.maven.org/#search%7Cga%7C1%7Chasor

更新记录：http://my.oschina.net/u/1166271/blog?catalog=380952

Git@OSC：http://git.oschina.net/zycgit/hasor

Github：https://github.com/zycgit/hasor

Blog：http://my.oschina.net/u/1166271/blog/161439

Email：zyc@byshell.org