#Hasor

&emsp;&emsp;Hasor 是一款基于 Java 语言的应用程序开发框架，它的核心设计目标是提供一个简单、切必要的环境给开发者。开发者可以在此基础上，通过Hasor强有力的粘合机制，构建出更加完善的应用场景。同时Hasor的各种内置插件也会帮助开发者进行快速开发。

----------
### 设计思想

&emsp;&emsp; Hasor 提倡开发者充分利用编程语言的优势进行三方整合和模块化设计。同时Hasor也主要是通过这种“微内核+插件”的方式丰富开发所需的所有功能。

&emsp;&emsp; 决定避开COC原则的原因是，COC虽然已约定的方式降低了整个框架的设计复杂度，但同时也最大限度的牺牲了框架的灵活性。缺少灵活性的框架在应用场景上会受到极大的制约。而Hasor的设计则更加面向底层，因此需要更多的灵活性。

&emsp;&emsp; Hasor强大的灵活性表现在模块整合能力上，对于某一个领域开发方面Hasor依然强调并力推COC。小而美的核心，大而全的生态圈是Hasor的目标。

----------
### 架构

![架构](http://static.oschina.net/uploads/space/2015/1127/185946_9TWV_1166271.png)

----------
### 特性
01. 支持IoC编程，灵活，多种注入方式可选
02. 支持Aop编程，更精准、更强大
03. ‘零’配置文件，所有框架配置全部内置
04. ‘零’开发，解析自定义Xml配置文件
05. 提供事件机制，通过事件方便业务流程的解耦合，使业务逻辑更佳清晰
06. 提供完备的数据库操作接口，支持 Result -> Object 映射
07. 全面支持各种数据库事务传播属性，提供更佳精准的事务控制，包括针对代码片段的事务控制
08. 支持多数据源下的事务控制，无需考虑多数据源下事务切换的问题
09. 框架日志输出以slf4j-api为基准，可同时支持log4j、logback等多种主流日志框架

----------
### 内置插件
01. AopModule插件，提供精确到方法级的注解化Aop配置
02. EncodingModule插件，提供对请求响应编码统一设置的插件，默认编码为：UTF-8
03. MimeTypeModule插件，提供MimeType接口，支持通过配置文件扩展环境不存在的mime type定义
04. ResourceModule插件，提供丰富的资源加载模块，可以实现将zip包中的资源响应给web请求
05. RestfulModule插件，提供restful风格的Api进行Web开发
06. StartupModule插件，简化“modules.module”的配置，提供整个应用程序的一个唯一入口 Module
07. ValidModule插件，一个简单的对象验证框架
08. TemplateModule插件，提供一个通用的模版渲染引擎接口

----------
### 发展状况

&emsp;&emsp; Hasor起源于2012年，并在2013年初具雏形。当时尚未开源，并被应用到公司个大项目中。那个时候Hasor还是基于Guice构建，并且整合了Spring JDBC、Guice Servlet等大量三方框架。还有少量自建功能，例如：WebMVC。当时整个框架还未从软件产品中完全剥离出来，其中最基本的要素经过打包会产生约 130MB的 JAR包依赖。与目前相比显得无比臃肿。

&emsp;&emsp; 随后2013年开始，通过对整套框架的重新梳理。Hasor才得以从项目中脱离出来成为独立的框架，依赖也随时聚减。同年开始计划开源版本的实现，也就是如今的Hasor。

&emsp;&emsp; 2013年9月15日，0.0.1版本，第一个版本被推送到Maven中央仓库，Hasor的雏形被确立，Module化的插件概念被提出，依赖减少到只有Guice和slf4j。

&emsp;&emsp; 2014年7月26日，0.0.9版本被推送到Maven中央仓库。经历多个版本迭代更新从0.0.9开始，最后一个核心依赖Guice也被去除。而此时Hasor已经拥有了Web、WebMVC、JDBC、和完整的数据库事务能力。在这个阶段中Hasor曾经在Guice、Spring之间摇摆不定，也对未来的路线和定位发生过几次重大的改变。

&emsp;&emsp; 2015年5月9日，0.0.12版本发布，此时的Hasor拥有6大模块和若干小插件，发展上过于零碎。加上精力有限于是开始了all-in行动，将众多模块合并到一起。直至2015年7月3日，Hasor-1.0.0发布。

&emsp;&emsp; 2015年11月27日，2.0版本发布，提供了@Inject注解方式进行依赖注入，该版本一举解决了在去除Guice和决定不在依赖Spring之后Hasor的Ioc能力大大下降的问题。同时Hasor2.0开始确立了“小而美的核心，大而全的生态圈”的目标。

----------
### 交流平台

QQ群：**193943114**

issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)

Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)


----------
![Hasor捐赠](http://static.oschina.net/uploads/space/2015/1130/154023_xiMj_1166271.png)