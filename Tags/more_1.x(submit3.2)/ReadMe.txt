发布说明：
	名称：more_1.0.0.110721_alpha3
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
bug:
  1.submit的扩展actionjs如果遇到action名称中包含“.”将导致整个js执行失败。
  2.classcode-目前构造方法中无法支持float和long，两个基本类型。原因不明。
  3.classcode-对于重复使用ClassCode的字节码修改有异常。
info:
 hypha:
	1.通过 constructor-arg或property描述的属性值信息，没有通过date、bigText、ref、file、directory、uri、el等描述优先级高。如果两者同时出现则后者会替换前者。
 submit
	1.submit的返回值脚本回调处理目前版本不支持脚本编译缓存机制，所以可能有性能问题。
	2.已知在与Spring集成时，并且配置了aop。该注解将失效。其原因是由于Spring在生成新类时会自动忽略掉所有携带的注解信息。
更新说明：

workflow v2.0
  1.完成，并测试workflow.el
more core
  1.增加了ognl表达式语言框架。
  2.恢复被删除的Base64js文件.
  3.伤处重复的功能类SimpeCode.
  4.使Base64可以获取到base64.js的Reader流.
  5.增加xml软件包
  	1.提供了三种xml访问级别。
  	2.可以针对xml命名空间进行解析。
classcode v2.0
  1.升级classcode代码，全部重写相关代码。目前版本v2.0
  2.可以在对象上增加一个任意属性并且可以通过属性策略指定其readonly特性。
  3.可以增加一个代理属性，通过接口实现的方法来操控属性的get、set。
  4.增加了before,returnning,throwing三个切点的监听器。
  5.改进了aopFilter接口注入的方法。
hypha v2.0：
  1.正式推出“菌丝”组建，该组建是一个Bean容器为基础支撑环境的组建，可以替代Spring。它的前身是beans组建目前推出2.0版本并且更名为菌丝。
  2.菌丝是一个微内核的ioc容器。它可以分为三个大组成部分1.API接口、2.可扩展的模型描述信息、3.API接口实现。
  3.6种类型Bean定义的支持，其他类型持续加入。TemplateBeanBuilder、VariableBeanBuilder，不支持AOP。
  4.新的标签解析体系，使用了全新的 xml解析器引擎。使菌丝的配置文件可以与任意其他配置文件集成在一起。
  5.保留了beans v1.2中的三种注入方式，Fast、Ioc、Expend。
  6.增加了事件系统支持。
  7.增加了EL解析器，其内核是Ognl。
  8.增加了扩展点管理器，通过扩展点管理器可以参与bean装载创建等核心环节。
  9.新的 bean define体系，并且可扩展。其解析器也可以通过配置文件增加到hypha中。
  10.接口层面90%以上升级。
  11.支持自定义服务。
submit v3.2：
  重要更新：
  1.重新构建了Submit3.1的内核结构，目前升级为Submit3.2。
  2.所有submit资源都移动到META-INF目录中。
  3.几乎重写了整个Submit的代码，代码重写率达到80%以上。
  4.从核心功能中分离Filter部分，使其成为一个扩展，默认的more和spring外壳实现上都已经实现了filter部分的支持。如果有支撑环境在实现ActionContext时需要支持filter那么需要在额外实现FilterContext接口即可，SubmitBuild会自动为其装饰Filter的支持。
  5.更改submit的代码为jdk1.5兼容。
  6.增强了submit的ActionStack，可以设置回调脚本的脚本引擎名称。从而可以选择回调脚本的脚本引擎。
  修正：
  1.取消了当在web模式下的WEB-INF下配置文件位置，配置文件位置被统一到classes目录下。
  优化：
  1.简化了casing的代码量，去除了ObjectFactory类。casing只需要实现ActionContext接口和FilterContext接口即可完成casing。
  2.不在设立独立的web支撑环境，web支撑已经融入到submit的设计一部分中。
  3.简化了SubmitBuild的代码量，目前无论是build一个console还是web环境上的SubmitContext都只需要经过SubmitBuild的build方法即可完成，回避了以前那种web复杂的支撑。
  扩展：
  1.新版本Submit支持了SubmitContext和ActionContext两个核心接口的装饰器，通过这个装饰器可以扩展Submit的功能。
  2.增加了一个parentStack作用域的支持，可以指定作用域在Stack的父级上。
  3.参数作用域可以通过扩展方式注册。
json
  1.修复将对象转换成Json数据时候丢失{}和[]数据之间的逗号问题。
  2.修复了无法将下述格式的json转换成对象的bug，例:{{key:'key',age:12}:{a:'b'}}
  3.增加了字符串数据可以通过属性配置决定使用单引号还是双引号环抱数据。
  4.完成了JsonUtil类的toObject方法。
  5.扩充了JsonUtil类的构造方法，在创建JsonUtil对象时就可以指定使用单引号还是双引号环抱字符串数据。
actionjs
  1.应用新协议。
  2.解决当项目在非站点跟路径上部署时无法拦截action调用请求的bug。