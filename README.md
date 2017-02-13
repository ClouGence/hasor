#Hasor

&emsp;&emsp;Hasor 是一款基于 Java 语言的应用程序开发框架，它的核心设计目标是提供一个简单、切必要的环境给开发者。开发者可以在此基础上，通过Hasor强有力的粘合机制，构建出更加完善的应用场景。同时Hasor的各种内置插件也会帮助开发者进行快速开发。

&emsp;&emsp;基于目前 Hasor 内核上已经发展出 DB、Web、RSF 三大独立的基础框架。

----------
### 设计思想

&emsp;&emsp; Hasor 提倡开发者充分利用编程语言的优势进行三方整合和模块化设计。同时Hasor也主要是通过这种“微内核+插件”的方式丰富开发所需的所有功能。

&emsp;&emsp; 决定避开COC原则的原因是，COC虽然已约定的方式降低了整个框架的设计复杂度，但同时也最大限度的牺牲了框架的灵活性。缺少灵活性的框架在应用场景上会受到极大的制约。而Hasor的设计是让开发者使用更加便捷，因此需要更多的灵活性。

&emsp;&emsp; Hasor强大的灵活性表现在模块整合能力上，对于某一个领域开发方面Hasor依然强调并力推COC。小而美的核心，大而全的生态圈是Hasor的目标。

----------
### 架构

![架构](http://project.hasor.net/resources/185946_9TWV_1166271.png)

----------
### 特性
01. IoC/Aop编程模型，设计精巧，使用简单
02. COC原则的最佳实践，‘零’配置文件
03. 微内核 + 扩展，基于内核已发展出 DB、Web、RSF 三大独立的基础框架
04. 独特的Xml读取技术，让你真正的零开发，解析项目特有的自定义 Xml
05. 支持模板化配置文件，程序打包之后一套配置通吃(日常、预发、线上)以及其它各种环境
06. 完备的 JDBC 操作接口，支持 Result -> Object 映射
07. 提供三种途径控制事务，支持七种事务传播属性，标准的事务隔离级别
08. 支持多数据源
09. 事件机制，方便进行业务深度解耦，使业务逻辑更佳清晰
10. Web 类型项目开发，提供 restful 风格的 mvc 开发方式
11. 提供Form表单验证、支持场景化验证
12. 提供开放的模版渲染接口，支持各种类型的模版引擎
13. 支持log4j、logback等多种主流日志框架
14. 体积小，无第三方依赖

----------
### 集成
01. 支持与 Spring 紧密集成
02. 支持与 JFinal 紧密集成

----------
### 发展状况

&emsp;&emsp; Hasor起源于2012年，并在2013年初具雏形。当时尚未开源，并被应用到公司个大项目中。那个时候Hasor还是基于Guice构建，并且整合了Spring JDBC、Guice Servlet等大量三方框架。还有少量自建功能。当时整个框架还未从软件产品中完全剥离出来，其中最基本的要素经过打包会产生约 130MB的 JAR包依赖。

&emsp;&emsp; 随后2013年开始，通过对整套框架的重新梳理。Hasor才得以从项目中脱离出来成为独立的框架，依赖也随时聚减。同年开始计划开源版本的实现，也就是如今的Hasor。

&emsp;&emsp; 2013年9月15日，0.0.1版本，第一个版本发布。Hasor的雏形被确立，Module化的插件概念被提出，同时依赖减少到只有Guice和slf4j。

&emsp;&emsp; 2014年7月26日，0.0.9版本开始被推送到Maven中央仓库。经历多个版本迭代最后一个核心依赖Guice也被去除。而此时Hasor已经拥有了Web、JDBC、和完整的数据库事务能力。在这个阶段中Hasor曾经在Guice、Spring之间摇摆不定，也对未来的路线和定位发生过几次重大的改变。

&emsp;&emsp; 2015年7月3日，1.0.0版本发布。1.0.0首先明确了Hasor的发展路线，同时它具备了Web、JDBC两项核心功能，依赖包也减少到只有slf4j-api。同年11月27日，经过大量优化2.0版本发布。最终确立了“小而美的核心，大而全的生态圈”的目标。

&emsp;&emsp; 2016年8月18日，2.4.3版本，经过持续的沉淀、整合、优化。Hasor越来越稳定可靠，基于2.4版本Hasor开始孕育全新的子项目 Hasor-RSF (分布式RPC)。

&emsp;&emsp; 2017年1月10日，3.0.0版本，是扩展性上的一次革新。同时一直并行发展的web和restful两个老牌框架进行了合并。得力于3.0的扩展性从这个版本开始Hasor 不在需要单独为某一个领域单独设计特殊的 AppContext。

----------
### 最低要求
* jdk6
* servlet 2.3

----------
### 相关连接

* WebSite：[http://www.hasor.net](http://www.hasor.net)
* Docs : [http://hasor-guide.mydoc.io/](http://hasor-guide.mydoc.io/)
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)
* Demo工程：[http://git.oschina.net/zycgit/hasor-example](http://git.oschina.net/zycgit/hasor-example)
* QQ群：193943114
* [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-core)
[![Build Status](https://travis-ci.org/zycgit/hasor.svg?branch=master)](https://travis-ci.org/zycgit/hasor)
[![Build Status](https://travis-ci.org/zycgit/hasor.svg?branch=dev)](https://travis-ci.org/zycgit/hasor)

### 正式发布

* mvn release:prepare -P release
* mvn deploy -P release

### 偶尔启动需要增加 -noverify 参数