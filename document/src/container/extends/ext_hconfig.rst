--------------------
配置文件 机制
--------------------
Hasor 目前支持（属性、Xml）两种格式的配置文件形式，依据不同场景合理选择。

**Properties**

 - 配置可以被完全 K/V 化，不含有层次结构
 - 配置都在同一个空间中

.. code-block:: text
    :linenos:

    key1 = value1
    key2 = value2
    ...

**Xml**

 - K/V 不能完全满足配置需求
 - 需要在2个及以上的空间中进行配置
 - 需要预先定义环境变量

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
      ...
    </config>


模块化
------------------------------------
在 Hasor 中一个 java 项目可以含有多个工程，每个工程都是独立的 Jar/War 包。而每个工程都可以定义自己的 hconfig 配置文件。

.. image:: ../_static/CC2_8633_6D5C_MK4L.png

这些分散在不同工程中的配置信息，将会在项目启动时重新汇聚在一起。这就是 Hasor 的配置文件模块化。

首先创建一个多工程的 Java 项目，然后在每个工程的 classpath 下新建 ``META-INF`` 目录。
然后在这个目录下创建 ``hasor.schemas`` 文件，以 hasor-core 自身为例，它的 hasor.schemas 内容为：

.. code-block:: text
    :linenos:

    /META-INF/hasor-framework/core-hconfig.xml

这是一个 hconfig 文件在 classpath 中的路径。在一个工程中可以有多个 hconfig 配置文件，只要这些配置文件按照每一行一个的形式写下去。

最后 Hasor 会在启动的时候都会加载它们，并且封装成 ``net.hasor.core.Settings`` 接口。


配置空间
------------------------------------
当配置文件被拆分为多个模块之后。由于组织协调原因有可能因为信息不对称而导致同一个配置被不同的 jar 包重复定义，但是各自含义却又不相同。

这就造成了 ``配置冲突`` 配置空间的存在就是为了解决这类问题，例如 A 框架定义了一个配置 ``serverLocal.url = www.126.com`` 写成Xml配置文件是：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <serverLocal url="www.126.com" />
    </config>


另外一个 B框架在不知道 A框架存在的情况下也定义了相同的配置信息，但是配置内容不同。这个时候两个jar包的配置文件就会产生冲突：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <serverLocal url="www.souhu.com" />
    </config>


解决冲突有两种方法： ``约定法``、``隔离法``

**约定法**

具体办法是，人为约定各自使用的配置前缀，并通过这个前缀加以隔离。例如：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <mod1_serverLocal url="www.126.com" />  <!-- 强制加 mod1 前缀 -->
    </config>

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <mod2_serverLocal url="www.souhu.com" /><!-- 强制加 mod2 前缀 -->
    </config>


最后在读取配置内容不需要明确配置的来源：

.. code-block:: xml
    :linenos:

    AppContext appContext = Hasor.create().build();
    Settings settings = appContext.getInstance(Settings.class);

    String url1 = settings.getString("mod1_serverLocal.url")
    String url2 = settings.getString("mod2_serverLocal.url")


**隔离法**

通过 xml 命名空间，把不同配置进行隔离。例如：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://mode1.myProject.net"><!-- Xml 命名空间隔离 -->
        <serverLocal url="www.126.com" />
    </config>

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://mode2.myProject.net"><!-- Xml 命名空间隔离 -->
        <serverLocal url="www.souhu.com" />
    </config>


最后，读取配置时需要明确读取的配置空间：

.. code-block:: xml
    :linenos:

    AppContext appContext = Hasor.create().build();
    Settings settings = appContext.getInstance(Settings.class);
    String url1 = settings.getSettings("http://mode1.myProject.net").getString("serverLocal.url");
    String url2 = settings.getSettings("http://mode2.myProject.net").getString("serverLocal.url");


针对上面多空间的例子，还可以借助 Xml 的命名空间机制来整合它们：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns:mod1="http://mode1.myProject.net"
            xmlns:mod2="http://mode2.myProject.net"
            xmlns="http://www.hasor.net/sechma/main">
      <!-- mode1.myProject.net 配置 -->
      <mod1:config>
          <mod1:serverLocal mod1:url="www.126.com" />
      </mod1:config>
      <!-- http://mode2.myProject.net 配置 -->
      <mod2:config>
          <mod2:serverLocal mod2:url="www.souhu.com" />
      </mod2:config>
    </config>

.. CAUTION::
    属性文件由于不存在 Xml 命名空间的概念，因此明确指定其所属配置空间需要通过 ``namespace`` 属性来确定：

.. code-block:: text
    :linenos:

    namespace = http://mode2.myProject.net
    key1 = value1
    key2 = value2
    ...


默认配置空间
------------------------------------
`http://www.hasor.net/sechma/main` 空间是保留给应用的。如没有特殊的需求，建议建议使用这个空间：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        ...
    </config>


默认配置空间在一些场景下享有优先权，。例如：“myconfig.driver” 这个配置时，Hasor 会优先尝试在 `http://www.hasor.net/sechma/main` 中读取，其它命名空间的配置按照字符串排序顺序决定。

附录：配置空间表
------------------------------------
所有 Hasor 官方出品，基于 Hasor 体系构建的框架，其配置空间都在 ``http://www.hasor.net/sechma/`` 下进行定义。下面是已经存在并且正在使用中的配置空间：

+-------------+-------------------------------------------------+
| **模块**    | **命名空间**                                    |
+-------------+-------------------------------------------------+
| 应用自身    | `http://www.hasor.net/sechma/main`              |
+-------------+-------------------------------------------------+
| Core        | `http://www.hasor.net/sechma/hasor-core`        |
+-------------+-------------------------------------------------+
| Jdbc        | `http://www.hasor.net/sechma/hasor-db`          |
+-------------+-------------------------------------------------+
| Web         | `http://www.hasor.net/sechma/hasor-web`         |
+-------------+-------------------------------------------------+
| web-mime    | `http://www.hasor.net/sechma/mime-mapping`      |
+-------------+-------------------------------------------------+
| tConsole    | `http://www.hasor.net/sechma/hasor-tconsole`    |
+-------------+-------------------------------------------------+
| RSF         | `http://www.hasor.net/sechma/rsf-framework`     |
+-------------+-------------------------------------------------+
| Registry    | `http://www.hasor.net/sechma/rsf-registry`      |
+-------------+-------------------------------------------------+
| Land        | `http://www.hasor.net/sechma/hasor-land`        |
+-------------+-------------------------------------------------+

