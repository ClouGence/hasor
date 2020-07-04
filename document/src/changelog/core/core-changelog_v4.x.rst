--------------------
Release Hasor v4.x
--------------------
Hasor v4.1.10 (2020-07-?)
------------------------------------
**新增**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1IZ16>`_ 增加 LookupDataSourceListener 扩展接口，允许每次执行 DataQL 的 SQL 片段时都动态的查找 DataSource 以实现动态数据源。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1M4FY>`_ CollectionUdfSource 函数库需要一个 size 函数
    - collect 函数库新增 size、newMap、groupBy 三个函数。
**优化**
    - 函数库调整：已有 hex 函数库合入 convert 函数库；已有 compare 函数库合入 string 函数库；已有 hmac 函数库合入 codec 函数库；
    - 多个 Notify SPI 下，必须要有仲裁。
    - AppContext 接口的 joinSignal 方法废弃。增加一个 waitSignal 方法（可以利用对象锁的形式进行等待）
    - `issue <https://gitee.com/zycgit/hasor/issues/I1KFAZ>`_ 优化 DomainHelper 增加对 UUID 类型的默认支持，将其转换为 String
**修复**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1M4CH>`_ 带有 content-path 的工程生成的 Swagger 文档中 BaseURL少了一级路径
    - `issue <https://gitee.com/zycgit/hasor/issues/I1M4FS>`_ ResultStructure 取消勾选之后，在刷新又显示被勾选了
    - `issue <https://gitee.com/zycgit/hasor/issues/I1MM4L>`_ 4.1.9版本获取header参数Bug

Hasor v4.1.9 (2020-06-29)
------------------------------------
**新增**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1IT82>`_ Api列表时显式其http method
    - `issue <https://gitee.com/zycgit/hasor/issues/I1J2BJ>`_ Dataway string 库函数新增 split 方法。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1JA0Q>`_ UI 的操作提供权限控制扩展能力
    - 新版本检查提示功能，例如:官方在发布新版本的时。Interface-UI 会检测是否有新版本并弹窗提示给使用者，使用者可以选择永久忽略这次版本提示。
    - SPI 增加可以判断 hasSpi、hasJudge 方法用来判断 SPI 是否有被注册。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1EQCS>`_ 异常信息增加了行号。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1GZZM>`_ 新增一个开源，Parameters中结构化的参数可以包装为一个整体。
    - hasor-web 项目新增 HttpParameters 工具类。可以静态方法形式获取各类请求参数。
**优化**
    - 所有编辑器统一使用 MonacoEditor，去掉 Codemirror 依赖。
    - ApiInfo 类型中增加 CallSource 枚举，用来表示请求的来源。isPerform 方法成为过期方法。
    - ValueModel 的isByte、isShort、isInt、isLong 等等方法。逻辑调整为是否可以转换为该类型。
    - 去掉了 DataQL 的环境隔离能力。
    - FxWebInterceptor 对于 header、cookie 的获取通过 HttpParameters 类来获取。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1LC53>`_ 整合Swagger 之后 Header 无法通过 Swagger 传入。
**修复**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1J7K3>`_ 修复Dataway 生成的 Swagger api文档，在容器中用 swagger 访问失败。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1K85T>`_ 修复 字符串加法计算特定场景下抛异常。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1K1MJ>`_ 修复 @@mybatis 多线程并发问题 MybatisFragment
    - `issue <https://gitee.com/zycgit/hasor/issues/I1J33N>`_ 跨域下 4.1.7、4.1.8 前端请求第一次是options时会报错。

