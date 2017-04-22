# Summary

* [第一章 介绍](README.md)
  * [1. 约定优于配置](docs/chapter-01/about_coc.md)
  * [2. 相关连接](docs/chapter-01/resources.md)
  * [3. 历代版本](docs/chapter-01/changelog.md)
    * [3.x Version](docs/chapter-01/subject-01/changelog_v3.x.md)
    * [2.x Version](docs/chapter-01/subject-01/changelog_v2.x.md)
    * [1.x Version](docs/chapter-01/subject-01/changelog_v1.x.md)
    * [0.x Version](docs/chapter-01/subject-01/changelog_v0.x.md)
  * [4. 贡献](docs/chapter-01/contribution.md)
  * [5. 参与开发](docs/chapter-01/subject-02/cooperation.md)
    * [版本管理](docs/chapter-01/subject-02/cooperation.md)
    * [编码规范](docs/chapter-01/subject-02/cooperation.md)
    * [递交代码](docs/chapter-01/subject-02/cooperation.md)
  * [6. 感谢](docs/chapter-01/tks.md)
* [第二章 快速上手](docs/chapter-02/QuickStart.md)
  * [1. 配置Web工程](docs/chapter-02/ConfigWebApps.md)
  * [2. 接收Request请求](docs/chapter-02/ProseccRequest.md)
  * [3. 使用模板引擎](docs/chapter-02/UseTemplate.md)
  * [4. 基本IoC](docs/chapter-02/IoC.md)
  * [5. Aop拦截方法调用](docs/chapter-02/AopInterceptor.md)
  * [6. 读写数据库](docs/chapter-02/ReadDataBase.md)
  * [7. 数据库事务](docs/chapter-02/DataBaseTransaction.md)
  * [8. 配置文件](docs/chapter-02/ReadSettingsFile.md)
* [第三章 Hasor基础](docs/chapter-03/subject-01/CreateHasor.md)
  * [第一节 启动Hasor](docs/chapter-03/subject-01/CreateHasor.md)
    * [1. 创建容器](docs/chapter-03/subject-01/CreateHasor.md)
    * [2. 程序入口](docs/chapter-03/subject-01/AppEnterIn.md)
  * [第二节 生命周期](docs/chapter-03/subject-02/Overview.md)
    * [1. Init阶段](docs/chapter-03/subject-02/InitPhase.md)
    * [2. Start阶段](docs/chapter-03/subject-02/StartPhase.md)
    * [3. Shutdown阶段](docs/chapter-03/subject-02/ShutdownPhase.md)
  * [第三节 Module](docs/chapter-03/subject-03/Module.md)
    * [1. 依赖和组合](docs/chapter-03/subject-03/Dependency.md)
    * [2. 执行阶段](docs/chapter-03/subject-03/Lifecycle.md)
    * [3. 模块类型](docs/chapter-03/subject-03/ModuleTypes.md)
    * [4. 多工程](docs/chapter-03/subject-03/MultiProject.md)
  * [第四节 ApiBinder](docs/chapter-03/subject-04/KnowApiBinder.md)
    * [1. 类扫描](docs/chapter-03/subject-04/ScanClass.md)
    * [2. 扩展ApiBinder](docs/chapter-03/subject-04/ExtApiBinder.md)
    * [3. ApiBinder转换](docs/chapter-03/subject-04/ApiBinderConver.md)
  * [第五节 IoC](docs/chapter-03/subject-05/ioc.md)
    * [1. 类型注入](docs/chapter-03/subject-05/InjectType.md)
    * [2. 接口注入](docs/chapter-03/subject-05/InjectFaces.md)
    * [3. 名称注入](docs/chapter-03/subject-05/InjectName.md)
    * [4. ID注入](docs/chapter-03/subject-05/InjectID.md)
    * [5. 得到AppContext](docs/chapter-03/subject-05/GetAppContext.md)
    * [6. 注入配置](docs/chapter-03/subject-05/InjectSettings.md)
    * [7. 注入环境变量](docs/chapter-03/subject-05/InjectVars.md)
    * [8. 用代码配置注入](docs/chapter-03/subject-05/InjectCodes.md)
    * [9. 调用类初始化方法](docs/chapter-03/subject-05/InitMethodCall.md)
  * [第六节 Aop](docs/chapter-03/subject-06/aop.md)
    * [1. 使用Aop拦截器](docs/chapter-03/subject-06/AopInterceptor.md)
    * [2. 拦截器级别](docs/chapter-03/subject-06/LevelInterceptor.md)
    * [3. 拦截器链](docs/chapter-03/subject-06/InterceptorComplex.md)
    * [4. 拦截器的匹配器](docs/chapter-03/subject-06/InterceptorMatcher.md)
  * [第七节 作用域](docs/chapter-03/subject-07/Scope.md)
    * [1. 单例](docs/chapter-03/subject-07/Singleton.md)
    * [2. 原型](docs/chapter-03/subject-07/Prototype.md)
    * [3. 自定义作用域](docs/chapter-03/subject-07/CustomScope.md)
  * [第八节 事件](docs/chapter-03/subject-08/Event.md)
    * [1. 同步事件](docs/chapter-03/subject-08/SyncEvent.md)
    * [2. 异步事件](docs/chapter-03/subject-08/AsyncEvent.md)
    * [3. 只执行一次的监听器](docs/chapter-03/subject-08/OnesEvent.md)
    * [4. 事件链](docs/chapter-03/subject-08/EventChain.md)
    * [5. 异步事件线程池设置](docs/chapter-03/subject-08/EventSettins.md)
  * [第九节 配置文件](docs/chapter-03/subject-09/Settings.md)
    * [1. 文件格式](docs/chapter-03/subject-09/FileFormat.md)
    * [2. 读取配置文件](docs/chapter-03/subject-09/ReadSettings.md)
    * [3. static-config](docs/chapter-03/subject-09/StaticSettings.md)
    * [4. 命名空间](docs/chapter-03/subject-09/NameSpace.md)
    * [5. 加载顺序](docs/chapter-03/subject-09/LoadSequence.md)
    * [6. 解析Xml](docs/chapter-03/subject-09/ParserXml.md)
  * [第十节 环境变量](docs/chapter-03/subject-10/Environment.md)
    * [1. 使用环境变量](docs/chapter-03/subject-10/VarEnv.md)
    * [2. 模版化配置文件](docs/chapter-03/subject-10/TemplateSettings.md)
    * [3. env.config](docs/chapter-03/subject-10/EnvConfig.md)
