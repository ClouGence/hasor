Hasor-Core

介绍：
	Hasor的核心软件包，几乎所有Hasor扩展模块都会依赖到它。包含工具和 Hasor 两个部分，它是整个Hasor蓝图的基础。
	该软件包提供了：配置文件解析、事件、容器、IoC/Aop等核心功能。


IoC机制：
	  Hasor 的 Ioc 是通过递归的方式实现，在 0.0.10 版本中要想实依赖注入必须要实现 InjectMembers接口。
	注入的过程需要开发人员自己编写(将来版本 Hasor Ioc 将会考虑支持自动注入)

Aop机制：
	  Aop, Hasor 使用 MethodInterceptor 的接口处理 Aop 拦截器的逻辑。注册 Aop需要在初始化时进行。
	使用 ApiBinder 类的 bindInterceptor 方法即可完成 Aop 的注册。Hasor 通过 Matcher 接口完成
	Aop 的匹配操作，只有匹配成功的类、方法才会被冠以 Aop。
	  Aop 的实现采用的是动态字节码技术这一部分由 ASM 完成，并封装成独立的字节码工具 classcode。该部件代码
	位于“org.more.classcode”软件包下。是一个独立的工具，它可以脱离 Hasor 独立使用。

Bind机制：
	  Bind 参考了 Google Guice 的 Binder 接口设计，功能上大体相似。目的是提供一种不同于配置文件、注解方式
	的配置方法。这样一种设计并不是指 Hasor 抛弃配置文件和注解的优势，开发者可以根据项目的特征自行选择。
	  Hasor 的开发者可以将某一个类使用 ApiBinder 接口的 bindType 方法注册到容器中。这个工作与 Spring
	配置文件中 Bean 配置的作用并无不同。

Event机制：
	  Event 提供了一个简单的事件管理器。开发者可以通过 EventListener 接口编写事件处理程序。开发人员可以
	  事件注册、引发事件。Hasor 的事件机制支持 Sync、Async 两种触发机制，它们的区别在于引发事件之后事件的处理
	  方式上不同。对于异步事件可以通过 EventCallBackHook 接口收到事件执行过程中成功还是失败的信息。

Module模块：
	Hasor 是可扩展的，任何扩展功能都是通过 Module 接口进行，这是 Hasor 开发的主要入口。

Config配置文件解析：
	  Hasor 配置文件基于 XML 并以 stax 方式进行解析。尽管如此使用 Hasor 时候依然不需要编写任何配置文件，除非
	 您需要它保存一些数据，读取 XML 配置信息也极其简单。Hasor 通过 Settings 接口提供了各种类型数据返回。
	  Hasor 在读取 XML 的时候会将整个 XML 配置文件转换为 “element.element”这种形式的 K/V 集合方便开发读取。
	同时 Hasor 的配置文件还支持命名空间，这样一来相同配置的不同 Value 还可以被配置到不同命名空间以方便读取。它还可以
	做简单的 XML 解析成 DOM 的工作。	