Hasor v4.1.8 (2020-05-28)
------------------------------------
**新增**
    - `issue <https://github.com/zycgit/hasor/issues/47>`_ Pre 拦截器中建议增加 hint 的设置。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1GG0O>`_ 合并来自社区的 Api 分组功能，分组是通过 Api 的 path 切分为 Tree 形式。
    - `PR <https://gitee.com/zycgit/hasor/pulls/8>`_ 合并来自社区的 @@mybatis 的能力，通过 @@mybatis 可以给 SQL 增强动态SQL能力。
    - ApiBinder 新增一组 loadSpiListener 方法，用来简化 Spi 注册的使用。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1ES3K>`_ 加入 Swagger2 的支持
**优化**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1HS58>`_ 4.1.7 执行之后 Result 为 "empty.",较低版本浏览器兼容问题Bob的方法不支持 data() 导致
    - DataqL 的 Maven 插件对 Maven 的依赖从 3.3.9 降低到 3.3.3。
    - UI 发起的请求，Server 端统一使用 UTF-8 编码形式将数据转换为二进制，同时浏览器端统一采用 Blob 方式接受二进制数据在通过 decodeURIComponent 函数来解码UTF-8数据。
    - GET 模式下，UI 发起的请求会将请求参数全部转化为 String。因为 GET 获取参数在服务器端拿到的也都是 String。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1I9XW>`_ 处理api跨域部分头信息失败。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1HQPF>`_ 优化配置接口 /interface-ui/ 页面加载大js导致加载速度缓慢。对静态资源增加了 1个月对缓存。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1HW0C>`_ 增加一个 Hint 用来可以设定查询分页时 currentPage 的默认启始数字
**修复**
    - 修复当内部 UI 请求报错之后，错误被吃掉没有显示出来的问题。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1HX3O>`_ 修复 `HASOR_DATAQL_DATAWAY_FORCE_DBTYPE` 配置项在 4.1.7 中失效的问题。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1IB09>`_ 修复 Dataway 4.1.7中的ApiHistoryGet.ql参数传错了的问题。

Hasor v4.1.7 (2020-05-18)
------------------------------------
**新增**
    - `issue <https://github.com/zycgit/hasor/issues/14>`_ 新增 Microsoft SQL Server 的支持
    - `issue <https://gitee.com/zycgit/hasor/issues/I1GDPD>`_ 新增 PG 的支持
    - `issue <https://github.com/zycgit/hasor/issues/41>`_ 支持返回结果的完全自定义序列化，甚至可以自定义二进制专有协议。
**修复**
    - `issue <https://github.com/zycgit/hasor/issues/45>`_ Oracle 下 ReleaseDetail.ql 脚本中的查询语句缺少一个 where

Hasor v4.1.6 (2020-05-10)
------------------------------------
**修复**
    - `issue <https://github.com/zycgit/hasor/issues/36>`_ oracle 分页模式下 select count 语句无法正确执行。

Hasor v4.1.5 (2020-05-09)
------------------------------------
**新增**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1FGQO>`_ 接口可以自定义返回值，完美兼容老项目的接口规范。
    - `issue <https://github.com/zycgit/hasor/issues/32>`_  增加 ConvertUdfSource 类型转换函数包。
**优化**
    - FxSql 工具类抽象出 FxQuery 接口 和 SqlFxQuery 实现类。
    - `issue <https://github.com/zycgit/hasor/issues/30>`_ 优化了一下交互，新建接口情况下 comment 不会主动显示出来。
**修复**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1G6QS>`_ DatawayService 接口使用 @Bean 在 Spring 中配置报错。
    - `issue <https://github.com/zycgit/hasor/issues/29>`_ SQL模式下保存api之后，点击编辑再进入，没有显示之前保存的信息。
    - `issue <https://github.com/zycgit/hasor/issues/31>`_ 分页模式下，FRAGMENT_SQL_COLUMN_CASE 不起作用。

