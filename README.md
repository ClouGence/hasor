#Hasor

&emsp;&emsp;Hasor 是一款开源的轻量级 Java 应用程序开发框架，它的核心目标是提供一个简单、切必要的开发环境给开发者，开发者可以在此基础上构建出更加完善的应用程序。

----------
### 设计思想

&emsp;&emsp;“微内核+插件”是 Hasor 的主体设计思想。Hasor通过提供统一的接口进行扩展，通过扩展可以丰富所需要的功能，即使是核心模块也是通过扩展实现的。而作为扩展是可以随时被剔除的。

----------
### 交流平台

QQ群：**193943114**

issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)

Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)

----------
### 功能介绍
&emsp;&emsp;`Core` 是核心软件包，几乎所有 Hasor 扩展模块都会依赖到它。包含工具和 Hasor 两个部分，它是整个 Hasor 蓝图的基础。该软件包主要提供了：`ApiBinder`、`Settings`、`Event`、`AppContext`、`IoC/Aop` 功能。

&emsp;&emsp;`db` 是数据库操作框架，该框架主要目的是为 `Hasor` 提供 `JDBC` 数据库访问接口。它是 `Spring JDBC` 的浓缩版，可以说Hasor-DB拥有 `SpringJDBC` 绝大部分功能。此外在 `Hasor-DB` 下还提供了一个与Spring同功能的事务管理器。

&emsp;&emsp;`web` 是参照 `guice-servlet` 进行的仿制，在仿制过程中 `Hasor` 做了大量针对性的改进和优化。同时它也是作为 `Web` 上开发框架的一个基石。开发者使用它可以通过编码形式动态注册 `Servlet/Filter`。Hasor-Web 会为它们建立统一的 `Dispatcher` 入口。

&emsp;&emsp;`mvc` 是基于 `web` 模块的MVC框架，它吸收了淘宝WebX，Jfinal，SpringMVC等框架的精华，但不臃肿，而且它还支持 `RESTful`、`表单验证`、`参数映射` 等功能来简化开发者开发。
