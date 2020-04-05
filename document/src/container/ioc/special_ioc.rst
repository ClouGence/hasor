注入配置
------------------------------------
下面以属性文件为例：

.. code-block:: properties
    :linenos:

    jdbcSettings.jdbcDriver = com.mysql.jdbc.Driver
    jdbcSettings.jdbcURL = jdbc:mysql://127.0.0.1:3306/test
    jdbcSettings.userName = sa
    jdbcSettings.userPassword =


也可以通过 Xml 文件来表示相同的配置内容：

.. code-block:: xml

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <jdbcSettings>
            <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
            <jdbcURL>jdbc:mysql://127.0.0.1:3306/test</jdbcURL>
            <userName>sa</userName>
            <userPassword></userPassword>
        </jdbcSettings>
    </config>

在 Bean 中通过 `net.hasor.core.InjectSettings` 注解来表示注入的内容来自于配置文件

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("jdbcSettings.jdbcDriver")
        private String jdbcDriver;
        @InjectSettings("jdbcSettings.jdbcURL")
        private String jdbcURL;
        @InjectSettings("jdbcSettings.user")
        private String user;
        @InjectSettings("jdbcSettings.password")
        private String password;
        ...
    }

最后在创建容器的时候指定要加载的配置文件即可

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().mainSettingWith("<config-file-name>").build();


**类型自动转换**
`@InjectSettings` 可以帮助做一些简单的类型转换，类型转换工具为 `net.hasor.utils.convert.ConverterUtils`。其来源为： Apache Commons

.. code-block:: java
    :linenos:

    public class TestBean {
        @InjectSettings("userInfo.myAge")
        private int myAge;
    }


注入环境变量
------------------------------------
把敏感信息通过环境参数传递给应用是一个十分安全的做法，Hasor 支持注入一个环境参数。例如：

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("${db.user}")
        private String user;
        @InjectSettings("${db.pwd}")
        private String password;
        ...
    }


然后当启动程序时，追加两个 `-D` 参数即可： ``java TestMain -Ddb.user=username -Ddb.pwd=password``


除了 `-D` 参数之外，环境变量还可以是系统环境变量。例如得到 JAVA_HOME 位置。

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("${JAVA_HOME}")
        private String javaHome;
    }

.. HINT::

    这些位置可以成为Hasor环境变量的来源

+---------------------------------------------------------------------+
| 位置                                                                |
+=====================================================================+
| `System.getenv()`                                                   |
+---------------------------------------------------------------------+
| `System.getProperties()`                                            |
+---------------------------------------------------------------------+
| `hconfig.xml` 配置文件中 `hasor.environmentVar` 的子节点            |
+---------------------------------------------------------------------+
| `Hasor.create().addVariable(...)`                                   |
+---------------------------------------------------------------------+


注入容器类型
------------------------------------
例如：得到容器自身

.. code-block:: java
    :linenos:

    public class AwareBean {
        @Inject()
        private AppContext appContext
    }
    //或
    public class AwareBean implements AppContextAware {
        public void setAppContext(AppContext appContext) {
            ...
        }
    }


其它容器可以被注入的特殊类型：

+-------------------------------+--------------------+
| 功效                          | 接口               |
+===============================+====================+
| net.hasor.core.AppContext     | 容器               |
+-------------------------------+--------------------+
| net.hasor.core.Settings       | 配置文件接口       |
+-------------------------------+--------------------+
| net.hasor.core.Environment    | 环境变量接口       |
+-------------------------------+--------------------+
| net.hasor.core.spi.SpiTrigger | SPI 触发器         |
+-------------------------------+--------------------+
| net.hasor.core.EventContext   | 容器事件模型接口   |
+-------------------------------+--------------------+