Hasor v4.1.4 (2020-04-30)
------------------------------------
**新增**
    - 接口可以跨域访问。
    - Dataway 增加 CompilerSpiListener 扩展点，可以自定义 DataQL 编译过程。
    - Dataway 增加 PreExecuteChainSpi 扩展点，可以在 DataQL 执行之前进行干预。配合 ResultProcessChainSpi 可以实现缓存和权限。
    - Dataway 增加 ResultProcessChainSpi 扩展点，可以对DataQL执行的结果进行二次处理。
    - hasor-spring 做整合的时，Hasor-web可以工作在 Filter模式下也可以工作在 SpringWebMVC 拦截器模式下
    - Dataway 新增 DatawayService 界面配置的接口可以在本地应用上用代码发起调用了。
    - `issue <https://github.com/zycgit/hasor/issues/13>`_ Dataway 支持配置多个数据源。但一个 DataQL 查询中目前依然只能使用一种数据源。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1F0ZB>`_ Dataway 新增 Oracle 的支持。
    - 新增 FRAGMENT_SQL_COLUMN_CASE 选项，可以决定 SQL 执行器的返回结果 key 策略，是全部大写还是全部小写或者满足驼峰。
    - 新增 mapKeyToLowerCase、mapKeyToUpperCase、mapKeyToHumpCase 三个函数，对 Map 的 Key 做转换
**优化**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1EUAL>`_ 改进 Dataway 在处理 GET 请求时，多个同名参数获取的问题。之前只能拿到数组形态，在于 POST 模式进行对比的时容易产生奇异造成认为是 Bug 的假象。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1DK6R>`_ hasor-dataql-fx 项目中 ognl 内嵌到 jar包中，减少两个外部依赖 jar。
    - SpiInterceptor 机制有些说不清，改为 SpiJudge（仲裁机制：SPI 仲裁：当同一个 SPI bind 了多个监听器时，仲裁可以决定哪些 SPI 会被调用）
    - hasor-web 支持路径中出现多个连续 / ，例如： ``http://127.0.0.1:8080/app/////interface-ui/#/new``。连续的 / 会被折叠成一个。
    - Dataway UI 界面中模式切换会因为 // 但行注释问题产生一些不友好的用户体验。现改成 /**/ 多行注释方式。
**修复**
    - `issue <https://gitee.com/zycgit/hasor/issues/I1EM2V>`_ Dateway 4.1.3 版本资源文件缺失问题。
    - `issue <https://gitee.com/zycgit/hasor/issues/I1FD95>`_ Dataway 修复 spring boot context_path 不支持的问题。
    - Dataway 当关闭 UI 功能之后接口调用报 NPE 问题。Bug 原因是 Dataway 内置 DataQL 的环境是一个隔离环境，隔离环境的初始化是在 UI 之后。
    - 修复 SqlFragment 单行注释判断不识别的问题。

Hasor v4.1.3 (2020-04-13)
------------------------------------
**新增**
    - 新增 Dataway 框架
    - dataway 通过数据库探测机制来实现确定 SQL 执行方案。
    - DataQL 增加可以构建多个独立的环境。其中 dataway 使用独立的环境。
    - DataQL 新增 SQL 代码片段执行器，开启 DataQL + SQL 模式。支持分页模式，并兼容多种数据库。
    - CorsFilter web框架增加一个跨域的 Filter 工具类。
    - **DataQL-fx** DataQL FragmentProcess 接口新增批量处理能力。
    - **DataQL-fx** 增加完成事物函数库，完整支持 7种事务传播属性。
    - **DataQL-fx** 增加 web 相关的 函数库
    - **DataQL-fx** 增加 加密解密 udf 工具。
**优化**
    - DataQL 语法解析器新增支持 标识符可以通过 `` 来囊括特殊字符例如：+、-、*、/ 等符号
    - DataQL QueryApiBinder 的 bindFinder 支持 Supplier了。
    - 修复 ApiBinderCreater 拼写错误 ApiBinderCreator。
    - 2.mapjoin 函数名改为 mapJoin。
**修复**
    - Hasor-web：InvokerSupplier，修复 npe 问题。

Hasor v4.1.2 (2020-03-04)
------------------------------------
**新增**
    - 新增 Hasor-Spring 项目，让 Spring 更方便的使用 Hasor 功能，例如：hasor-dataql、hasor-web。
    - Matchers 类，增加 anyClassInclude、anyClassExcludes 方法。
    - 新增 RenderType 注解，用来标记默认使用的是哪一个渲染器。
    - 新增 JsonRender 使用内置 JSON 工具(来源于jetty) 实现一个 json 渲染
