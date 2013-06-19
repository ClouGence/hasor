发布说明：
	名称：more_1.0.0.20110805_alpha4
	开发：赵永春。
	说明：
		进行了大范围的升级改动。

编译说明：
	整个项目采用maven进行管理。

附加说明：
bug:
  1.classcode-目前生称的方法中不支持基本类型。原因不明。
  2.classcode-对于重复使用ClassCode的字节码修改有异常。
info:
 hypha:
	1.通过 constructor-arg或property描述的属性值信息，没有通过date、bigText、ref、file、directory、uri、el等描述优先级高。如果两者同时出现则后者会替换前者。

更新说明：
more core
  1.增加了ognl表达式语言框架。
  2.恢复被删除的Base64js文件.
  3.删除重复的功能类SimpeCode.
  4.使Base64可以获取到base64.js的Reader流.
  5.增加xml软件包
  	1.提供了三种xml访问级别。
  	2.可以针对xml命名空间进行解析。
log v2.0 *
  1.重写了Log组建。
  2.增加级别概念，在不同的级别上可以输出不同级别的日志。内置了high、below、default，三个日志输出级别。
  3.日志的消息类型可扩展，内置了debug、info、error、warning，四个类型。
  4.增加了filters概念，可以通过filters来过滤起作用的日志输出源。
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
submit v4.0：
  重要更新：
  1.重新构建了Submit的内核结构，目前升级为Submit4.0。
  2.Submit接口层面内容丰富（有待完善开发）。
  3.删除了ActionFilter的概念，同时删除相关功能。
  4.新增命名空间的概念，不同的命名空间使用不同的AC进行支持(AC,全名是ActionContext是一个Bean容器。)
  5.删除了脚本回调的功能，因为考虑到执行效率关系。
  6.ActionStack在该版本中被接口化了。
  7.submit4.0，延续先前版本是一个独立的组建。不过submit4.0在hypha的支持下才显得更为强大，利用hypha它支持了注解扫描。xml化配置文件如果使用独立环境这些支持将不在有效。
  8.AC，目前已经支持的AC容器有GoogleGuice、hypha、Simple(内置的)、Spring。
  9.除了命名空间还支持包的概念，在每个ac中可以定义多个package，包是用于区分不同的action。注意：submit4.0不支持包中包。包的名称中可以包含字符“.”但是不允许包含“/”.
  10.action的定位采用URI的形式，例如:ac://package.package.package.action/param/param。
  扩展：
  1.AC、ACB
  2.内置了Web扩展。
  json，通过Result扩展可以完成将返回结果转换成为json对象。
  actionjs，目前版本删除了该插件功能。后续会使用jQuery实现相关功能。