工程配置
------------------------------------
Hasor的Web支持是一个独立的框架，在使用它之前首先引入它。在您的项目中添加下面这个依赖，然后配置 web.xml 即可。

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-web</artifactId>
        <version>4.1.2</version>
    </dependency>


接下来配置 web.xml 配置文件：

.. code-block:: xml
    :linenos:

    <!-- 框架启动 -->
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
    <!-- (建议)启动模块 -->
    <context-param>
        <param-name>hasor-root-module</param-name>
        <param-value>com.xxx.you.project.StartModule</param-value>
    </context-param>
    <!-- (可选)如果有配置文件在这里指定 -->
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


.. HINT::
    配置项 `hasor-root-module` 可以在配置文件中进行等效配置，使用配置文件的好处是可以提供更丰富的配置。具体如下：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <hasor>
            <!-- 项目所属包：减少类扫描范围 -->
            <loadPackages>com.xxx.you.project.*</loadPackages>
            <!-- 框架启动入口 -->
            <startup>com.xxx.you.project.StartModule</startup>
        </hasor>
    </config>


HelloWord
------------------------------------
这里展示基于 MVC 使用 Hasor 接收一个 Web 请求然后交给 jsp 显示的例子。

首先创建请求处理器，一个请求处理器可以简单的只包含一个 `execute` 方法

.. code-block:: java
    :linenos:

    @MappingTo("/hello.jsp")
    public class HelloMessage {
        public void execute(Invoker invoker) {
            invoker.put("message", "this message form Project.");
        }
    }


然后在启动模块中注册控制器

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            //设置请求响应编码
            apiBinder.setEncodingCharacter("utf-8", "utf-8");
            // 扫描所有带有 @MappingTo 特征类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            // 配置控制器
            apiBinder.loadMappingTo(aClass);
        }
    }


最后创建 `hello.jsp` 文件，我们把 `message` 打印出来：

.. code-block:: jsp
    :linenos:

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
        <head>
            <title>Hello Word</title>
        </head>
        <body>
            ${message}
        </body>
    </html>


当上面的一切都做好之后，启动您的 web 工程，访问： `http://localhost:8080/hello.jsp` 即可得到结果。