**优化**
    - RenderEngine接口的initEngine方法删除。
    - Invoker接口增加contentType 方法。
    - 新增 ForwardTo、RedirectTo 两个注解用来处理返回值的 Forward和Redirect
    - hasor-dataql-fx-basic 具备自己独立的jar包名。
    - ApiBinderInvocationHandler 机制改变一下 为了兼容 ApiBinder 接口中调用 installModule方法。
    - rsf 改为默认不启动。
    - ResourceLoader 增加计算资源长度的方法。
    - dataql-codegen-template.tpl、QueryHelper.java 两个文件中增加非空判断。

Hasor v4.1.1 (2020-02-22)
------------------------------------
**新增**
    - 新增 DataQL Maven 插件，会根据 *.ql 文件生成对应的 Java 调用代码。
    - 添加 TypeSupplier 接口可以让 Hasor 有能力工作在其它 IoC 框架下。一个典型的场景就是与 Spring 整合。
    - DataQL：Finder 接口取消 Object findBean(String beanName)  方法
**优化**
    - UdfSourceAssembly 接口优化实现，getSupplier 改为返回自己。
    - UdfSourceAssembly 接口中：Object、UdfSource、UdfSourceAssembly 三个类型的方法不被默认列入。
    - bindSpiChainProcessor 方法更名为 bindSpiInterceptor 更为贴切其含义。
    - VarSupplier 接口删除使用 Supplier 替代。
    - SqlQueryFragment 当遇到返回数据仅一行时，将不在包裹 List 。
    - CollectionUdfSource evalJoinKey 方法兼容 NULL 值。
    - NumberDOP 在做二元计算时，兼顾了 POSITIVE_INFINITY、NaN、NEGATIVE_INFINITY 三种情况。
    - DO 指令增加了 除法修正 的前置处理
**修复**
    - all-in-one 包的传递依赖丢失问题修复。
**其它**
    - land 项目并入 rsf。
    - 内置ASM 升级到 7.3.1

Hasor v4.1.0 (2020-02-03)
------------------------------------
**Commons**
    - 修复 ResourcesUtils 和 ScanClassPath，IO 文件句柄泄露问题。
    - BasicFuture 的 callback，当没有实现CancellFutureCallback的时候时候，会触发failed。
    - DataQL 中的 InterBeanMap 更名为 BeanMap 移到 commons 中。
    - DB 中的 LinkedCaseInsensitiveMap 移到 commons 中。
**Core**
    - 改造 hasor.core 全面支持 JSR-330。
    - 全新的 SPI 能力。
    - 单测覆盖率达到 90%，修复若干潜在的问题。
    - 主 namespace 'http://project.hasor.net/hasor/schema/main' 统一改为 'http://www.hasor.net/sechma/main'
    - @InjectSettings 注解增加，命名空间支持。
    - 默认配置文件名 hasor-config.xml 改为 hconfig.xml、不在提供环境参数属性文件的机制。
    - 其它大量接口上和内部执行机制的优化
**Web**
    - 单测覆盖率达到 90%，修复若干潜在的问题。
    - hasor-env-properties 参数不在有效。
    - @Produces 注解行为变化为不在影响使用哪个渲染器，而是负责指明使用什么类型作为 response 的 ContentType。
    - j2ee Servlet 会被转换成 MappingTo 运行。j2ee Filter 会被转换成 InvokerFilter 运行。
    - 新增 OneConfig 汇总了FilterConfig, ServletConfig, InvokerConfig 三个接口的实现。
    - ListenerPipeline 不在需要，取而代之使用 SPI 机制来替代。
    - RenderInvoker 接口不在提供 lockViewType 相关方法。
    - MimeType 接口在获取 mimeType 信息时改为优先框架内的数据，如果框架内数据招不到在到 context 上查找。
