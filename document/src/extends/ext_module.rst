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
