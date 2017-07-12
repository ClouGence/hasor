#Hasor 例子

项目说明
1. 项目启动入口：net.demo.hasor.core.StartModule
2. Freemarker 渲染引擎：net.demo.hasor.core.FreemarkerRender
3. RPC服务发布：net.demo.hasor.core.RpcModule

配置文件
1. hasor-config.xml

各包含义
1. core，项目启动、项目内功能模块切分
2. daos，数据库操作 DAO
3. domain，数据库的 domain模型
4. provider，RPC服务实现接口
5. services，业务逻辑服务
6. web，接收 web 请求的控制器

表单验证
1. 相关类：Login(Action)、LoginForm(表单)、LoginFormValidation(验证器)

数据库操作方面
1. Hasor 提供三种途径控制事务，支持七种事务传播属性，标准的事务隔离级别。集成之后 JFinal 也会具备这些功能。
2. 通过 @Transactional 注解快速实现事务控制，而且支持多层嵌套，嵌套层数无上限。

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为异构技术架构提供支持。
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