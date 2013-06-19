发布说明：
	名称：more_1.0.0.091223_alpha2
	开发：赵永春。
	说明：
		本次发布增加6个相关例子，着6个例子是《第一部分：入门教程》的例子同时也是提供的more学习例子。
		虽然简单但是覆盖了more的基本使用。利用初级教程已经可以开发常见项目。下一个alpha版本将继续与wiki同步发出。

编译说明：
	在编译时候需要引入j2ee相关包，和spring的核心jar。
	more的发布版使用的是spring-2.5.6.jar和tomcate中相关j2ee包进行编译。
	这些jar目前不保存在svn中。

附加说明：
	1.整个项目使用GBK编码编写，js文件使用utf-8编写。
	2.submit的返回值脚本回调处理目前版本不支持脚本编译缓存机制。

补充说明：
	beans
		1.拥有bean定义的静态缓存和动态缓存。利用这两个缓存可以优化bean定义的访问速度。

更新说明：
	submit3.0(更新说明)
		1.增加了submit的Session支持。使action调用时需要保存的临时信息可以保存到session中用于缓存。
		2.提供了Session接口用来保存Action范围之外的临时数据。
		3.保留不依赖J2EE的特性，submit3.0系统仍然可以应用在普通桌面程序。submit3.0组建仍然是一个完全独立的mvc框架。
		4.提供了Action调用堆栈的支持，如果Action再次调用了其他action则堆栈会自动创建子堆栈。
		5.更改submit2.0系统中的action标记类型参数ActionMethodEvent为ActionStack。
		6.减少了扩展submit3.0时候所提供的Factory数量。
		7.删除了2.0版本中线程参数对象的特性，取代的是SubmitContext属性、Session属性、ActionStack属性。
		8.更清晰的内核结构。
		9.提供SubmitContext、Session、ActionStack的属性监听器功能。
		10.在使用Spring做容器时候不必在必须配置Spring监听器，submit3.0提供通过参数传递Spring配置文件。
		11.在集成Spring时可以配置Spring监听器，同时submit3.0的配置文件参数就会失效。
  	actionjs：
		1.解决了actionjs在服务端无法获取参数的bug。
		2.增加了min属性，通过min属性可以决定是否启用自动生成简易action调用代码。min的配置拥有作用域。
  	dao.jdbc
		1.新增的功能，一个JDBC操作环境这个环境类似于Spring的JDBC框架，至此more可以独立操作数据库。
		2.具备了一个简单的事务管理器，它可以保证业务方法的事务原子性。