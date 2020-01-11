配置文件
------------------------------------
Hasor 支持两种格式的配置文件，这两种文件的内容都要求使用 UTF-8 编码

- Xml格式
- properties属性文件

.. HINT::
    关于配置文件的态度

    1. 拥抱约定优于配置，但是不鼓吹零配置。
    2. 提倡 Xml 结构化配置，同时也拥抱属性文件。


作为 Hasor 的 xml 配置文件最少内容如下：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        ...
    </config>


作为 Hasor 的 properties 配置文件最少内容是空：

.. code-block:: properties
    :linenos:

        ...


读取配置文件
------------------------------------
有如下配置文件，读取它的内容：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
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

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().mainSettingWith("simple-config.xml").build();
    Settings settings = appContext.getInstance(Settings.class);
    String driver = settings.getString("hasor-jdbc.dataSource.driver");
