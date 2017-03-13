# Summary

* [第一章 介绍](README.md)
  * [1. 约定优于配置](docs/chapter-01/about_coc.md)
  * [2. 相关连接](docs/chapter-01/resources.md)
  * [3. 历代版本](docs/chapter-01/changelog.md)
    * [3.x Version](docs/chapter-01/changelog_v3.x.md)
    * [2.x Version](docs/chapter-01/changelog_v2.x.md)
    * [1.x Version](docs/chapter-01/changelog_v1.x.md)
    * [0.x Version](docs/chapter-01/changelog_v0.x.md)
  * [4. 贡献](docs/chapter-01/cooperation.md)
  * [5. 参与开发](docs/chapter-01/cooperation.md)
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
    * [3. 复合拦截器](docs/chapter-03/subject-06/InterceptorComplex.md)
    * [4. 拦截器的匹配器](docs/chapter-03/subject-06/InterceptorMatcher.md)
  * [第七节 作用域](docs/chapter-03/subject-07/Scope.md)
    * [1. 单例](docs/chapter-03/subject-07/Singleton.md)
    * [2. 原型](docs/chapter-03/subject-07/Prototype.md)
    * [3. 自定义作用域](docs/chapter-03/subject-07/CustomScope.md)
  * 第八节 事件（未完成）
    * 发送事件
    * 接收事件
    * 同步事件
    * 异步事件
    * 只会触发一次的事件
    * 异步事件线程池设置
    * [2. 深度解耦](chapter-03/subject-08/深度解耦.md)
  * 第九节 配置文件（未完成）
    * 读取配置文件
    * 配置规则
    * 静态配置文件
    * 加载顺序
    * [5. 命名空间](chapter-03/subject-09/多项目.md)
    * 配置文件解析机制
    * Xml解析
  * 第十节 环境变量（未完成）
    * 配置环境变量
    * 配置文件引用环境变量
    * env.config
    * 模版化配置文件
    * 实践多环境
* 第四章 Web开发（未完成）
  * Web工程配置
  * 处理Web请求
  * 请求拦截器
  * 扩展请求拦截器
  * 获取请求参数
  * RESTful开发
  * 表单和验证
  * 使用模版引擎
  * 网站文件布局
  * 文件上传
* 第五章 数据库（未完成）
  * 第一节 访问数据库（未完成）
    * 多数据源
    * 数据库操作
  * 第二节 事务控制（未完成）
    * 数据库事务
    * 高级事务控制
* 第六章 RPC（未完成）
  * 第二节 服务治理（未完成）
* 第七章 分布式一致性（未完成）
* 第十一章 FAQ（未完成）