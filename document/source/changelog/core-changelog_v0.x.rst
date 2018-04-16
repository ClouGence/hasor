--------------------
Release Hasor v0.x
--------------------

Hasor-Core v0.0.14 (2015-06-18)
------------------------------------
**新增**
    - Settings接口增加merageXmlNode方法用于汇聚XmlNode。
**改进**
    - JdbcTemplate 类中的公共方法分离出一个新的基类JdbcConnection。
    - Result - 添加Message消息传输机制。
    - 放弃Hasor-AR项目，删除AR相关的软件包，目前为止Hasor-DB原生不支持ORM。后续扩展可以支持Hibernate，ibatis，mybatis。
    - 保留Hasor-AR项目中，PageResult、Paginator两个工具类
**修复**
    - Fix 日志输出了格式化字符。
    - Fix 多配置文件时候，主配置文件失效问题。
    - Fix 相同模块在不同的“static-config.xml”配置文件中出现时，解决重复加载问题。
    - 扫清，FindBugs 扫描出来的Bug。


Hasor-Core v0.0.13 (2015-05-09)
------------------------------------
**改进**
    - 删除封装的log，采用slf4j。jdk自带的log因为采用了SystemClassLoader导致log失败。
    - 修改测试用例，取消log的封装。


Hasor-Core v0.0.12 (2015-05-09)
------------------------------------
**简介**
    - all-in行动，db和web项目被合并到core，同时合并一些quick插件。
**DB**
    - 新增 orm 工具包，携带了一个及其简易的数据库操作框架。
    - 新增 SimpleDBModule 工具类，便于为一个数据源配置 事务管理器。
    - LocalDataSourceHelper 支持每个线程绑定多个数据库连接。
    - 支持7种事务传播属性。
    - 支持隔离级别设置。
    - 支持多数据源。
    - 支持多数据源事务控制（非JPA）
    - 提供@Transactional简单的声明式事务注解。
    - 开发者可以自定义事务拦截器，自定义注解拦截事务。
    - 新增 SavepointDataSourceHelper 接口，并且 DefaultDataSourceHelper 类支持该接口。
    - 新增可以通过 DataSourceUtils 获取当前连接的方法。
    - 通过 net.hasor.db.jdbc 软件包为 Java 应用程序提供了一个轻量化 JDBC 操作接口。
    - 软件包 net.hasor.db.datasource 为 Java 应用程序提供了数据源服务。
    - 增加事务管理器、事务拦截器
**Web**
    - 增加 shutdown 生命周期阶段。
    - 更换LoggerHelper为日志输出组件。
    - 环境变量实现接口调整，应用Core改动。
    - 增加 MimeType 获取工具Module。
    - AnnoWebAppContext 类中增加，下面几个接口的绑定。
      ServletRequest、HttpServletRequest、ServletResponse、HttpServletResponse、HttpSession、ServletContext
    - 新增 JSP Tag 标签库，通过 Hasor JSP 标签库可以方便基于 jsp 的 Hasor 开发。
**Quick**
    - 新增@Aop注解插件，用于简化Aop声明，只要类通过binder绑定到系统中就会支持。
    - 新增Web请求响应编码设置，默认为UTF-8。
    - 新增资源插件，可以从classpath\path\zip中提取资源作为web响应。
**改进**
    - StandardEventManager 改为无锁方式实现。
    - 删除封装的log，采用slf4j。jdk自带的log因为采用了SystemClassLoader导致log失败。


Hasor-Core v0.0.11 (2015-04-24)
------------------------------------
**修复**
    - LoggerHelper,修复当多次调用log输出时产生堆栈溢出问题。


Hasor-Core v0.0.10 (2015-04-03)
------------------------------------
**新增**
    - 添加org.more.future工具包，用于实现 java.util.concurrent.Future 接口功能。
    - 新增一个 ApiBinderWrap类。
    - 增加 SettingsWarp 工具类。
    - 增加 shutdown 生命周期阶段。
    - 增加 apache 中 toStringBuilter 工具到 org.more.builder。
    - 增添业务基础包org.more.bizcommon（将来如果发展大了会分离出去，目前就几个类）
    - 增添 StartModule 接口。
**改进**
    - 修复当程序工作在特殊 ClassLoader 下例如 （Web容器中）时，MoreClassLoader类导致 Class 重复加载问题。
    - 优化，AbstractSettings 实现细节，解决在多线程下可能引发的 HashMap 死锁问题。
    - AopClassConfig、MethodClassConfig、MethodClassConfig 类增加了一个无参的构造方法。
    - ClassUtils 增加 getSuperClassGenricType 方法以获取泛型信息。
    - 内部实现增加 CustomerProvider接口。
    - 内部实现增加 MetaDataAdapter类。
    - 不再依赖slf4j日志包，日志输出采用jdk自带的，至此 Hasor实现了0依赖。
    - 分离出专用的 log 工具包 org.more.logger。
    - 环境变量实现接口调整，接口有删减。原有接口当设置Java系统属性之后，环境变量组建不能识别它（Bug）。
    - 修复 ScanClassPath 类无法正确扫描到父类中接口实现的Bug。


