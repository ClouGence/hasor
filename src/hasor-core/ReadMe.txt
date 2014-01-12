Hasor-Core

    该项目是 Hasor 体系的核心，共分为三个部分。
“net.hasor.core.*” Hasor 的微内核，这一部分是整个 Hasor 的核心。
“net.hasor.plugins.*” 作为 Hasor 核心项目对外提供的一些插件。
“org.more.*”，这部分包含了 Hasor 依赖的一些第三方工具类；
以及我以前写的一些独立的工具包，有很大一部分工具 Hasor 没有使用它。
    目前最新版本 v0.0.3 是 Hasor 的核心软件包，几乎所有 Hasor 
扩展模块都会依赖到它。该软件包中包含了：模块生命周期管理、配置文件服务、
事件服务、环境变量、IoC/Aop、Bean。并且通过 Guice提供 JSR-330 标准的兼容。

---Aop 插件---
    通过 @Aop、@GlobalAop 两个注解提供声明 Aop 切面。@Aop 可以标记到
方法或类上，根据标记的位置来决定 Aop 切面作用的范围。@GlobalAop 注解
是标记到拦截器上的，用于配置全局拦截器，拦截范围可以通过注解进行配置。
支持表达式配置；支持 Aop 链。

---Bean 插件---
    该插件会将所有标记了 @Bean 的类通过“ApiBinder.defineBean(...)”
代码将其注册到 Hasor 容器中。注册之后可以通过“AppContext.getBean”
获取Bean对象。用过 Spring 的话一定不会陌生。

---Cache 插件---
    该插件本身并不提供缓存功能，但是为使用缓存提供了统一的接口。缓存功能
的提供需要实现 CacheCreator 接口并通过标记 @Creator 注解以生效。
使用缓存可以通过在需要缓存的方法上通过标记 @NeedCache 注解以启用结果缓存。

---Event 插件---
    通过标记 @Listener 注解声明一个 “net.hasor.core.EventListener”类型的
事件监听器。通过“EventManager.doSync or .doAsync”可以引发事件。
Hasor 中事件的处理分为同步(Sync)和异步(Async)。

---Guice 插件---
    通过 @GuiceModule 注解可以将任意一个基于 Guice 开发的“com.google.inject.Module”
模块加入到 Hasor 中作为 Hasor 的一个模块。

---Setting 插件---
    通过 @Settings 注解声明一个配置文件改变监听器。Hasor 在启动之后会持续
监听配置文件是否改变，如发生改变 Hasor 会自动重载它。标记了 @Settings 注解的
SettingsListener监听器会收到这个通知。