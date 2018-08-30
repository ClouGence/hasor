配置上引用环境变量
------------------------------------
前面我们讲解了 Hasor 环境变量的使用。同时我们也演示了 Hasor 环境变量和操作系统的互动，本节向您展示 Hasor 环境变量在配置文件中的应用。我们以配置数据库链接配置作为开始：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
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
    <config xmlns="http://project.hasor.net/hasor/schema/main">
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