启动
------------------------------------
根据项目类型的不同 Hasor 的启动方式可能会有不同。我们以一般情况下，您可以通过下面这行代码启动 Hasor 容器。Hasor 是支持 ‘零’ 配置文件的，因此您大可不必为 Hasor 准备任何配置文件。

.. code-block:: java
    :linenos:

    import net.hasor.core.Hasor;
    AppContext appContext = Hasor.create().build();


当然您可以在项目中创建配置文件给 Hasor ，下面这段代码展示了 Hasor 容器时使用配置文件。如下所示：

.. code-block:: java
    :linenos:

    import net.hasor.core.Hasor;
    AppContext appContext = Hasor.create().mainSettingWith("simple-config.xml").build();


simple-config.xml 配置文件的格式如下:

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        ...
    </config>


启动模式
------------------------------------
Hasor 的启动有三种模式，所有模式都在 `net.hasor.core.Hasor.Level` 类中定义。Hasor默认运行在 Full 模式中。
三种启动模式的介绍如下：

- Full：完整加载框架和可以发现的所有插件模块。
- Core：核心部分，只完整的加载 hasor-core。
- Tiny：最小化启动，放弃一切插件加载。相当于设置如下两个环境变量为 false

HASOR_LOAD_MODULE、HASOR_LOAD_EXTERNALBINDER

带有设置启动模式的代码如下：

.. code-block:: java
    :linenos:

    Hasor.create().asCore().build(); // Core 模式
    Hasor.create().asFull().build(); // Full 模式
    Hasor.create().asTiny().build(); // Tiny 模式