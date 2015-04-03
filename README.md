Hasor

Hasor 是一款轻量级Java应用程序开发框架。Hasor 的本质与 Struts,Hibernate 等单层框架不同，它的功能更小、更方便、功能更加集中。通过它的一个及其微小的核心扩展更多的插件。

它包括了core、Web、DB、MVC、Test、Quick。几个部分。

Core
	Hasor的核心软件包，几乎所有Hasor扩展模块都会依赖到它。包含工具和 Hasor 两个部分。提供了：配置文件解析、事件、容器、IoC/Aop等核心功能。

Web
	它是Hasor作为Web上开发框架的一个基石，开发者使用它可以通过编码形式动态注册 Servlet/Filter。Web 为它们建立了统一的 Dispatcher 入口。

DB
	一个JDBC框架，该框架主要目的是为Hasor提供关系型数据库访问功能。该项目中包含了完善的事务控制接口和一个小而强大的数据库操作接口，除此之外还携带了一个ORM工具。

MVC
	它是注解驱动的 MVC 框架，它的运行对 Web 没有强制要求。您可以在一般程序中使用它充当门面入口。在 Web方面提供了Restful、Strategy、Result等支持，通过这些扩展可以使Web开发更加简单轻松。

Test
	一个简单的测试工具。

Quick
	各种扩展插件集


##
TODO ：

log输出增加行号。