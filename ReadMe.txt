Hasor-0.0.1.20130831-M1
    Hasor-Core:
        01.)基于COC原则。基本的开发不需要任何配置。
        02.)支持模块生命周期调度；支持模块依赖循环检测；启动时提示模块依赖树。
        03.)支持模块三种依赖规则（强依赖/弱依赖/依赖反制）。
        04.)容器级别提供事件API支持（同步事件/异步事件）。
        05.)JSR-330依赖注入标准支持，可以通过@Bean注解将Bean交由容器管理。
        06.)Binder：在模块中可以使用Guice相关接口
        07.)@Before注解化Aop开发。
        08.)不需要开发Xml解析器就可以解析自定义Xml配置文件。
        09.)通过注册配置文件监听器，当配置文件改变时通知业务程序动态更新配置。
        10.)可以用在任何Java类型项目上，没有运行环境要求。
        11.)松散式设计，可以与任何其他框架整合开发。
    Web部分：
        12.)J2EE各组件的注解化开发(@Filter/@HttpServlet/@WebSessionListener/@WebContextListener)

    Hasor-MVC：
        13.)WebMVC开发框架
        14.)Action拦截器
        15.)RESTful使用接口简单，JSR-311标准的部分实现。
        16.)支持从（Zip\Jar\ClassPath\任意目录）中加载Web静态资源文件，为资源文件管理提供支持。
        17.)支持JSON格式数据响应。
        18.)自定义Action返回值处理器。


Hasor-0.0.2.<...>-M2
    调整：
        01.)ResourcesUtils工具类中，类扫描代码优化。
        02.)@Module注解，更名为@DefineModule。注解可以标记在com.google.inject.Module接口上以定义一个Guice模块。
        03.)所有核心接口都经过了重构。
        	1.InitContext接口功能合并到Environment接口中
        	2.ApiBinder接口增加模块依赖管理同时合并onReady阶段和onInit阶段。
        	3.HasorEventListener接口更名为EventListener。
        	4.XmlProperty接口更名为XmlNode。
		05.)config-mapping.properties属性文件的解析不在是必须的。
        06.)DecSequenceMap.java、DecStackMap.java两个类文件增加一些有用的方法。
        07.)重构Settings实现：目前提供InputStreamSettings、FileSettings、StandardContextSettings、MappingInitContextSettings可用的类。
        	Xml解析方式不在依赖ns.prop属性文件，实现方式改为Sax。如需要解析config-mapping.properties属性文件，请使用MappingInitContextSettings类。
        08.)InitContext接口功能合并到Environment接口中，目前提供SimpleEnvironment、FileEnvironment、StandardEnvironment、MappingEnvironment类可用。
        	如需要解析config-mapping.properties属性文件，请使用MappingEnvironment类。
        09.)重构AppContext实现：目前提供DefaultAppContext、FileAppContext、StandardAppContext、MappingAppContext可用的类。
        	如需要解析config-mapping.properties属性文件，请使用MappingEnvironment类。
        10.)包空间整理，所有包都被移动到net.hasor下，整理License文件。删除残余的类，无用的类。
        11.)所有Demo程序都汇总到demo-project项目中。



2.ApiBinder增加方法
        09.)Hasor-Core中删除所有与Web相关的支持，这部分功能全部移动到Hasor-Web（Hasor-MVC更名而来）。
        
        
        04.)LifeCycle枚举中定义的阶段事件转而在DefaultAppContext类中定义。

    新增：
        01.)增加jetty-all项目，该项目包含了jetty-8.1.12中所有代码。并且支持jsp，使用jetty-all可以简化Web开发环境。
        02.)支持使用@FmTag注解声明一个自定义标签(Tag/Tag2)，模板引擎使用Freemarker。
        03.)支持使用@FmMethod注解声明一个Freemarker，自定义函数。注解可以放置在任意方法上。
        04.)支持自定义TemplateLoader
        05.)支持Web环境下使用Freemarker作为模板显示引擎。
        06.)支持通过配置文件设置模板路径为(ClassPath/Zip/Jar/任意目录)中的任意一个或组合配置。






