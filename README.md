#Hasor

&emsp;&emsp;Hasor 是一款基于 Java 语言的应用程序开发框架，它的核心设计目标是提供一个简单、切必要的环境给开发者。开发者可以在此基础上，通过Hasor强有力的粘合机制，构建出更加完善的应用场景。同时Hasor的各种内置插件也会帮助开发者进行快速开发。

&emsp;&emsp;基于目前 Hasor 内核上已经发展出 DB、Web、Restful 三大基础框架。

----------
### 设计思想

&emsp;&emsp; Hasor 提倡开发者充分利用编程语言的优势进行三方整合和模块化设计。同时Hasor也主要是通过这种“微内核+插件”的方式丰富开发所需的所有功能。

&emsp;&emsp; 决定避开COC原则的原因是，COC虽然已约定的方式降低了整个框架的设计复杂度，但同时也最大限度的牺牲了框架的灵活性。缺少灵活性的框架在应用场景上会受到极大的制约。而Hasor的设计则更加面向底层，因此需要更多的灵活性。

&emsp;&emsp; Hasor强大的灵活性表现在模块整合能力上，对于某一个领域开发方面Hasor依然强调并力推COC。小而美的核心，大而全的生态圈是Hasor的目标。

----------
### 架构

![架构](http://project.hasor.net/resources/185946_9TWV_1166271.png)

----------
### 特性
01. 支持IoC编程，灵活，多种注入方式可选
02. 支持Aop编程，更精准、更强大
03. ‘零’配置文件，所有框架配置全部内置
04. ‘零’开发，解析自定义Xml配置文件
05. 提供事件机制进行业务深度解耦，使业务逻辑更佳清晰
06. 提供完备的数据库操作接口，支持 Result -> Object 映射
07. 提供丰富的JDBC事物控制机制
08. 支持多数据源下的事务控制，无需考虑多数据源下事务切换的问题
09. 支持log4j、logback等多种主流日志框架。
10. 支持无缝与Spring集成，在Spring中也可以享受Hasor的插件
11. 提供丰富的工具箱，帮助您快速开发
12. web - 无需‘web.xml’配置各种Listener、Servlet、Filter
13. web - 支持对ServletContextListener、HttpSessionListener、HttpServlet、Filter 的注入和Aop
14. web - 支持restful风格的Web开发，支持各种类型的模版引擎。

----------
### 内置插件
01. AopModule插件，提供精确到方法级的注解化Aop配置
02. EncodingModule插件，提供对请求响应编码统一设置的插件，默认编码为：UTF-8
03. RestfulModule插件，提供restful风格的Api进行Web开发
04. StartupModule插件，简化“modules.module”的配置，提供整个应用程序的一个唯一入口 Module
05. ValidModule插件，一个简单的对象验证框架
06. EventModule插件，提供注解化事件监听器，无需编写代码进行注册事件监听器

----------
### 工具箱
01. ConverterUtils，提供任意类型到任意类型的转换
02. JSON，来自Jetty的JSON序列化和反序列化工具
03. “org.more.util”下拥有各种Util工具，它们都来自于Apache
04. Paginator和PageResult对分页数据的封装
05. “org.more.classcode”提供字节码的增强，提供了Aop、动态接口实现、动态属性新增。

----------
### 发展状况

&emsp;&emsp; Hasor起源于2012年，并在2013年初具雏形。当时尚未开源，并被应用到公司个大项目中。那个时候Hasor还是基于Guice构建，并且整合了Spring JDBC、Guice Servlet等大量三方框架。还有少量自建功能，例如：WebMVC。当时整个框架还未从软件产品中完全剥离出来，其中最基本的要素经过打包会产生约 130MB的 JAR包依赖。与目前相比显得无比臃肿。

&emsp;&emsp; 随后2013年开始，通过对整套框架的重新梳理。Hasor才得以从项目中脱离出来成为独立的框架，依赖也随时聚减。同年开始计划开源版本的实现，也就是如今的Hasor。

&emsp;&emsp; 2013年9月15日，0.0.1版本，第一个版本被推送到Maven中央仓库，Hasor的雏形被确立，Module化的插件概念被提出，依赖减少到只有Guice和slf4j。

&emsp;&emsp; 2014年7月26日，0.0.9版本被推送到Maven中央仓库。经历多个版本迭代更新从0.0.9开始，最后一个核心依赖Guice也被去除。而此时Hasor已经拥有了Web、WebMVC、JDBC、和完整的数据库事务能力。在这个阶段中Hasor曾经在Guice、Spring之间摇摆不定，也对未来的路线和定位发生过几次重大的改变。

&emsp;&emsp; 2015年5月9日，0.0.12版本发布，此时的Hasor拥有6大模块和若干小插件，发展上过于零碎。加上精力有限于是开始了all-in行动，将众多模块合并到一起。直至2015年7月3日，Hasor-1.0.0发布。

&emsp;&emsp; 2015年11月27日，2.0版本发布，提供了@Inject注解方式进行依赖注入，该版本一举解决了在去除Guice和决定不在依赖Spring之后Hasor的Ioc能力大大下降的问题。同时Hasor2.0开始确立了“小而美的核心，大而全的生态圈”的目标。

&emsp;&emsp; 2016年5月29日，2.3.2版本，所有内置插件提供了智能载入机制。智能是指，内置插件在初始化的时都做了配置检查，只有用到了它们才会被加载到框架中。在这之后Hasor不光体积小功能强，在启动运行时也保证了自己的最小化，避免了不必要的加载。至此2.3.2版本之后，您可以大胆的放弃内置插件采用自己的方式去替代相关功能，也不需要担心内置插件会出来捣乱。在Hasor中一切都变得可以被化简。

----------
### 相关连接

* WebSite：[http://www.hasor.net](http://www.hasor.net)
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)
* QQ群：193943114
* [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-core)
[![Build Status](https://travis-ci.org/zycgit/hasor.svg?branch=master)](https://travis-ci.org/zycgit/hasor)
[![Build Status](https://travis-ci.org/zycgit/hasor.svg?branch=dev)](https://travis-ci.org/zycgit/hasor)

### 致项目组成员

* mvn release:prepare -P release
* mvn deploy -P release