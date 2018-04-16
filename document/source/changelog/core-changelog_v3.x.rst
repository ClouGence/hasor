--------------------
Release Hasor v3.x
--------------------

Hasor-Core v3.2.3 (2018-04-?)
------------------------------------
**改进**
    - 当使用 maven-shade-plugin 进行 maven 打包时由于 static-config.xml 无法通过文件追加的方式进行简单合并，因此老版本 Hasor 的工程无法使用 maven-shade-plugin 打包。
      3.2.3版本之后修改了 static-config.xml 发现机制，改为通过 /META-INF/hasor.schemas 配置文件进行发现。
      改进之后，使用 maven-shade-plugin 的 hasor 工程可以像处理 spring.schemas 一样处理 hasor.schemas 合并。
    - BeanUtils 类的 canWriteField，修复了对 Field 为 final 的判断。
    - rsf的内置 hessian 序列化和反序列化配置文件，路径改到 ‘META-INF/hasor-framework/rsf-hessian/’ 下面。以避免和 hessian jar包冲突。
    - rsf Gateway 从 rsf 基础框架中删除后面会独立成一个框架。
    - env.config 配置文件名，更名为 hconfig.properties。
**修复**
    - fix 执行查询结果返回为空时，AbstractRowMapper.convertValueToRequiredType 方法报 NPE 的 Bug。


Hasor-Core v3.2.2 (2018-01-02)
------------------------------------
**新增**
    - AppContextWarp 类,提供 AppContext 包装工具。
    - DataQL UDF 新增 manager 接口方便管理和注册。
    - DataQL 新增了 import 语法，现在可以导入另外一个 QL 查询作为 udf 导入到当前 QL 中了。
    - @ValidBy注解新增boolean类型属性inherited控制注解是否被继承。
    - 新增doValidation验证器验证策略ValidStrategy，用于控制是否执行后面的验证器。
    - RSF-Center 的内存数据存储器新增了垃圾数据扫描处理机制。
**改进**
    - Spring 插件中的 sechma 升级为 3.2.2。同时 rsf 的 sechma 和 hasor-core 合并成一个。
    - DataQL 的 LoaderUdfSource 增加 isIgnore 方法用于判断是否忽略不正确的UDF查找请求
    - DataQL 优化udf source增加机制。
    - HASOR_LOAD_EVENT_POOL 配置项从 20 改为 8
    - RSF 网络层，抽象 Connector 概念，作为 RPC 连接器存在。有了连接器扩展任意 rpc 协议变得可能。
    - 提供一个 netty 基础连接器实现，rsf 协议基于 Netty 连接器 实现。
    - 提供一个 http 基础连接器实现， hprose 协议基于 http 连接器实现。
    - RSF RequestInfo 和 ResponseInfo 不在同时封装 byte 和 object。
    - 序列化和反序列化都交给 io 线程进行处理。
    - Hprose 协议，支持双向调用了。之前只能被动充当 Hprose 服务提供者，目前也可以作为消费者存在了。
    - Rsf 注册重新完全重新实现。
    - Web框架中 @MappingTo 支持配置多个地址。
    - @Transactional 注解可以标记在：方法、类 上。
**修复**
    - fix Hasor 的 Spring 插件初始化失败问题。
    - fix jfinal 列子编译问题。


Hasor-Core v3.2.1 (2017-10-17)
------------------------------------
**新增**
    - DataQL，执行引擎新增 jsr223 兼容。从这个版本开始可以使用 jsr223 的方式使用 DataQL 了。
    - DataQL UDF 新增 manager 接口方便管理和注册。
**改进**
    - 优化 dataQL 函数注册更加方便。
    - 优化 ApiBinder 在 toString 时的提示信息。
    - plugin 项目的插件依赖改为弱依赖。
**修复**
    - 修复 dataQL 表达式计算时 == 判断出现异常的问题。
    - fix plugins 插件中配置文件错乱的问题。
    - fix RsfWebModule 已经删除但是配置依然存在的问题。


Hasor-Core v3.2.0 (2017-10-15)
------------------------------------
**新增**
    - 新增内置 Freemarker 渲染器，如想使用该渲染引擎开发者还需要额外依赖 freemarker 的 jar 包。
    - 新增内置 DataQL，服务查询引擎，全面提供 “数据库 + 服务” 整合查询，并为查询结果提供全面的数据整合能力。
    - 新增内置 Json 渲染器，JSON 渲染引擎会按照下面顺序尝试寻找可用的json库：fastjson、Gson
    - 内置JSON渲染引擎，可以通过 apiBinder.bind(JsonRenderEngine.class) 方式绕过内部查找机制直接使用用户自定义的json渲染器。
**改进**
    - EventContext 接口增加 fireSyncEventWithEspecial 方法，可以用于指定同步事件是否以独立线程运行。
    - ContextClassLoaderLocal 类移动位置。
    - 已有 DBModule 删除，功能被整合到 DataApiBinder 接口中。
**修复**
    - 修复当 aop 类中出现静态代码块，静态方法时。生成了错误的动态字节码。
    - Fix @Produces 注解工作时的一些问题。