Hasor-Core v0.0.9 (2014-09-21)
------------------------------------
**新增**
    - Hasor 的依赖注入使用 InjectMembers接口。
**改进**
    - 优化 AppContext 接口实现类的层次关系。
    - AppContext，不再支持 addModule 这种形式添加模块，改为通过 start 方法传入。
    - AppContext，不在支持 getParent()方法。
    - context部分重构，精简了设计。
    - Hasor 不再依赖 Guice or Spring。


Hasor-Core v0.0.8 (2014-07-26)
------------------------------------
**新增**
    - 确立类型绑定机制。
    - 确立Bean机制。
    - 内置Aop接口，不再直接依赖Aop联盟的包。
    - 支持Scope。
    - 支持Provider。
    - AopMatchers类型新增subClassesOf方法。匹配给定类型的子类（或实现了的接口），迁移至Hasor-Quick项目
**改进**
    - Settings接口的 getNameSpace 方法改名为 getSetting。
    - AppContext接口的 findBean、findProvider 方法更名 findBindingBean、findBindingProvider
      使其接近ApiBinder接口中bindingType方法的命名以便于开发者理解。
    - 所有Plugins都移动到Hasor-Quick项目中。
    - 将Hasor中start、stop生命周期，相关的支持全部删除，不再支持插件生命周期
    - 所有主要接口50%以上进行改进，改进的目的是不再强制依赖Guice
    - 添加Provider接口、EventContext接口
    - 删除ModulePropxy类、删除启动依赖检查功能
    - 瘦身（去掉JSON包、Ognl包、其它一些工具包）


Hasor-Core v0.0.7 (2014-01-17)
------------------------------------
**修复**
    - 修复 AbstractAppContext 当类扫描器扫描不到 AppContextAware 接口实现类时能引发整个doStart阶段的退出。
    - 修复当使用 Listener 注解注册容器事件时，导致ContextEvent_Initialized、ContextEvent_Started 两个事件无法正确调用的问题。
    - 修复当配置文件中，根节点下第一层节点名称如果与根节点同名，导致的数据混乱问题。
    - 修复 new AnnoStandardAppContext() 引发的Bug。
    - 修复 FileEnvironment 在创建 FileSettings 时候导致重复加载配置文件的问题。
**新增**
    - Settings 通过映射 key "." 可以取得根节点。
    - 新增 @GlobalAop 配置全局拦截器。
    - 新增 PluginHelper 工具类，可以将一个插件转换成为 Hasor 的 Module。
    - Settings 接口新增可以设置新值的功能。
    - Environment 接口新增可以设置包扫描范围的方法。
    - AbstractAppContext 类新增 addGuiceModule 方法可以直接将 Guice 模块加入到 Hasor 中。
**改进**
    - Settings接口的 getNameSpace 方法改名为 getSetting。
    - 改造 Hasor 的类扫描机制，从 Class.forName 改为字节码解析（性能上仍需要进一步优化）。
    - 部分接口方法名称更换。
    - 修改 Cache 接口的 clean 方法名为 cleanCache，以避免和 Map 接口冲突。
    - GlobalProperty接口更名为FieldProperty


Hasor-Core v0.0.6 (2013-12-21)
------------------------------------
**修复**
    - 改进 当使用 JSON 将（List、Map、基本类型）类之外的类型尝试序列化时会得到一个异常。修复这个问题，当遇到这种情况工具将使用 JSONPojoConvertor 对其进行序列化。
**新增**
    - 新增在注册 ServicesRegisterHandler 时使用 Class 类型。注册的 Handler 支持 依赖注入。
    - AppContext 接口新增 lookUpRegisterService 方法。可以通过该方法取得注册的 ServicesRegisterHandler 对象。
    - BeanUtils 工具类新增 copyProperty 用于做属性拷贝的方法。（支持POJO 与 Map 之间相互拷贝）
**改进**
    - 改进 Hasor 类使其输出日志时不在必须通过 new Exception 来确定方法调用位置，从而提升日志输出性能。
    - 改进 AopInterceptor 类中用于缓存作用的 methodInterceptorMap 属性，将其从 HashMap 类型改为 WeakHashMap。
    - 改进 AppContext 当处理 ServicesRegisterHandler 的注册和解除注册时新增 boolean 返回值。
    - Hasor 类，归为 core 软件包。
    - 改进 ConverterUtils 当使用“ConverterUtils.convert(Date.class, null);”转换时间日期时出现异常。
    - 删除 Aware 插件，该插件的 AppContextAware 接口将由 “net.hasor.core” 直接支持。
    - 从 “net.hasor.core” 中剔除鸡肋的 ServicesRegisterHandler 功能。该功能于 v0.0.4 版本加入。
    - AbstractAppContext 梳理内部方法调用过程，通过受保护的方法暴露一些内部执行过程。
    - 所有容器事件发送都改为 “当完成某个阶段的任务之后在引发阶段事件”


