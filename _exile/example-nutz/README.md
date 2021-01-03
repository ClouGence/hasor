#Hasor-Nutz 整合例子

## Nutz 整合 Hasor 之后 Nutz 哪些方面会有显著提升？

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以为 Nutz 提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为 Nutz 异构技术架构提供支持。

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

用法
==================================

本插件通过Ioc容器进行加载,符合nutz标准的插件结构,所以可以直接写入

```java
@IocBy(args={
	"*js", "ioc",
	"*anno", "net.wendal.nutzbook",
	"*hasor"})
```

与其他插件类似, 本插件也依赖名为conf的bean, 引用hasor开头的属性值

```
# 消费者
hasor.config=customer-config.xml
# 提供者
hasor.config=provider-config.xml

# 其余以hasor开头的属性,将作为hasor的环境变量,注入到hasor中
hasor.xxx.xxx=xxxx
```

使用Hasor or RSF 的入口

```java
@Configuration
public class RpcModule extends NutzModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        RsfApiBinder rsfApiBinder = apiBinder.tryCast(RsfApiBinder.class);
        //
        // 服务订阅
        rsfApiBinder.bindType(EchoService.class).toProvider(rsfApiBinder.converToProvider(  // 发布服务到 Hasor 容器中
                        rsfApiBinder.rsfService(EchoService.class).register()               // 注册消费者
                ));
        // 服务发布
        rsfApiBinder.rsfService(EchoService.class)                  // 声明服务接口
            .toProvider(nutzBean(rsfApiBinder, EchoService.class))  // 使用 nutz Bean 中的Bean 作为实现类
            .register();                                            // 发布服务
    }
}
```

@Configuration注解
======================================

* 用于让 Nutz 可以启动时发现 Hasor 的 Module。
* 您也可以通过 Hasor 的配置文件配置 Hasor Module。

NutzModule类
======================================

* Nutz 集成专门定制的 Hasor Module，通过该类提供的 nutzBean 方法，可以在 Hasor 的范围内拿到 Nutz 的 Bean。
* 拿到的 Nutz Bean 是延迟加载的。