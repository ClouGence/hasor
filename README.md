#Hasor

&emsp;&emsp;Hasor 是一款开源的轻量级 Java 应用程序开发框架，它的核心目标是提供一个简单、切必要的开发环境给开发者，
开发者可以在此基础上构建出更加完善的应用程序。

----------
### 设计思想

&emsp;&emsp;“微内核+插件”是 Hasor 的主体设计思想。Hasor通过提供统一的接口进行扩展，通过扩展可以丰富所需要的功能，
即使是核心模块也是通过扩展实现的。而作为扩展是可以随时被剔除的。

----------
### 架构
&emsp;&emsp;Hasor自底而上共分为三层，其中第一层是`Core`包。它提供了基本的Bean管理，和IoC/Aop支持。同时也提供了统一的module插件接口支持。

&emsp;&emsp;第二层是，依赖`Core`但又彼此独立的子框架。其中包括了`db`数据库框架。`web`框架。

&emsp;&emsp;位于最上面一层的是各类内置小插件，这些小插件功能虽小但是会给开发带来很多便利。

----------
### 特性
core
01. 最小依赖，只依slf4j。
02. 支持三种注入方式：1.`InjectMembers`接口方式注入、2.`@Inject`注解方式注入、3.Binder配置方式注入。
06. 提供一个事件框架，并支持同步事件和异步事件机制。
07. 支持xml形式的配置文件，并可以简单操作xml文件。
08. 支持环境变量解析。
09. 支持Bean的作用域。
10. 支持全注解化配置。

db
01. 提供了简化版的Spring，JdbcTemplate数据库操作接口。和完整的7种事务传播属性配置。
02. 提供TransactionCallback接口让用户可以针对某一段代码进行特殊的事务控制。

web
01. 支持通过编码形式动态注册 `Servlet/Filter`，Web 会为它们建立统一的 `Dispatcher` 入口。

----------
### 交流平台

QQ群：**193943114**

issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)

Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)


----------
![Hasor捐赠](http://static.oschina.net/uploads/space/2015/1130/154023_xiMj_1166271.png)