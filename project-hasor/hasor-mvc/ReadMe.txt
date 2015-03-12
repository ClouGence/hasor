Hasor-MVC

介绍：
	一个轻量化的MVC框架，它的被分为两个部分一个部分可以用于非Web下的MVC模式开发，而另一个重要的部分就是Web下的MVC开发。
	它是注解化的开发框架，开发者需要通过@MappingTo来定义控制器，而MappingTo的表达式中可以配置请求参数。
	Hasor-MVC的Web方面还提供了Restful、Strategy、Result等支持，通过这些扩展可以使Web开发更加简单轻松。


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