Hasor-Core v0.0.5 (2013-11-25)
------------------------------------
**新增**
    - 新增 DomXmlAccept 工具类，将 Xml 文件转换为  XmlNode 接口形式的 Dom 树。
**改进**
    - 修复 DecSequenceMap 工具类，removeAllMap 方法引发 ConcurrentModificationException 异常的问题。
    - 修复 ApiBinderModule 类构造方法没有处理 Binder 参数导致插件加载失败的问题。
    - 修改 AbstractAppContext 类的 Guice 创建方法 createInjector。
    - Cache 接口方法有关 key 参数从 String 类型更换为 Serializable。并增加了一些方法。
    - 修改 StandardContextSettings 类，确保位于jar包中的“static-config.xml”资源在合并时优先级低于file。


Hasor-Core v0.0.4 (2013-11-11)
------------------------------------
**新增**
    - Environment 接口新增 isDebug 方法，用于表示应用程序启动是否为 debug 模式。
    - Hasor 类新增 isDebugLogger、isErrorLogger、isWarningLogger、isInfoLogger 方法用于判断日志是否支持该级别的输出。
    - 原有 Hasor 类中 debug、error、warning、info 日志输出方法会判断响应级别的日志输出是否支持。
    - 增加 Hasor 类中 trace、isTraceLogger 方法，用于处理 Trace 日志级别。
    - 增加服务注册机制，详见 ServicesRegisterHandler 接口用法。
    - 增加 ModuleEvent_Start、ModuleEvent_Stoped事件。
    - ApiBinder、AppContext 接口中新增一些有关 BindingType 的工具方法。
    - 新增 Cache 插件。
    - 新增 Aware 插件，提供给不方便通过 Inject 注解注入 AppContext 接口的类，使其在 AppContext 在 Start 的第一时间得到注入。
    - 新增 日志输出，可以看到插件列表，并且展示了插件是否加载成功。
**改进**
    - 变更 ContextEvent_Stop 事件为 ContextEvent_Stoped。
    - 删除 giftSupport 属性的配置，Gift 扩展方式不在通过配置文件形式启用。
    - Gift 体系更改为 Plugin，原本 Gift 是作为模块的一个补充，目前改为框架功能插件体系。
    - 代码大面积优化。


Hasor-Core v0.0.3 (2013-10-09)
------------------------------------
**改进**
    - 修改根POM改为 0.0.2 该版本可以处理 GBK 编码下 Javadocs 生成。
    - 改进JavaDoc内容的质量。


Hasor-Core v0.0.2 (2013-09-29)
------------------------------------
**修复**
    - StandardAppContext调用无参构造方法引发异常的问题，同时修改几个核心类的构造方法。
**新增**
    - 新增以模块类名为事件名，当执行 Init\Start\Stop时候，抛出对应事件。
    - 增加 Gift 体系用于扩展非模块类小工具。
**改进**
    - DefaultXmlProperty类更名为DefaultXmlNode，并且XmlNode增加几个常用方法。
    - 删除所有Mapping部分支持，相关代码移到demo作为例子程序。
    - AbstractAppContext类中有关事件的声明移动到 AppContext 接口中。
    - Before 注解更名为 Aop 注解，性能进行了优化。
    - ASM升级为4.0、ClassCode连带升级。


Hasor-Core v0.0.1 (2013-09-15)
------------------------------------
**新增**
    - 增加@GuiceModule注解，可以标记在com.google.inject.Module接口上，可以将Guice模块引入到Hasor中。
**改进**
    - Hasor-Core：80%以上代码重构，重构主要涉及内容的是结构性重构。
    - InitContext接口功能合并到Environment接口中。
    - ApiBinder接口增加模块依赖管理。
    - HasorModule接口更名为Module。
    - HasorEventListener接口更名为EventListener。
    - XmlProperty接口更名为XmlNode。
    - config-mapping.properties属性文件的解析不在是必须的。
    - 重构Settings实现。Xml解析方式不在依赖ns.prop属性文件，实现方式改为Sax。
    - Module注解，更名为AnnoModule。
    - 重构AppContext实现。
    - 包空间整理，所有包都被移动到net.hasor下，整理License文件。删除残余的、无用的类。
    - 删除所有与Web相关的支持，这部分功能全部移动到Hasor-Web（Hasor-MVC更名而来）。
    - 生命周期：合并onReady和onInit两个生命周期阶段方法，删除销毁过程。
    - 工具包修订：ResourcesUtils工具类中，类扫描代码优化。
    - 工具包修订：DecSequenceMap.java、DecStackMap.java两个类文件增加一些有用的方法。
    - 所有Demo程序都汇总到demo-project项目中。
