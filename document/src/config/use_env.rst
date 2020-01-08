环境变量
------------------------------------
这些位置可以成为Hasor环境变量的来源：

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

例如：在 jvm 启动时增加一个参数：`-DMY_NAME=ccc`，然后在程序中得到它。

.. code-block:: java
    :linenos:

    AppContext appContext = ...
    Environment env = appContext.getEnvironment();
    System.out.println(env.getVariable("i say %MY_NAME%."));

.. HINT::
    Hasor 对于环境变量的定义是不区分大小写的。但建议：环境变量名，要求全部必须大写。


**配置文件中声明环境变量**
在配置文件中声明环境变量必须要在 `hasor.environmentVar` 节点中配置：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <hasor.environmentVar>
            <MY_VAR>Hello Word , this is my JAVA_HOME : %JAVA_HOME%</MY_VAR>
        </hasor.environmentVar>
    </config>


在项目中获取这个环境变量：

.. code-block:: java
    :linenos:

    AppContext appContext = ...
    Environment env = appContext.getEnvironment();
    System.out.println(env.getVariable("MY_VAR"));


环境变量表达式
------------------------------------
例1：引用其它环境变量

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <hasor.environmentVar>
            <MY_VAR>Hello Word , this is my JAVA_HOME : %JAVA_HOME%</MY_VAR>
        </hasor.environmentVar>
    </config>


MY_VAR 的输出结果是：`Hello Word , this is my JAVA_HOME : xxxxxx`


例2：引用多个环境变量

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <hasor.environmentVar>
            <MY_NAME>zyc</MY_NAME>
            <MY_AGE>100</MY_>
            <MY_VAR>my name is : %MY_NAME% , age is %MY_AGE%</MY_VAR>
        </hasor.environmentVar>
    </config>


MY_VAR 的输出结果是：`my name is : zyc , age is 100`


引用环境变量
------------------------------------
前面我们讲解了 Hasor 环境变量的使用。同时我们也演示了 Hasor 环境变量和操作系统的互动，本节向您展示 Hasor 环境变量在配置文件中的应用。我们以配置数据库链接配置作为开始：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <jdbcSettings>
            <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
            <userName>sa</userName>
            <userPassword>password</userPassword>
        </jdbcSettings>
    </config>


如果我们想把数据库连接的帐号和密码剥离出来，我们可以先把帐号和密码剥离到环境变量中，接着就可以使用前面章节提到的通过系统环境变量来动态替换：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <hasor.environmentVar>
            <JDBC_USER_NAME>sa</JDBC_USER_NAME>
            <JDBC_USER_PWD>password</JDBC_USER_PWD>
        </hasor.environmentVar>

        <jdbcSettings>
            <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
            <userName>${JDBC_USER_NAME}</userName>
            <userPassword>${JDBC_USER_PWD}</userPassword>
        </jdbcSettings>
    </config>

然后在 jvm 启动时候将 JDBC_USER_NAME 和 JDBC_USER_PWD 动态的传入给应用程序。