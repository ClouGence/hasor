#Hasor-Spring 整合例子

## Spring 整合 Hasor 之后 Spring 哪些方面会有显著提升？

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以为 Spring 提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为 Spring 异构技术架构提供支持。
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