#Hasor-JFinal 整合例子

## JFinal 整合 Hasor 之后 JFinal 哪些方面会有显著提升？

IoC/Aop
1. 支持 Controller 通过 @Inject 注解进行依赖注入Bean。
2. 被注入的 Bean 支持 IoC/Aop。
3. 通过 Hasor 的 RSF 框架 JFinal 会具备部署分布式服务的能力。

数据库操作方面
1. Hasor 提供三种途径控制事务，支持七种事务传播属性，标准的事务隔离级别。集成之后 JFinal 也会具备这些功能。
2. 通过 @Transactional 注解 JFinal 可以快速实现事务控制，而且支持多层嵌套，嵌套层数无上限。

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以为 JFinal 提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为 JFinal 异构技术架构提供支持。
```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-rsf</artifactId>
    <version>1.3.0</version>
</dependency>
```

分布式服务
1. RSF 对于服务提供了丰富的控制力，例如：多机房、异地调用、流控、服务路由。
2. 通过 RSF 注册中心可以集中管理您所有RPC服务的：订阅、发布。
```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-registry</artifactId>
    <version>1.3.0</version>
</dependency>
```

集成小建议，视您情况自行选择
1. HasorPlugin          ，必选，JFinal 中使用 Hasor 框架。
2. HasorInterceptor     ，可选，JFinal 拦截器为 JFinal 提供 IoC/Aop能力。
3. HasorDataSourceProxy ，可选，JFinal 数据源代理，用于接管 JFinal 事务控制。
4. HasorHandler         ，可选，JFinal 中使用 Hasor 的 Web 开发能力。