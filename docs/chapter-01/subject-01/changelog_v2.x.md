
### Hasor-Core v2.5.2 (2016-12-01)
**新增**
    01. 新增 MyBatis 插件，该插件会依赖 DBModule。
**改进**
    01. 整理出一个 Provider 实现工具类包。
    02. BindInfoBuilder 接口迁移位置。
---

### Hasor-Core v2.5.1 (2016-11-08)
**新增**
    01. 新增 JFina 插件支持。HasorPlugin、HasorInterceptor、HasorHandler 三个插件。
    02. AppContext 新增两个 justInject 方法用于对某个标记了 Hasor 注解的对象执行依赖注入。
    03. SpringModule 插件类，增加 DefaultHasorBeanName 常量，表示当 Hasor 和 Spirng 集成时。如果 Spring 中的那么默认 Bean ID。
**改进**
    01. Spring sechma 升级到 2.5.1。factoryID 不再是强制输入。如果没有配置 factoryID 会采用 "net.hasor.core.AppContext" 为默认名。
    02. 简化 Spring 集成，取消与 Spring 的 Event 双向打通机制，如果开发者需要该功能可以自行基于 Spring 实现。
    03. Hasor 在启动的时候可以从外面传递 Map 来指定环境变量了。之前只能通过 env.config 来指定环境变量。
    04. 增加 WebHasor 类，继承自 Hasor 类提供方便的 WebAppContext 初始化入口。
**修复**
    01. Fix 因 ClassLoader 不同而导致的 shutdown 过程中可能的异常抛出。
    02. Fix Spring集成时，无法使用最简化 Hasor 配置的问题。
---

### Hasor-Core v2.5.0 (2016-10-26)
**新增**
    01. 新增 ClassAwareProvider、InfoAwareProvider 两个工具类。
    02. Hasor 新增一个 autoAware 方法的重载。
    03. 新增 Hasor 和 Junit 的整合，通过 Junit 做 Hasor 单元测试更加方便了。
    04. ApiBinder 接口新增 getBindInfo、findBindingRegister 两组共计4个方法，现在 init 期间也可以查询其他模块注册的 Bean 了。
    05. BeanBuilder 接口增加 getBindInfo、findBindingRegister 两组共计4个方法。其子类加以实现。
    06. AppContext 的 getBindInfo、findBindingRegister 方法实现沉降到 BeanBuilder 实现类中。
**改进**
    01. 升级依赖 slf4j-api 到 1.7.21 版本。
    02. 删除 ContextEvent_Initialized 事件，该事件等同于 Module 的 loadModule。
    03. ContextShutdownListener、ContextStartListener两个内部接口提升为 Hasor Api接口。
    04. findModules 过程提升到 doInitialized 之前。
    05. 改进 BeanContainer 使其可以实时检测 ApiBinder 使用期间出现的 id 冲突等问题。而不是等待 init 后期最统一检测。
---

### Hasor-Core v2.4.6 (2016-10-07)
**新增**
    01. restful插件增加,json; 拦截。
    02. WebEnvironment、WebAppContext可以被依赖注入了。
    03. 所有接口类型参数传入,都走 AppContext 获取。
    04. upload包变更。
    05. 新增 WebAppContext.HTTP_BEFORE_REQUEST 和 WebAppContext.HTTP_AFTER_RESPONSE 事件。
    06. 新增了 ScopeManager 接口用于优化 Scope 的注册。
**改进**
    01. 改进 RenderData 接口使其更加人性化一点。
    02. RenderData 的数据不在保存在自己独立的 map 中而是共同 Request Attr。
    03. @ValidBy 注解支持配置多个表单验证器。
**修复**
    01. fix restful插件,action方法中无法传入 response 的问题。
---

### Hasor-Core v2.4.5 (2016-09-29)
**新增**
    01. BindInfo 新增 setMetaData 方法以设置元信息。
    02. BindInfo 新增 removeMetaData 删除元数据的方法。
    03. BizCommon 包增加 log 工具。
    04. Spring 集成插件,新增支持将 Spring 容器中的 属性配置文件导入 Hasor 作为环境变量。
    05. @InjectSettings 注解支持通过 "${xxx}" 形式解析并注入环境变量了(至此注入环境变量不再需要通过 hasor 配置文件中转)
