Hasor-Core

介绍：
core---
	Hasor的核心软件包，几乎所有Hasor扩展模块都会依赖到它。包含工具和 Hasor 两个部分，它是整个Hasor蓝图的基础。
	该软件包提供了：配置文件解析、事件、容器、IoC/Aop等核心功能。
db---
	Hasor的数据库操作框架，该框架主要目的是为Hasor提供关系型数据库访问功能。最新版本：0.0.3。
	前身是Hasor-JDBC项目，该项目中包含了“Transaction”、“JDBC”、“DataSource”、“ORM”四个部分。
	这四个组建又互相成为一个体系。整个Hasor-DB，可以被独立使用。其中“Transaction”、“JDBC”两个重要部件
	参考了SpringJDB做的设计，可以说Hasor-DB是缩小版的SpringJDBC。拥有SpringJDBC绝大部分功能。
web---
	Hasor-Web 是参照 guice-servlet进行的仿制，其中Hasor在仿制过程中做了大量改进优化。
	这使得Hasor-Web具有了更多很多优越的特性。同时它也是Hasor作为Web上开发框架的一个基石。
	几乎后续所有的Web模块都会依赖到它。开发者使用它可以通过编码形式动态注册 Servlet/Filter。
	Hasor-Web 为它们建立了统一的 Dispatcher入口。

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


DataSource：
	  DS 是一个简易的数据源管理工具，它位于“net.hasor.db.datasource”软件包。开发者可以通过
	“DataSourceUtils”工具类静态的获取和释放数据库连接。DataSourceHelper是它的核心接口。
	它的工作机制是为每个线程根据数据源绑定唯一的数据库连接。它内部通过引用计数来保证在释放连接的真正时机。
	Transaction组件就是通过它管理数据库事务连接的。

JDBC：
	  JDBC操作封装，这套软件包可以独立使用。通过它可以简化针对JDBC接口的使用。该接口原型是SpringJDBC
	  你可以简单的理解为它就是轻量化的SpringJDBC框架。

Transaction：
	  Hasor提供的操作数据库事务的接口，提供了7个不同的事务隔离级别。其实现思想来源于Spring。

Valid 验证机制：
	  AR模式的数据库操作接口，基于JDBC。	