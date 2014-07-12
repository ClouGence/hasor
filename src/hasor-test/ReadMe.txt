Hasor-MVC

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