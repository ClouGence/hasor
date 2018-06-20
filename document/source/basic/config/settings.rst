Hasor的配置
------------------------------------
Hasor 支持两种格式的配置文件，这两种文件的内容都要求使用 UTF-8 编码格式。

- Xml格式
- properties属性文件

.. HINT::

    关于配置文件的态度

    1. 拥抱约定优于配置，但是不鼓吹零配置。
    2. 提倡 Xml 结构化配置，同时也拥抱属性文件。
    3. 提倡 配置文件也模块化，同时也不放弃集中化配置。


关于“零配置”：相信聪明的同学都会知道即便是您真的一点都不需要做任何配置。但当面对数据库连接字符串等动态参数时，配置文件依然是你的最佳技术方案。市面上曾流行的所谓零配置，只是相对框架而言。

一旦您坚持了零配置，那么我也相信您很可能正在拥抱注解化。从某种角度来讲注解化也是一种配置。所以 Hasor 不鼓吹零配置，而是通过默认配置的方式让您尽量避免配置。同时 Hasor 也是拥抱注解化的。

配置文件的分类
------------------------------------
** 主配置文件 **

对于一般应用程序开发而言，您只需要接触到 Hasor 中所谓的主配置文件。全文中一般没有特指情况下说的都是主配置文件，默认主配置文件使用的是 xml 格式，您可以更换为属性文件。Hasor 识别它们是采用文件的后缀名来加以区分。

主配置文件可以是任意合法的 xml 或者有效的属性文件，但通常主配置文件像如下这样来书写

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        ...
    </config>


** 静态配置 **

静态配置通常当您开发一款 Hasor 插件时才有可能接触到，静态配置的作用是告诉 Hasor 您的插件中使用到的默认配置信息值。定义一个静态配置需要在 classpath 下新建一个文件 “META-INF/hasor.schemas” 然后在文件中指明具体的配置文件。例如：

.. image:: http://files.hasor.net/uploader/20180620/031929/CC2_11EF_E377_5C43.png

静态配置的书写完全遵循主配置文件。


** 差别 **

静态配置和主配置文件的区别其实不大，不同点是 Hasor 在启动时会先扫描加载所有静态配置。然后在加载主配置文件。因此主配置文件中的配置信息享有优先权。如果您同一个配置同时出现在两个配置文件中，利用这个特性您就可以实现主配置文件优先生效

读取配置文件
------------------------------------
有如下配置文件，我们要读取它的内容：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <!-- Demo 项目源码所处包 -->
        <hasor debug="false">
            <loadPackages>net.test.project.*</loadPackages>
        </hasor>
        <hasor-jdbc>
            <!-- 名称为 localDB 的内存数据库，数据库引擎使用 HSQL -->
            <dataSource name="localDB" dsFactory="net.test.C3p0Factory">
                <driver>org.hsqldb.jdbcDriver</driver>
                <url>jdbc:hsqldb:mem:aname</url>
            </dataSource>
        </hasor-jdbc>
    </config>


- 如果想读取：`debug="false"` 对应的表达式为：`hasor.debug`
- 如果想读取：`net.test.project.*` 对应的表达式为：`hasor.loadPackages`
- 如果想读取：`org.hsqldb.jdbcDriver` 对应的表达式为：`hasor-jdbc.dataSource.driver`


最后在得到表达式之后，我们通过 `Settings` 接口就可以读取配置信息了：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext("simple-config.xml");
    Settings settings = appContext.getInstance(Settings.class);
    String driver = settings.getString("hasor-jdbc.dataSource.driver");

脱离 Hasor 框架使用
------------------------------------
您可以单独使用 `net.hasor.core.setting.InputStreamSettings` 类去加载您的 xml 文件，然后以 Hasor 的方式去处理它。
