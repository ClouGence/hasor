意义
------------------------------------

开发 Hasor to Spring 的动机并不是为了取代 Spring 的某些功能，相反而是让 Spring 可以更加方便的使用 Hasor 的一些功能。例如：DataQL。
同时 Hasor 也可以借助 Spring 的一些优势来简化 Hasor 的开发。比如 Hasor + Spring Boot 可以让 Hasor 具备 boot 的能力。

当然这种结合也有一些可能意义并不是十分明显的例子，例如：Spring Boot + Hasor Web。
无论怎样，让 Hasor 和 Spring 产生交集还是利大于弊的。这就是开发 Hasor-Spring 的本真意图。

Spring版本说明
------------------------------------
开发 Hasor-Spring 插件时使用的是 Spring(5.2.3.RELEASE)、SpringBoot(2.2.4.RELEASE)，但是这并不是说用到了它们的最新特性。

实际情况 Spring 这部分的集成代码，早在 Spring2.5 版本上就做过兼容。

整合的方式有两种

    - Spring Xml配置文件方式
    - SpringBoot @Enabel* 注解方式

引入依赖
------------------------------------
无论是使用 Spring Boot 还是 Spring Xml 方式，您都需要引入下面这个依赖。

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-spring</artifactId>
        <version>4.1.4</version>
    </dependency>
