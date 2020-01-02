Web工程配置
------------------------------------
在 Web 开发开始前，请先检查您的工程是否是一个 Web 工程。一个正确的 Web 工程应该包含一个存有 web.xml 文件的 webapp 目录。

首先您需要创建一个配置文件并放入您的 classpath 中。文件名随意，我们使用 “hasor-config.xml”，内容如下：

.. code-block:: xml
    :linenos:

        <?xml version="1.0" encoding="UTF-8"?>
        <config xmlns="http://project.hasor.net/hasor/schema/main">
            <hasor>
                <!-- 项目所属包：减少类扫描范围 -->
                <loadPackages>com.xxx.you.project.*</loadPackages>
                <!-- 框架启动入口 -->
                <startup>com.xxx.you.project.StartModule</startup>
            </hasor>
        </config>


接下来配置 web.xml 配置文件。

.. code-block:: xml
    :linenos:

    <!-- 框架启动入口 -->
    <listener>
        <listener-class>net.hasor.web.startup.RuntimeListener</listener-class>
    </listener>
    <!-- 全局拦截器 -->
    <filter>
        <filter-name>rootFilter</filter-name>
        <filter-class>net.hasor.web.startup.RuntimeFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>rootFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!-- 上面创建的那个配置文件名 -->
    <context-param>
        <param-name>hasor-hconfig-name</param-name>
        <param-value>hasor-config.xml</param-value>
    </context-param>


最后创建包 “com.xxx.you.project” 并在包中新增一个类 “StartModule” 该类，内容如下：

.. code-block:: java
    :linenos:

    package com.xxx.you.project;
    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            System.out.println("You Project Start.");
        }
    }

启动您的的 Web 工程，如果控制台上看到 “You Project Start.” 则证明框架成功配置。



**“零”配置**

在上面例子基础上，删掉 hasor-config.xml 配置文件。然后在 web.xml 中增加如下配置：

.. code-block:: xml
    :linenos:

    <!-- 上面创建的那个配置文件名 -->
    <context-param>
        <param-name>hasor-root-module</param-name>
        <param-value>com.xxx.you.project.StartModule</param-value>
    </context-param>

重新启动工程，你会发现控制台上依然可以看到 “You Project Start.”，这是由于 “hasor-root-module” 配置替代了 hasor-config.xml 中配置的入口。
