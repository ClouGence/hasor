Hasor-Quick

---Aop 插件---
    通过 @Aop 注解提供声明 Aop 切面。@Aop 可以标记到方法或类上，
根据标记的位置来决定 Aop 切面作用的范围。支持 Aop 链。

---Bean 插件---
    该插件会将所有标记了 @Bean 的类通过“Beans.defineForType(...)”
代码将其注册到 Hasor 容器中。注册之后可以通过“Beans.getBean”
获取Bean对象。用过 Spring 的话一定不会陌生。

---Cache 插件---
    该插件本身并不提供缓存功能，但是为使用缓存提供了统一的接口。缓存功能
的提供需要实现 CacheCreator 接口并通过标记 @Creator 注解以生效。
使用缓存可以通过在需要缓存的方法上通过标记 @NeedCache 注解以启用结果缓存。

---Event 插件---
    通过标记 @Listener 注解声明一个 “net.hasor.core.EventListener”类型的
事件监听器。通过“EventManager.doSync or .doAsync”可以引发事件。
Hasor 中事件的处理分为同步(Sync)和异步(Async)。

---Setting 插件---
    通过 @Settings 注解声明一个配置文件改变监听器。Hasor 在启动之后会持续
监听配置文件是否改变，如发生改变 Hasor 会自动重载它。标记了 @Settings 注解的
SettingsListener监听器会收到这个通知。

---Servlet3 插件---
    该插件是用来支持Servlet3.0 规范的软件包，当 Servlet 容器不支持 Servlet3.0 规范时可以
通过该插件提供的 @WebFilter、@WebServlet、@WebInitParam 来实现 Servlet3.0。

---Resource 插件---
    通过这个插件可以将位于ClassPath、Zip等位置中的资源用作 Web 请求响应。插件是以
Servlet 方式提供，开发者需要自己注册它。