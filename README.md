#Hasor

&emsp;&emsp; Hasor是一套基于 Java 语言的应用程序开发框架，它的核心设计目标是提供一个简单、且必要的环境给开发者。开发者可以在此基础上快速进行软件开发。

&emsp;&emsp; 区别于其它框架的是 Hasor 有着自己一套完整的体系，无论您是一般的Web项目，还是几百人团队中复杂的分布式系统。Hasor都会给予你最有力的支持。

----------
### 特点

- “微内核+插件” 简单、小巧、功能强大、使用简单
- COC原则的最佳实践，‘零’配置文件
- 合理的整体架构规划，即是小框架也大平台
- 各部分全部独立，按需使用，绝不臃肿
- 体积小，无依赖

----------
### 架构
![架构](//files.hasor.net/uploader/20170221/125130/CC2_C7F9_F92E_2A8C.png "架构")

- Core 职责是提供Bean容器、Module机制
- DB 提供了JDBC操作、事务管理
- RSF 是一个完备的分布式 RPC 服务框架
- Web 是一个吸收了百家所长的 Web MVC框架
- Land 它类似于 Zookeeper 负责提供分布式一致性的支持
- Center 它是 RSF 的服务治理中心

----------
### 总体功能
01. 支持IoC、Aop、模块化
02. 支持模版化配置文件，让您程序打包之后通吃各种环境
03. 提供JDBC操作接口，支持 Result -> Object 映射
04. 完备的数据库事务控制能力，支持 7 种事务传播属性
05. 支持传统 Web MVC 开发，也支持  restful 方式
06. 提供Form表单验证、验证支持场景化
07. 开放的模版渲染接口，支持各种类型的模版引擎
08. 分布式 RPC 服务，支持容灾、负载均衡、集群
09. 通过 Hprose 支持多协议、跨语言的 RPC 调用
10. 通过服务注册中心，支持分布式服务统一治理
11. 提供对某一个状态提供分布式下状态一致性支持

----------
### 集成
01. 支持与 Spring 集成（2016-02-16）
02. 支持与 JFinal 集成（2016-11-03）
03. 支持与 Nutz 集成（2017-02-21）

----------
### 发展状况

&emsp;&emsp; Hasor起源于2012年。当时尚未开源，并被应用到公司个大项目中。当时基于Guice构建，并且整合了Spring JDBC、Guice等大量三方框架。

&emsp;&emsp; 2013年9月15日，第一个Hasor版本发布。Module化的插件概念被提出，同时依赖大量减少，成为一个开源的java开发框架。

&emsp;&emsp; 2015年7月3日，1.0.0版本发布。这一年Hasor明确的发展路线，确立了“小而美的核心，大而全的生态圈”目标。

&emsp;&emsp; 2016年8月18日，2.4.3版本发布，基于2.4版本Hasor开始孕育全新的子项目 RSF。当时 Hasor 的版图还没有这么大。 

&emsp;&emsp; 2017年2月21日，Hasor 大版图的想法首次公开亮相，RSF被并入大版图，同时 Land 项目进入开发阶段。

----------
### 最低要求
* jdk6
* servlet 2.3

----------
### 相关连接

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