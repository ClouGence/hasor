#Hasor

&emsp;&emsp;Hasor 是一款基于 Java 语言的应用程序开发框架，它的核心设计目标是提供一个简单、切必要的环境给开发者。开发者可以在此基础上，通过Hasor强有力的粘合机制，构建出更加完善的应用程序。同时Hasor的各种内置插件也会帮助开发者实现快速开发。

----------
### 设计思想

&emsp;&emsp; Hasor 提倡开发者充分利用编程语言的优势进行程序模块整合。通过“微内核+插件”的方式丰富所需要的所有功能。

&emsp;&emsp; 决定避开COC原则的原因是，COC虽然已约定的方式降低了整个框架的设计复杂度，同时也最大限度的牺牲了灵活性。缺少灵活性，是制约框架应用场景多少的最大杀手。

&emsp;&emsp; 另外一方面，开发者更加关心的是底层框架的稳定性，当选定一款稳定框架，但是不可否认的是COC成为了
减少了框架出错概率的发生。但作为开发者
之所以 Hasor 选择避开 COC 原则

&emsp;&emsp;做小而美的Hasor，大而全的生态圈。



----------
### 架构

![架构](http://static.oschina.net/uploads/space/2015/1127/185946_9TWV_1166271.png)

----------
### 特性
1. 最小依赖，slf4j-api
2. 三种注入方式：`InjectMembers`接口方式、`@Inject`注解方式、Binder配置
3. 框架级事件机制（同步事件、异步事件）
4. Xml形式配置文件，且 `零` 开发支持自定义Xml配置结构
5. 环境变量
6. 支持Bean的作用域
7. 支持全注解化配置

db

1. 提供了简化版的Spring，JdbcTemplate数据库操作接口。和完整的事务传播属性
2. 提供TransactionCallback接口让用户可以针对某一段代码进行特殊的事务控制

web

1. 支持通过编码形式动态注册 `Servlet/Filter`，Web 会为它们建立统一的 `Dispatcher` 入口

----------
### 交流平台

QQ群：**193943114**

issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)

Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)


----------
![Hasor捐赠](http://static.oschina.net/uploads/space/2015/1130/154023_xiMj_1166271.png)