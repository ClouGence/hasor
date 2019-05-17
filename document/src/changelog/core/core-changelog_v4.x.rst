--------------------
Release Hasor v3.x
--------------------
Hasor v4.0.3 (2019-05-?)
------------------------------------
    - xxxxx


Hasor v4.0.2 (2019-05-17)
------------------------------------
**修复**
    - fix 当@ParameterForm、@HeaderParameter 和其它注解组合使用时候，发现参数丢失问题。
**改进**
    - 优化验证框架。
    - 删除了 WebPlugin 机制。


Hasor v4.0.1 (2019-05-12)
------------------------------------
**Web**
    - BeanContainer 增加当配置新 Bean 发生异常时自动回滚新 Bean 的能力
    - @Render 注解功能从描述URL请求尾缀，变为描述渲染器名字，同时可以执行这个渲染器使用的特殊MimeType。
    - InMapping 接口删除
    - Invoker 接口新增 ownerMapping、fillForm 方法。
    - 对应TestCase 调整。
*Boot*
    - Boot Maven插件项目不在单独设立，而是只保留一个启动器，使用时建议用户结合 SpringBoot使用更加方便。


Hasor v4.0.0 (2019-05-09)
------------------------------------
**JDK8相关 **
    - 依赖JDK版本从 jdk6 升级到 jdk8，代码层面全面迁移到 jdk8 语法并深入结合 jdk8 相关特性。
    - net.hasor.core.Matcher 接口暂时保留，但已经不在使用，已替换成 java8 提供的 java.util.function.Predicate 接口替代。
    - net.hasor.core.Provider 接口暂时保留，但已经不在使用，已替换成 java8 提供的 java.util.function.Supplier 接口替代。
    - @Aop、@MappingTo、注解迎合JDK8特性可以同时标注多个。
**新成员**
    - 新增：hasor-boot 项目和配套的 hasor-boot mavenplugin。
    - 新增：tConsole 框架，提供一个 Telnet 环境支持，给予没有界面类的应用一个可以通过命令行进行交互的工具。
**static-config.xml 和 env.config**
    - 当使用 maven-shade-plugin 进行 maven 打包时由于 static-config.xml 无法通过文件追加的方式进行简单合并。
    - 因此老版本 Hasor 的工程无法使用 maven-shade-plugin 打包。
    - 3.3.0 版本之后修改了 static-config.xml 发现机制，改为通过 /META-INF/hasor.schemas 配置文件进行发现。
    - 改进之后，使用 maven-shade-plugin 的 hasor 工程可以像处理 spring.schemas 一样处理 hasor.schemas 合并。
    - 取消 env.config 属性文件机制。
**ApiBinder**
    - ApiBinder 接口支持 inject 一个 Class 类型。
    - ApiBinder 接口中新增 asEagerSingletonClear 方法，可以用来抹除 Bean 身上配置的 @Prototype 或者 @Singleton 行为。
    - 新增 MetaInfo 接口，AppContext、BindInfo 两个接口均继承自这个接口。从而提供除了 Context 之外的第二种途径绑定环境参数。
    - 增加一组 bindToCreater 方法用户绑定 BeanCreaterListener 到 BindInfo 上，BeanCreaterListener的作用是当创建 Bean 的时候会调用这个监听器。
**Settings**
    - @InjectSettings @Inject 注解支持标注在参数上了。
    - Settings 接口增添 removeSetting 方法，可以将整个配置项的多个值全部删除。
    - 使用 Hasor 设置 mainSettings 配置文件时可以指定 Reader 类型了。
    - Hasor 类增加支持设置 setMainSettings 为 Reader 或 InputStream
    - Hasor 类增加 addSettings 用来代码方式增添配置文件。
    - 配置项 “hasor.modules.loadErrorShow” 改名为 “hasor.modules.throwLoadError”
**EventContext**
    - EventContext 接口增加异步任务方法，从现在开始可以使用异步任务了。
    - EventContext 接口 fireSyncEventWithEspecial 更名为 fireSyncEventWithAlone
    - EventContext 接口 新增 clearListener 清空监听器能力。
**Environment**
    - StandardEnvironment 增添若干构造方法，AbstractEnvironment 调整输出日志内容。
    - Environment.addEnvVar 方法在添加 环境变量时如果 Value 为空或者空字符串，其行为相当于删除。
    - Environment 接口上的一些常量定义删除（例如：WORK_HOME）
**Web**
    - MappingSetup 接口，更名为 MappingDiscoverer，MappingData更名为Mapping
    - web RuntimeListener 新增：hasor-root-module、hasor-hconfig-name、hasor-env-properties 三个 web.xml 的属性配置。
    - @HttpMethod 注解可以加到 Method 上了。
    - WebApiBinder 接口中 scanAnnoRender 方法改为 loadRender。
    - InvokerFilter、InvokerChain 拥有返回值了。
**RSF**
    - 进行重构。
    - 使用 RSF_DATA_HOME 环境变量替代 RsfEnvironment.WORK_HOME。
    - rsf的内置 hessian 序列化和反序列化配置文件，路径改到 ‘META-INF/hasor-framework/rsf-hessian/’ 下面。以避免和 hessian jar包冲突。
    - rsf Gateway 从 rsf 基础框架中删除后面会独立成一个框架。
    - rsf 地址本保存时候不在保存空数据。
**JDBC**
    - fix 执行查询结果返回为空时，AbstractRowMapper.convertValueToRequiredType 方法报 NPE 的 Bug。
    - fix JdbcTemplate 类中 requiredSingleResult 当执行结果为空时报空指针的异常。
**Bean容器**
    - BeanBuilder 接口的三个 getInstance 方法改为 getProvider 方法。
    - 新增 @ConstructorBy 注解，可以在多个构造方法中指定一个作为创建 Bean 的入口。
    - fix 包扫描 AopIgnore 注解时，如果包里面没有任何类不加载 package-info.class 的问题。
    - AopIgnore 注解新增 ignore 属性，可以用于关闭注解功能（一般用不到）。
    - fix 了 Aop 的类不支持 double, long 两种基础类型参数的问题。
    - 字节码工具 ASM 升级到 7.0 版本
    - 新增 BeanCreaterListener 接口，该接口可以用来监听 Bean 的创建。通过 ApiBinder 中 whenCreate 相关方法来配置这个接口。
**改进和优化**
    - Hasor 类新增一组 asxxxSingleton 方法，用来设定 AppContext 的单例范围（静态、线程、ClassLoader）
    - asSmaller 时会设置 HASOR_LOAD_EXTERNALBINDER 、HASOR_LOAD_MODULE 为false，调用 asSmaller 之后不会加载任何 module 和 binder 扩展。同时任何位置的 mime.types.xml 也都不会加载
    - FutureCallback 的 cancelled 方法沉降到 CancellFutureCallback 接口中。
    - Class.forName 用法改进，普遍增加 ClassLoader 参数传入。
    - BeanUtils 类的 canWriteField，修复了对 Field 为 final 的判断。
    - 新增：utils resource loader 相关工具，来源为老版本 hasor 中的工具。
    - plugin 项目新增多种 freemarker 的 loader。
    - 增加单元测试，提升代码测试覆盖率。
