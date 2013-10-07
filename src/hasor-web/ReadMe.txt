Hasor-MVC v0.0.1.20130831-M1
    1.)WebMVC开发框架
    2.)Action拦截器
    3.)RESTful使用接口简单，JSR-311标准的部分实现。
    4.)支持从（Zip\Jar\ClassPath\任意目录）中加载Web静态资源文件，为资源文件管理提供支持。
    5.)支持JSON格式数据响应。
    6.)自定义Action返回值处理器。


Hasor-Web v0.0.1
	1.重构：基于Hasor-Core v0.0.1.Release，包含了所有v0.0.1.20130831-M1版本中Hasor-Core的Web相关功能。同时包含了全部 Hasor-MVC 功能。
	2.修改：在 Servlet 支持中删除注解的支持，相关功能以 Gift 方式实现。 
	3.修改：重构 Controller 控制器设计和实现，并将Restful部分功能从 控制器中剥离出来。
	4.新增：强制要求 Action 必须继承自 AbstractController 抽象类。
	5.新增：AbstractController 类被设计用在 单例 或 多例 的情况下。

Hasor-Web v0.0.2
	1.修复#5314 Hasor-Web v0.0.1- Restful 服务无法正常发布的问题。
	2.优化抛出的异常。
	3.接口WebApiBinder新增方法，可以通过代码形式注册 Servlet/Filter时指定顺序。
	4.修复#5501 从 WebApiBinder 接口中注册的 Filter 启动顺序不可控的问题。
	5.新增 Restful 拦截器的支持。