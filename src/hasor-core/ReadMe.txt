Hasor-Core v0.0.1.20130831-M1
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


Hasor-Core v0.0.1.Release
    01.)Hasor-Core：80%以上代码重构，重构主要涉及内容的是结构性重构。重构之后将会使Hasor核心层的逻辑更加清晰，更便于扩展核心层功能。
        1.InitContext接口功能合并到Environment接口中。
        2.ApiBinder接口增加模块依赖管理。
        3.HasorModule接口更名为Module。
        4.HasorEventListener接口更名为EventListener。
        5.XmlProperty接口更名为XmlNode。
        6.config-mapping.properties属性文件的解析不在是必须的。
        7.重构Settings实现。Xml解析方式不在依赖ns.prop属性文件，实现方式改为Sax。
        8.@Module注解，更名为@AnnoModule。
        9.增加@GuiceModule注解，可以标记在com.google.inject.Module接口上，可以将Guice模块引入到Hasor中。
        A.重构AppContext实现。
        B.包空间整理，所有包都被移动到net.hasor下，整理License文件。删除残余的、无用的类。
        C.删除所有与Web相关的支持，这部分功能全部移动到Hasor-Web（Hasor-MVC更名而来）。
        D.生命周期：合并onReady和onInit两个生命周期阶段方法，删除销毁过程。
    02.)工具包修订：
        1.ResourcesUtils工具类中，类扫描代码优化。
        2.DecSequenceMap.java、DecStackMap.java两个类文件增加一些有用的方法。
    03.)所有Demo程序都汇总到demo-project项目中。


Hasor-Core v0.0.2.Release
    1.修改：DefaultXmlProperty类更名为DefaultXmlNode，并且XmlNode增加几个常用方法。
	2.修改：删除所有Mapping部分支持，相关代码移到demo作为例子程序。
    3.修改：AbstractAppContext类中有关事件的声明移动到 AppContext 接口中。
    4.修改：@Before 更名为 @Aop，性能进行了优化。
    5.升级：ASM升级为4.0、ClassCode连带升级。
    6.增加：以模块类名为事件名，当执行 Init\Start\Stop时候，抛出对应事件。
    7.增加：增加 Gift 体系用于扩展非模块类小工具。
    8.修复：StandardAppContext调用无参构造方法引发异常的问题，同时修改几个核心类的构造方法。