Hasor-Core v3.1.3 (2017-02-23)
------------------------------------
**改进**
    - 事件管理器增添一个字符串参数的构造方法参数，用来确定执行事件的线程名称。
**修复**
    - 修复 AppContext接口 getBindIDs、getNames 两个方法返回值为空的问题。


Hasor-Core v3.1.2 (2017-02-19)
------------------------------------
**新增**
    - Hasor类在处理用户设置的环境参数时，设定为两种分类：框架环境变量、用户环境变量。
    - 在框架层面：无论是否设置框架层面的环境变量参数，Hasor都会将继续尝试执行加载 env.config。
    - 用户层面：如果配置了用户层面的环境参数，那么Hasor将放弃加载 env.config。
    - db框架新增 BeanSqlParameterSource 支持 Bean 类型的 SqlParameterSource。
**改进**
    - render 框架以及RenderApiBinder接口功能融入 hasor-web 框架。
    - 提升表单验证功能开发体验，表单验证接口 net.hasor.web.valid.ValidErrors 合并到 net.hasor.web.valid.ValidInvoker 接口。
    - HASOR_RESTFUL_LAYOUT环境变量默认值从 true 改为 false。站点文件布局本身是一个极具个性色彩的功能，不应该强行加给开发者。


Hasor-Core v3.1.1 (2017-02-16)
------------------------------------
**修复**
    - 当Hasor通过 Hasor.create 创建容器之后，如果开发者设置了环境参数。那么Hasor将放弃加载 env.config。


Hasor-Core v3.1.0 (2017-02-15)
------------------------------------
**改进**
    - 当依赖注入遇到父子类重名字段引发，duplicate异常时候，打印出冲突的字段名。
    - 环境变量名不区分大小写。
    - AbstractEnvironment，改进“env.config”配置文件的加载改为：先在WORK_HOME下查找，找不到在加载classpath下的。
    - 启动日志中，打印出“env.config”中所加载的所有信息。
    - 原 org.more 包内的工具仅保留使用到的工具类，同时移动到net.hasor.core包中，瘦身约三分之一。
    - classcode 成为 Hasor 的一部分。
    - 增加 debug 模式，debug 模式下会保存 动态代理生成的字节码文件。
    - 删除不常用的 Event插件，由于再有没有任何内置插件在启动时扫描类，因此 Hasor 启动速度飞快。
**修复**
    - fix 当在 jdk8 下使用 hasor aop 功能时出现 VerifyError 错误的问题，3.1.0版本开始不在需要通过 -noverify 参数压制异常。


Hasor-Core v3.0.3 (2017-02-07)
------------------------------------
**修复**
    - Fix ClassEngine 类在判断 @AopIgnore 时，潜在的一个空指针 bug。该问题会导致启动失败。


Hasor-Core v3.0.2 (2017-01-30)
------------------------------------
**新增**
    - 新增 ProviderType 接口，用于确定 Provider 接口的返回值类型。
    - MappingToBuilder 新增 3 个 findBindType 方法用于确定 bindType 类型。
**修复**
    - Fix RuntimeFilter入口类，当没有配置 request/ressponse 编码时引发的异常。


Hasor-Core v3.0.1 (2017-01-29)
------------------------------------
**修复**
    - Fix DefaultXmlNode在执行配置替换时，属性没有被替换的问题。
**改进**
    - 删除了 LogUtils 小工具。


Hasor-Core v3.0.0 (2017-01-12)
------------------------------------
**新增**
    - 新增 ApiBinder 扩展机制。开发者可以通过 net.hasor.core.binder.ApiBinderCreater 接口可以自定义 ApiBinder。
    - WebApiBinder 新增可以设置(请求/响应)编码方法。
    - 通过 ContainerCreater 可以扩展 Hasor 的上帝类了。
    - 新增 @AopIgnore 注解，用于忽略Hasor的Aop动态代理功能。当标记到包上时表示整个包都忽略动态代理。该功能可以有效的防止泛滥的全局Aop。
    - 新增 ApiBinder 接口新增 tryCase 方法用于将 ApiBinder 转换为支持的另外一种接口。
    - 新增 InvokerFilter 接口，该接口功能等同于 Filter。
    - 新增 Invoker 接口取代之前的 RenderData 接口，同时 Invoker 可以像 ApiBinder 一样支持扩展。
    - 新增 MappingSetup 接口，当发现一个控制器时会通过该接口通知给开发者。
    - 新增 WebPlugin 接口，用来扩展过滤器链的开始调用，和调用结束。
**改进**
    - 受益于 ApiBinder 扩展机制，WebEnvironment、WebAppContext、WebHasor 都不在需要。
    - Web 框架通过 ApiBinder扩展机制融入 AppContext，不在需要 AppContext 的定制化。
    - restful 框架和 web 框架。在功能不变的前提下全面融合，代码重构接近90%。
    - 2.4.4版本中添加的 web-fragment 特性不在支持，原因很容易引起重复配置。
**修复**
    - Fix HasorUnitRunner 在 JUnit 4.12 版本上 computeTestMethods 方法出现异常的问题。
