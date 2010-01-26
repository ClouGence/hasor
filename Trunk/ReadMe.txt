发布说明：
	名称：more_1.0.0.20100126_alpha3
	开发：赵永春。
	说明：
		本次发布增加4个《第二部分：中级教程》例子，同时对《第一部分：入门教程》的例子做一些调整和修复。在第一部分中增加MoreBeans例子。

编译说明：
	在编译时候需要引入j2ee相关包，和spring的核心jar。
	more的发布版使用的是spring-2.5.6.jar和tomcate中相关j2ee包进行编译。
	这些jar目前不保存在svn中。

附加说明：
	1.整个项目使用GBK编码编写，js文件使用utf-8编写。
	2.submit的返回值脚本回调处理目前版本不支持脚本编译缓存机制。

更新说明：
beans v1.2(版本alpha3)：
  1.增加注解配置支持，至此xml配置与注解支持可以同时进行。xml的优先级高于注解。
  2.可以注解配置Bean。
  3.可以注解配置Bean的构造方法参数注入
  4.可以注解配置Bean的Aop链。
  5.可以注解配置附加接口实现，但是所有附加接口都使用一个接口处理委托。如果要一个接口对应一个委托只有xml配置支持。
  6.可以注解配置属性注入。
  7.注解配置支持以定义的基本类型和四种引用类型。
  8.注解配置需要通过xml配置anno:anno标签。
  9.通过anno标签可以开启注解配置以及配置beans的扫描路径。
  10.重写了Resource接口的实现类。
  11.修复了大量Bug。

submit v3.1(版本alpha3)：
  1.TopFilter类更名为SubmitRoot。
  2.优化submit代码，减少了代码中new的操作。
  3.修改submit的Config接口getInitParameter相关方法返回值为Object类型。
  4.重写了submit的more和spring外壳提供环境使其更简化和直观。同时more和spring外壳支撑中加入了默认配置文件位置。
	其中more支撑环境默认配置文件位置：/WEB-INF/more-config.xml
	其中spring支撑环境默认配置文件位置：/WEB-INF/applicationContext.xml
  5.CasingBuild增加缓存功能，可以通过cacheContext属性控制是否开启缓存，默认是开启的。
  6.增加ActionObjectFactory核心接口，该接口负责创建、查找一切Action相关资源，同时该负责解析Filter注解。
  7.支持注解化配置Action所有配置。