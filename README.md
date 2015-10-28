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
&emsp;&emsp;`Core` 核心包.提供了IoC、Aop、事件、配置文件、AppContext、ApiBinder、自动注入、插件等核心功能。

&emsp;&emsp;`db` 是数据库操作框架。它是 `Spring JDBC` 的浓缩版，此外在它下面下还提供了一个与Spring同功能的事务管理器。

&emsp;&emsp;`web` 使用它可以通过编码形式动态注册 `Servlet/Filter`。Web 会为它们建立统一的 `Dispatcher` 入口。

&emsp;&emsp;`mvc` 是基于 `web` 模块的MVC框架，它吸收了淘宝WebX，Jfinal，SpringMVC等框架的精华，但不臃肿，而且它还支持 `RESTful`、`表单验证`、`参数映射` 等功能来简化开发者开发。


----------
### 特性
&emsp;&emsp;`IoC`，支持三种注入方式：1.`InjectMembers`接口方式注入。2.`@Inject`注解方式注入。3.Binder配置方式注入。
&emsp;&emsp;其中`InjectMembers`接口方式注入与后两者是互斥的，这就意味着一旦您选择了接口方式注入，那么整个注入的操作都由您亲自完成，框架不会有做任何IoC的动作。