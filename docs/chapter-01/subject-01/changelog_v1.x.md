
### Hasor-Core v1.2.0 (2015-08-20)
**新增**
	01. 增加 @IgnoreParam注解，用于Form表单注入忽略。
	02. net.hasor.db.transaction.Manager新增回调方式执行事务的方法。
	03. 事务拦截器部分分离出独立的事务插件，不再属于db的一部分。
**修复**
	01. 修复BeanUtils在获取所有字段、所有方法时无法获取到父类中数据的问题。
**改进**
	01. Validation验证类传入的参数改为泛型，ValidationCallInterceptor增加了对泛型判断的逻辑。
	02. Valid验证框架分离出MVC成为一个独立的验证框架。
	03. 改进ResultDO相关Message方面的功能，使其用起来更爽。
---

### Hasor-Core v1.1.0 (2015-07-09)
**新增**
    01. 增加ContextMap类，该类可以为WebMVC提供request作用域范围的数据存储。可以通过AbstractWebController或者AppContext获取到它。
    02. mvc框架增加请求文件名尾缀匹配，默认配置：“htm;html;do;”，配置项为：“hasor.modConfig.mvc.interceptFiles”。
    03. Hasor增加autoAware方法用以冲抵ApiBinder中删除的autoAware方法。
**修复**
    01. 修复TemplateAppContext中在加载模块的时候，没有拦截住因为引入依赖而导致的报错。
    02. 修复mvc验证框架ValidData类中validMessage字段未初始化引发的空指针异常。
    03. 修复资源加载器插件，MultiResourceLoader类在向map插入值时插入空指针的问题。
**改进**
    01. ResourceLoader接口的exist方法不在抛出IO异常。
    02. AbstractWebController增加getModelByName方法。
    03. AppContextAware接口的使用不再依赖ApiBinder接口的声明，此项改进去掉了ApiBinder接口中的autoAware方法。
    04. TemplateAppContext 在 start过程中不再需要处理AppContextAware相关初始化工作。
    05. BindInfoProvider去掉构造方法ApiBinder类型参数，相关AppContextAware工作不再需要特殊声明。
---

### Hasor-Core v1.0.0 (2015-07-03)
**新增**
    01. 新增DateUtils，时间日期方面的工具类。
    02. 增添了一组getProvider方法方法，可以通过String或Class来获取Bean的Provider。
    03. AopMatchers类的expressionClass和expressionMethod方法开放使用。
    04. 合并Hasor-MVC框架
**mvc**
    05. 迁移 controller、result 插件。
    06. 支持 Action 返回值自定义处理。
    07. 新增验证机制，使用Validation接口。
    08. ResultProcess新增对异常的处理方法。
    09. 优化扩展机制的设计，进一步模块化设计。
    10. LoadHellper类的apiBinder方法，获取的ApiBinder类型改为返回WebApiBinder类型。
    11. loadController方法增加异常抛出。
    12. LoadHellper，增加注册表单验证器的方法。
**改进**
    01. Paginator 类中Order子类，提升为接口，原始的类实现通过SortFieldOrder提供。
        --此项更改有助于分页工具类应用到更广的场景下。
    02. 修改Result接口的addMessage方法，使其子类在重写的时候减少警告的发生。
    03. Hasor内部实现factory相关的简化重构。
    04. resource插件功能简单化，之前的插件是直接迁移自Hasor-WebUI项目。
**修复**
    01. 修复使用“apiBinder.bindType(PojoBean.class).asEagerSingleton()”方式声明单例失效的问题。
---