* [第四章 Web开发](docs/chapter-04/Web.md)
  * [第一节 热身](docs/chapter-04/subject-01/Start.md)
    * [1. Web工程配置](docs/chapter-04/subject-01/Start.md)
    * [2. Hello Word](docs/chapter-04/subject-01/HelloWord.md)
  * [第二节 处理Web请求](docs/chapter-04/subject-02/WebController.md)
    * [1. 请求参数](docs/chapter-04/subject-02/ReqParam.md)
    * [2. Cookie](docs/chapter-04/subject-02/CookieParam.md)
    * [3. 请求头信息](docs/chapter-04/subject-02/HeaderParam.md)
    * [4. RESTful](docs/chapter-04/subject-02/RESTful.md)
    * [5. 拦截器](docs/chapter-04/subject-02/Interceptor.md)
  * [第三节 表单](docs/chapter-04/subject-03/AboutForm.md)
    * [1. Form](docs/chapter-04/subject-03/Form.md)
    * [2. 表单验证](docs/chapter-04/subject-03/Validation.md)
    * [3. 场景化验证](docs/chapter-04/subject-03/SceneValid.md)
  * [第四节 扩展性](docs/chapter-04/subject-04/InterceptorExt.md)
    * [1. 扩展拦截器](docs/chapter-04/subject-04/InterceptorExt.md)
    * [2. 获取请求处理器](docs/chapter-04/subject-04/MappingSetup.md)
  * [第五节 J2EE](docs/chapter-04/subject-05/J2EE.md)
    * [1. 使用 Servlet](docs/chapter-04/subject-05/Servlet.md)
    * [2. 使用 Filter](docs/chapter-04/subject-05/Filter.md)
    * [3. 使用 HttpSessionListener](docs/chapter-04/subject-05/HttpSessionListener.md)
    * [4. 使用 ServletContextListener](docs/chapter-04/subject-05/ServletContextListener.md)
  * [第六节 使用模板引擎（文档编写中...）](.md)
    * [1. Freemarker](.md)
    * [2. ...](.md)
  * [第七节 装饰器（文档编写中...）](.md)
    * [1. 工作原理](.md)
    * [2. 文件布局](.md)
    * [3. 使用装饰器](.md)
  * [第八节 文件上传（文档编写中...）](.md)
    * [1. 缓存设置](.md)
    * [2. 流式上传](.md)
* 第五章 数据库（文档编写中...）
  * 第一节 数据源
    * 设置数据源
    * 数据源代理
    * 多数据源
    * 使用连接池
  * 第二节 CURD
    * 增/删/改
    * 批量操作
    * 使用SQL查询
    * 参数化查询
    * 查询结果转为Bean
    * CallBack
    * 操作多个数据库
  * 第三节 事务控制
    * 数据库事务
    * 高级事务控制
* 第六章 GraphQL（文档编写中...）
* 第七章 插件集（文档编写中...）
    * JFinal
    * Nutz
    * Spring
* 第八章 构建生产环境（文档编写中...）
* 第九章 默认配置详解（文档编写中...）
* 第十章 API指引（文档编写中...）