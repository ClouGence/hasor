约定优于配置
------------------------------------
约定优于配置(Convention Over Configuration)是一个简单的概念。

系统、类库、框架应该假定合理的默认值，而非要求提供不必要的配置。
流行的框架如 Ruby on Rails2 和 EJB3 已经开始坚持这些原则，以对像原始的EJB 2.1规范那样的框架的配置复杂度做出反应。
一个约定优于配置的例子就像EJB3持久化，将一个特殊的Bean持久化，你所需要做的只是将这个类标注为@Entity。框架将会假定表名和列名是基于类名和属性名。
系统也提供了一些钩子，当有需要的时候你可以重写这些钩子。但是在大部分情况下，你会发现使用框架提供的默认值会让你的开发效率提升的更快。
或许您对执行效率有着其它看法,至少在 COC 下你可以减少大量不必要的配置。

Hasor不鼓吹“零配置”、“零注解”、“零Xml”，但是Hasor会把最简的开发体验作为首要准则，因此在使用Hasor开发项目时你会很少接触到配置。
如果您真的要进行深度定制，Hasor 的配置文件覆盖机制也会给您最大的灵活性。
但通常情况下使用约定俗成的方式就可以完美的运行。当然Hasor也允许您自己建立一套专有的约定标准，例如属于您自己特殊定制的注解系统。


引入依赖
------------------------------------
我们假定您的应用程序是基于 Maven 构建，接下来您在开始 Hasor 之旅的第一站就是将它的依赖引入到您的项目中。
截止到目前为止 Hasor 的最新版本为：**4.0.7**。

您也可以在这个连接中查找特定版本的Hasor，这个网站会提供给您各种依赖管理框架的引入配置。下面以 maven 为例作为展示。

- 支持：Maven、Gradle、SBT、Ivy、Grape、Leiningen、Buildr
- https://mvnrepository.com/artifact/net.hasor

.. code-block:: xml
    :linenos:

    <!-- 基础包 -->
    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-core</artifactId>
        <version>4.0.7</version>
    </dependency>

启动Hasor
------------------------------------
创建 Hasor 环境您只需要一行代码

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build(apiBinder -> {
        ...
    })

Web环境下可以参考下面这段代码：

.. code-block:: java
    :linenos:

    ServletContext sc = ;
    AppContext appContext = Hasor.create(sc).build((WebModule) apiBinder -> {
        ...
    });

如果需要加载配置文件那么如下：

.. code-block:: java
    :linenos:

    String setting = "hasor-config.xml"; // Hasor 会自动辨别配置文件的格式是 xml 还是 properties 文件
    AppContext appContext = Hasor.create().mainSettingWith(setting).build((WebModule) apiBinder -> {
        ...
    });

更多启动方式您可以探索 `Hasor` 类的其它方法。