**DB**
    - mybatis 插件回归 hasor-db
**tConsole**
    - 重构，对于多行输入支持用户自定义命令结符号or字符串。重构后单测覆盖率达到 90%。
    - 支持 server 模式通过 Socket 端口运行
    - 支持 基于标准输入输出流运行
    - hasor-boot 能力被完完全全整合，因此 Hasor 将不在提供 hasor-boot。
**DataQL**
    - 重构，放弃 javacc 更换成 antlr4。antlr4 更加智能。AST 模型仍然不变。重构后单测覆盖率达到 90%。
    - DataQL 大量新语法新特性。具体参看语法参考手册。一些老的语法形式也不在支持，因此 DataQL 的语法和以前有明显变化。
    - 运行时内存模型：确定为 两栈一堆
    - 指令集系统：不在需要 ASM、ASA、ASO 三个指令，取而代之的是更严谨的指令集。
    - SDK：函数包能力
    - DataModel数据模型：增加 unwrap 方法，用来解开 DataModel 包裹
    - 新增 Fragment 机制允许 DataQL 执行外部非 DataQL 语法的代码片段。
    - BeanContainer 改为 Finder，删掉 UdfSource、UdfManager、UdfResult 不在需要这些概念。
    - 原有 dql test case 语句文件统一转移到 _old 目录下面备用。
**RSF**
    - rsf 使用 tconsole 的新接口
    - 注册中心暂不可用，下几个版本会重新设计。
    - rsf 的 InterAddress 支持域名传入，但是toString 的时仍然会转换为 ip。
**其它**
    - 删除 Hasor 默认提供的 JFinal 插件支持。理由是 JFinal 功能和 Hasor 体系重叠，同时 Hasor 的所有功能都是独立。
    - 整合 Hasor 及其容易因此没有提供集成代码的必要。
    - 新增 Hasor-all 包。

Hasor v4.0.6 (2019-05-31)
------------------------------------
**改进**
    - getInstance、getProvider 新增 param 参数以支持构造方法入参。
    - tConsole 接口调整。

Hasor v4.0.5 (2019-05-27)
------------------------------------
**重要**
    - 4.0.0版本新增的 Hasor-Boot 项目不在单独存在，理由 Hasor 可以很好的在 Spring Boot 上运行和部署，因此并无任何必要在重复构建相同功能。
    - Boot 的机制融入到AppContext 接口的两个 join、joinSignal 新增方法中，不在单独设立 Hasor Boot 启动器。
    - 删除 @IgnoreParam 注解，@ParameterForm 注解更名为 @ParameterGroup。
**新增**
    - 新增 @Destroy 注解 @PreDestroy 注解支持，可以配置当容器停止时调用的方法。
    - binder 可以声明 Destroy 方法了，要想使用 Destroy 的Bean 必须是单例的。
    - Web请求中 ServletContext 可以作为特殊类型注入进来了。
    - AppContext 新增 join、joinSignal 两个方法。
**改进**
    - 标记了 @ParameterForm 的参数对象会执行 inject。
    - ApiBinder 的 installModule 支持数组入参了。
    - Hasor 类的工具方法拆分到 HasorUtils 中。
    - Hasor.assertIsNotNull 方法使用 Objects 相关的方法进行替代。

Hasor v4.0.4 (2019-05-22)
------------------------------------
**新增**
    - Environment 接口新增 getVariableNames、getVariable 两个方法方便获取环境变量。
**改进**
    - Environment 接口的 removeEnvVar 方法更名为 removeVariable
    - Environment 接口的 addEnvVar 方法更名为 addVariable
**修复**
    - 修复了 WebApiBinder 接口 loadRender 方法
    - 修复 RenderWebPlugin NPE 的问题。

Hasor v4.0.3 (2019-05-17)
------------------------------------
**修复**
    - 删除默认配置文件中 net.hasor.web.valid.ValidWebPlugin，插件的配置，该插件已经不存在但是遗留了一个配置导致启动报错。

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
