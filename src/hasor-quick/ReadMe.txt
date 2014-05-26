Hasor-Quick

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

---Controller 插件---
    通过 @Controller 注解提供了 WebMVC 开发所需的支持。该插件还提供了请求响应拦截器功能。
所有控制器都必须要求继承自 AbstractController 抽象类，如果某个方法不像被发布成 action，
可以通过@ControllerIgnore 注解或者配置“hasor-web.controller.globalIgnore”隐藏它们。
    
---Restful 插件---
    通过 @RestfulService 注解发布 Restful 服务的支持，Hasor 的 restful 参考了 JSR-311。
@Any、@AttributeParam、@CookieParam、@Get、@Head、@HeaderParam、@HttpMethod、@Options
@Path、@PathParam、@Post、@Produces、@Put、@QueryParam 这些注解是由这个插件提供的。
    
---Result 插件---
    该插件是 Controller、Result两个插件的扩展插件，它为上述两个插件提供了返回值集处理机制。
@Forword、@Include、@Json、@Redirect 就是它提供的，开发者还可以自己另外自定义扩展。

---Servlet3 插件---
    该插件是用来支持Servlet3.0 规范的软件包，当 Servlet 容器不支持 Servlet3.0 规范时可以
通过该插件提供的 @WebFilter、@WebServlet、@WebInitParam 来实现 Servlet3.0。

---Resource 插件---
    通过这个插件可以将位于ClassPath、Zip等位置中的资源用作 Web 请求响应。插件是以
Servlet 方式提供，开发者需要自己注册它。