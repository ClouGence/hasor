Hasor-MVC

  Hasor-MVC 是一款专门的 mvc 框架。它使用 MappingTo 定义控制器。框架在初始化时候会将标记了
该注解的方法列入 mvc 控制器列表，并通过字符串查找匹配的控制器，然后进行调用。


Strategy机制：
	  CallStrategy 接口完成策略机制。该机制类似 Aop 允许开发者为控制器上建立一个拦截器，并且支持
	策略的链接套用。通过该机制 Hasor 扩展了 Result、Around 特性。

Result特性：
	  result 特性是通过 MVC 策略机制扩展出来的一种特殊功能，可以当作插件看待。该特性允许控制器在
	调用结束返回时，额外对返回值进行特殊处理。

Web：
	  Web扩展，专注于Web上提供MVC功能，支持 restful 特性。
	@Any、@AttributeParam、@CookieParam、@Get、@Head、@HeaderParam、@HttpMethod、@Options
	@Path、@PathParam、@Post、@Produces、@Put、@QueryParam 这些注解是由这个插件提供的。
	---Result 扩展---
	  @Forword、@Include、@Json、@Redirect 就是它提供的，开发者还可以自己另外自定义扩展。