### Hasor-Core v3.2.1 (2017-10-17)
**新增**
    01. DataQL，执行引擎新增 jsr223 兼容。从这个版本开始可以使用 jsr223 的方式使用 DataQL 了。
    02. DataQL UDF 新增 manager 接口方便管理和注册。
**改进**
    01. 优化 dataQL 函数注册更加方便。
    02. 优化 ApiBinder 在 toString 时的提示信息。
    03. plugin 项目的插件依赖改为弱依赖。
**修复**
    01. 修复 dataQL 表达式计算时 == 判断出现异常的问题。
    02. fix plugins 插件中配置文件错乱的问题。
    03. fix RsfWebModule 已经删除但是配置依然存在的问题。

### Hasor-Core v3.2.0 (2017-10-15)
**新增**
    01. 新增内置 Freemarker 渲染器，如想使用该渲染引擎开发者还需要额外依赖 freemarker 的 jar 包。
    02. 新增内置 DataQL，服务查询引擎，全面提供 “数据库 + 服务” 整合查询，并为查询结果提供全面的数据整合能力。
    03. 新增内置 Json 渲染器，JSON 渲染引擎会按照下面顺序尝试寻找可用的json库：fastjson、Gson
    04. 内置JSON渲染引擎，可以通过 apiBinder.bind(JsonRenderEngine.class) 方式绕过内部查找机制直接使用用户自定义的json渲染器。
**改进**
    01. EventContext 接口增加 fireSyncEventWithEspecial 方法，可以用于指定同步事件是否以独立线程运行。
    02. ContextClassLoaderLocal 类移动位置。
    03. 已有 DBModule 删除，功能被整合到 DataApiBinder 接口中。
**修复**
    01. 修复当 aop 类中出现静态代码块，静态方法时。生成了错误的动态字节码。
    02. Fix @Produces 注解工作时的一些问题。
---

### Hasor-Core v3.1.3 (2017-02-23)
**改进**
    01. 事件管理器增添一个字符串参数的构造方法参数，用来确定执行事件的线程名称。
**修复**
    01. 修复 AppContext接口 getBindIDs、getNames 两个方法返回值为空的问题。
---

### Hasor-Core v3.1.2 (2017-02-19)
**新增**
    01. Hasor类在处理用户设置的环境参数时，设定为两种分类：框架环境变量、用户环境变量。
    02. 在框架层面：无论是否设置框架层面的环境变量参数，Hasor都会将继续尝试执行加载 env.config。
    03. 用户层面：如果配置了用户层面的环境参数，那么Hasor将放弃加载 env.config。
    04. db框架新增 BeanSqlParameterSource 支持 Bean 类型的 SqlParameterSource。
**改进**
    01. render 框架以及RenderApiBinder接口功能融入 hasor-web 框架。
    02. 提升表单验证功能开发体验，表单验证接口 net.hasor.web.valid.ValidErrors 合并到 net.hasor.web.valid.ValidInvoker 接口。
    03. HASOR_RESTFUL_LAYOUT环境变量默认值从 true 改为 false。站点文件布局本身是一个极具个性色彩的功能，不应该强行加给开发者。
---

### Hasor-Core v3.1.1 (2017-02-16)
**修复**
    01. 当Hasor通过 Hasor.create 创建容器之后，如果开发者设置了环境参数。那么Hasor将放弃加载 env.config。
---

#### Hasor-Core v3.1.0 (2017-02-15)
**改进**
    01. 当依赖注入遇到父子类重名字段引发，duplicate异常时候，打印出冲突的字段名。
    02. 环境变量名不区分大小写。
    03. AbstractEnvironment，改进“env.config”配置文件的加载改为：先在WORK_HOME下查找，找不到在加载classpath下的。
    04. 启动日志中，打印出“env.config”中所加载的所有信息。
    05. 原 org.more 包内的工具仅保留使用到的工具类，同时移动到net.hasor.core包中，瘦身约三分之一。
    06. classcode 成为 Hasor 的一部分。
    07. 增加 debug 模式，debug 模式下会保存 动态代理生成的字节码文件。
    08. 删除不常用的 Event插件，由于再有没有任何内置插件在启动时扫描类，因此 Hasor 启动速度飞快。
**修复**
    01. fix 当在 jdk8 下使用 hasor aop 功能时出现 VerifyError 错误的问题，3.1.0版本开始不在需要通过 -noverify 参数压制异常。
---

### Hasor-Core v3.0.3 (2017-02-07)
**修复**
    01. Fix ClassEngine 类在判断 @AopIgnore 时，潜在的一个空指针 bug。该问题会导致启动失败。
---

### Hasor-Core v3.0.2 (2017-01-30)
**新增**
    01. 新增 ProviderType 接口，用于确定 Provider 接口的返回值类型。
    02. MappingToBuilder 新增 3 个 findBindType 方法用于确定 bindType 类型。
**修复**
    01. Fix RuntimeFilter入口类，当没有配置 request/ressponse 编码时引发的异常。
---

### Hasor-Core v3.0.1 (2017-01-29)
**修复**
    01. Fix DefaultXmlNode在执行配置替换时，属性没有被替换的问题。
**改进**
    01. 删除了 LogUtils 小工具。
---

### Hasor-Core v3.0.0 (2017-01-12)
**新增**
    01. 新增 ApiBinder 扩展机制。开发者可以通过 net.hasor.core.binder.ApiBinderCreater 接口可以自定义 ApiBinder。
    02. WebApiBinder 新增可以设置(请求/响应)编码方法。
    03. 通过 ContainerCreater 可以扩展 Hasor 的上帝类了。
    04. 新增 @AopIgnore 注解，用于忽略Hasor的Aop动态代理功能。当标记到包上时表示整个包都忽略动态代理。该功能可以有效的防止泛滥的全局Aop。
    05. 新增 ApiBinder 接口新增 tryCase 方法用于将 ApiBinder 转换为支持的另外一种接口。
    06. 新增 InvokerFilter 接口，该接口功能等同于 Filter。
    07. 新增 Invoker 接口取代之前的 RenderData 接口，同时 Invoker 可以像 ApiBinder 一样支持扩展。
    08. 新增 MappingSetup 接口，当发现一个控制器时会通过该接口通知给开发者。
    09. 新增 WebPlugin 接口，用来扩展过滤器链的开始调用，和调用结束。
**改进**
    01. 受益于 ApiBinder 扩展机制，WebEnvironment、WebAppContext、WebHasor 都不在需要。
    02. Web 框架通过 ApiBinder扩展机制融入 AppContext，不在需要 AppContext 的定制化。
    03. restful 框架和 web 框架。在功能不变的前提下全面融合，代码重构接近90%。
    04. 2.4.4版本中添加的 web-fragment 特性不在支持，原因很容易引起重复配置。
**修复**
    01. Fix HasorUnitRunner 在 JUnit 4.12 版本上 computeTestMethods 方法出现异常的问题。
---