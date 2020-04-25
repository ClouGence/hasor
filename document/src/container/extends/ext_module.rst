--------------------
Module 机制
--------------------
Module 是步入 Hasor 的必经之路，任何基于 Hasor 开发的程序几乎都离不开 Module。

设计 Module 的本真意图是为了将不同的功能模块可以区别隔离开来，并允许引用程序在自己初始化的时候按照自己的要求进行装配。


启动加载Module
------------------------------------

.. code-block:: java
    :linenos:

    Hasor.create().build(new UserModule(),new ClassModule() ...);


Module加载Module
------------------------------------

.. code-block:: java
    :linenos:

    public class RootModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.installModule(new UserModule());
            apiBinder.installModule(new ClassModule());
            ...
        }
    }


配置文件加载Module
------------------------------------

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <!-- 默认要装载的模块 -->
        <hasor.modules>
            <module>net.hasor.web.render.RenderWebPlugin</module>
            <module>net.hasor.plugins.startup.StartupModule</module>
            <module>net.hasor.plugins.aop.AopModule</module>
        </hasor.modules>
    </config>

.. code-block:: java
    :linenos:

    Hasor.create().mainSettingWith("my-hconfig.xml").build();