**改进**
    01. JSON 包简化了对 Log 的输出处理,减少了 5 个日志相关类。
    02. Spring集成时,支持通过 Spring 方式来指定 Hasor 的配置文件。
    03. Spring集成下,env.config 将会失效,环境配置属性需要通过 Spring导入。
    04. AbstractEnvironment 类的 afterInitEnvironment 方法取消参数传入。
    05. AbstractEnvironment 类的 initEnvironment 方法增加boolean参数来决定是否启用 env.config 配置文件。
    06. LinkedCaseInsensitiveMap类提升为公共工具类,包位置发生变更。
**修复**
    01. Fix Settings 接口的 add &amp; remove &amp; set 系方法,解决 settings key 出现大小写敏感问题。
    02. Fix AbstractEnvironment 的 refreshVariables 方法,忽略了对默认值的替换,导致了bug出现。
    03. Fix Environment 接口拼写错误的方法名。remoteEnvVar -&gt; removeEnvVar。
---

### Hasor-Core v2.4.4 (2016-09-05)
**新增**
    01. 宽泛的Servlet版本支持 servlet 2.3 到 servlet 3.1。
    02. 新增支持文件上传功能支持。
    03. 新增 @Async 注解,用于标记 restful 请求是否以 异步 servlet 方式执行。(需要容器支持 servlet 3 ,否则无效)
    04. 如果容器支持 Servlet 3 那么 Hasor 会自动启用 Servlet 3.0 特性。
    05. servlet3: 基于 web-fragment 技术,您的 web.xml 中不需要任何配置或者您干脆删除 web.xml。
    06. servlet3: 支持通过@Async注解开启异步Servlet。
    07. servlet3: 文件上传共功能可以基于@Async转为异步文件上传(感谢:哎瑞！麓孩 提供的实现思路,这里是他的blog, http://ysj12.lofter.com/)。
**改进**
    01. otg.more包瘦身前的一些调整。
    02. WebController类中方法访问修饰符调整。
---

### Hasor-Core v2.4.3 (2016-08-18)
**新增**
    01. 新增 Htmlspace工具类:StringEscapeUtils、EntitiesUtils。工具来源为:apache-common
**改进**
    01. 改进启动日志输出。
    02. WebController 增加一组 putData 方法用于输出到渲染模版中。
    03. JdbcOperations接口可以通过依赖注入被注入到 Service 中。
    05. TransactionTemplateManager 代码优化,修复潜在的 npe 异常情况。
    06. ValidErrors 接口继承自 RenderData 获取参数更加方便。
    07. WebController 类重度优化,删除了一些重复的方法,增添表单验证相关的方法。
---

### Hasor-Core v2.4.2 (2016-08-04)
**新增**
    01. Restful 框架整合 Validation 插件增加请求参数 @Valid 验证功能。
    02. 注解 @Valid 支持场景化验证。
**改进**
    01. env.config 加载环境变量遇到名称冲突时的,Bug。
    02. DBModule 类,在声明 Aop 类匹配的时候从任意类,改为需要标记 @Transactional 注解的类。
---

### Hasor-Core v2.4.1 (2016-08-01)
**改进**
    01. env.config 功能的优化。
---

### Hasor-Core v2.4.0 (2016-07-29)
**新增**
    01. 新增 DecSpaceMap 可以将多个Map合并成一个Map对象给予操作，每个子map可以通过一个空间字符串进行标识。
    02. AbstractSettings 类增加 resetValues 方法,可以通过 UpdateValue 接口更新载入的配置文件数据,一个典型的应用场景是模版化配置文件。
    03. 支持配置文件模版化,通过模版化的配置文件。可以在不修改部署包的前提下,替换配置文件的值。使其可以方便的适用于各种环境，例如:日常、预发、线上、隔离。
    04. 新增 @InjectSettings 注解,支持配置文件数据注入。
    05. Restful、Template、mime 三个小插件整合到一起，提升为 webmvc 框架，编程接口依然以 Restful 为标准。
    06. Render渲染器,支持多种,并通过viewType可以动态指定。
    07. 依赖注入支持 WebAppContext、WebEnvironment 两个接口的注入。
**改进**
    01. 配置文件加载在原有xml格式下新增 "属性类型" 文件的加载支持，属性文件默认采用UTF-8编码方式读取。
    02. Settings 接口的两个 findClass 方法移动到 Environment 接口中 Settings的职责更佳明确，只负责配置文件相关的操作。
    03. 删除 AbstractMergeSettings 不在需要该类承担数据的职责，Settings的数据承载交给 DecSpaceMap。
    04. Settings 小范围重构，内部的数据管理更佳清晰。
    05. StartupModule插件的类装载，使用 Environment 接口提供的。
    07. Resource 插件被拆分成两个部分。web相关的部分降级到demo中，资源加载部分沉淀到 org.more 工具包。
    08. Encoding 插件被降级成为 demo 的一部分。
---

### Hasor-Core v2.3.3 (2016-06-16)
**新增**
    01. 新增@ImplBy注解，用于方便接口注入。
---

### Hasor-Core v2.3.2 (2016-05-29)
**改进**
    01. 事件机制，callBack.handleComplete的调用实际改为，事件处理结束。
    02. 一些方法的注释进行更正。
    03. 新增datachain工具，可以脱离Hasor使用：数据对象转换工具，提供 A 类型对象到 B 类型对象转换功能。并使开发者在转换过程中可以实现更加高级别的控制协调能力。
    04. Result增加一系列Message相关方法。
    05. 插件智能载入。所有内置插件，在初始化的时都做了配置检查，只有用到了它们才会被加载到框架中。否则内置插件在初始化期间就会放弃加载自己。
---

### Hasor-Core v2.3.1 (2016-04-13)
**修复**
    01. Fix ：AbstractMergeSettings在刷新加载配置文件的时，因为map无序而导致。主配置文件被率先放入最终结果集中，正常的逻辑应该是最后放入。
---

### Hasor-Core v2.3.0 (2016-04-06)
**新增**
    01. 新增“.hasor.default.asEagerSingleton”配置用来配置默认情况下，类型是以单例模式运行还是以原型方式运行。（默认单例模式）
    02. ApiBinder接口新增“asEagerPrototype”方法用来强制以原型方式注册。到目前为止可以选择的方式有：“asEagerSingleton”、“asEagerPrototype”
    03. 新增注解配置：@Singleton、@Prototype
    04. 单例类如果配置了@Init注解,则在注册到Hasor容器时，会在容器启动的第一时间自动调用init方法以完成对象初始化功能。
        - 这个特性类似Spring配置文件中 init属性的功能。
    05. Hasor启动类新增传入File参数方式指定配置文件。
**改进**
    01. 容器在启动时增加EventContext类型的绑定，开发的时候可以直接通过依赖注入或者appContext.getInstance(EventContext.class)方式的到。
    02. Event插件在接收到事件响应时，需要一直等待AppContext对象被注入进来。原有逻辑是等待10秒注入AppContext对象。
    03. 优化EventModule启动时间。
    04. 事件处理线程命名。
    05. 改进环境变量处理方式，凡是Hasor中定义的环境变量其优先级都高于系统环境变量。这意味着，定义相同名称的环境变量Hasor中配置会覆盖系统的配置。
---

### Hasor-Core v2.2.0 (2016-02-23)
**新增**
    01.增加Event事件注册插件，简化事件的注册机制。
    02.Settings接口增加 addSetting方法和clearSetting方法用来增加和删除配置项。
    03.新增Spring插件，完美与Spring整合。同时支持与Spring的双向事件通知。
        - 支持Hasor的Bean通过Spring获取、支持SpringBean通过Hasor获取。
        - 支持Hasor的事件，通过Spring方式接收、支持Spring的事件，通过Hasor方式接收。
**改进**
    01.改进事物管理器的拦截器注册机制，从拦截所有类改为只拦截标记了Transactional注解的方法。
    02.ResourceModule插件的实现机制从 Servlet 改为Filter，当资源无法通过插件获取时候，转交给servlet容器。原有方案是直接抛除404。
    03.RestfulModule插件的实现机制从 Servlet 改为Filter，可以通过WebController类中renderTo方法指定具体要渲染的模版，模版渲染更加灵活。
    04.RestfulModule插件的实现机制从 Servlet 改为Filter，支持ContextMap中setViewName方法来指定渲染的模版。
    05.环境变量，WOR_HOME 从 USER.DIR 更换到 USER.HOME。原因是，USER.DIR 工作目录获取并不是想象的那样始终是在程序位置。
    06.SaxXmlParser类优化，在处理配置项冲突时，升级为保留全部配置。原有逻辑为合并覆盖。
    07.Event接口在传入参数时不再使用“Object[]”方式，改为范型T，这样做简化了开发者在使用事件机制时各种类型转换的麻烦，从而减少错误的概率。
---

### Hasor-Core v2.1.0 (2016-01-17)
**新增**
    01. 增加一个WebDemo示例工程。
    02. 新增一个插件，简化“modules.module”的配置。
    03. 新增restful插件，做为hasor内置Web开发插件。
    04. 添加templates插件，该插件将提供模版渲染支持。
**改进**
    01. 修改ShutdownHook钩子。在start时注册它、当shutdown时解除注册。
    02. 增加Environment接口的包装器。
    03. 为@Inject注解，增加Type枚举。通过枚举可以标识注入是：ByID 还是 ByName。
    04. 剔除JSP自定义函数功能。
    05. resource插件在，选择缓存目录时，如果连续失败99999次。将会报一个错误，然后终止插件的启动。
    06. templates插件与resource插件，整合了mimetype插件功能。
    07. Valid插件增加@ValidDefine注解方式定义验证。
**修复**
    01. Fix “Shutdown in progress”异常。
    02. Fix Web模式下启动空指针异常。
    03. Fix @Inject 注解携带value参数时失效的问题。
    04. Fix JdbcTemplate使用Result－&gt;Object映射时，最后一个参数应设值丢失的问题。
---

### Hasor-Core v2.0.0 (2015-11-27)
**新增**
    01. 新增 @Inject、@Init 两个注解以支持注解方式的自动注入。
    02. 添加 ShutdownHook钩子，当外部终止jvm的时候，Hasor可以引发shutdown过程。
    03. 事务管理增加“TransactionTemplate”接口。
    04. 启动过程中增加了一些 log 的输出。
    05. 将jetty的JSON解析器代码添加到Hasor工具代码中，位于包“org.more.json”。
    06. 新增WebApiBinderWrap类。
    07. ASM包升级为5.0版本，原有的ASM组建在解析jdk1.8的类文件时会有异常。
**改进**
    01. StandardEnvironment构造方法改进。
    02. StartModule接口更名为LifeModule，并新增了onStop方法。至此通过LifeModule接口可以得到模块整个生命周期。
    03. AbstractEnvironment类的initEnvironment方法增加Settings类型参数。createSettings方法不再属于AbstractEnvironment的抽象方法。
    04. StandardEnvironment类增加Settings类型参数的构造方法。
    05. MimeType接口增加getContent()方法。
    06. 原有模块在实现 StartModule 接口时，如果是通过启动参数或者配置方式的模块，器onStart调用时间点在“ContextEvent_Started”事件之后。
        - 现改为引发“ContextEvent_Started”事件时。
    07. MVC的插件分离成独立插件。
    08. db包“datasource”模块重构、简化逻辑，它不再提供数据库连接和当前线程的映射绑定。
    09. 事务管理器模块大量优化，同时“Manager”更名为“TranManager”。可以更好的让人理解。
    10. 事务管理器负责提供数据库连接与当前线程的绑定关系。
    11. 删除ResultModule类和其相关的功能，该功能不再是核心功能的一部分。
    12. MVC框架被迁出 Hasor框架成为一个独立的Web开发框架名为“haweb”。
**修复**
    01. 大量优化。。
    02. Fix，classcode模块对long、float、double基本类型错误处理的问题。
    03. AbstractClassConfig增加对 java javax 包类的排除，凡是这两个包的类都不进行aop。
---