启动
------------------------------------
根据项目类型的不同 Hasor 的启动方式可能会有不同。我们以一般情况下，您可以通过下面这行代码启动 Hasor 容器。Hasor 是支持 ‘零’ 配置文件的，因此您大可不必为 Hasor 准备任何配置文件。

.. code-block:: java
    :linenos:

    import net.hasor.core.Hasor;
    AppContext appContext = Hasor.createAppContext();


当然您可以在项目中创建配置文件给 Hasor ，下面这段代码展示了 Hasor 容器时使用配置文件。如下所示：

.. code-block:: java
    :linenos:

    import net.hasor.core.Hasor;
    AppContext appContext = Hasor.createAppContext("simple-config.xml");


simple-config.xml 配置文件的格式如下:

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        ...
    </config>


唯一
------------------------------------
有的时候我们为了方便将AppContext放到某个 static 的静态变量上。在 Hasor 提供了一种方式来协助你做到这一点，您无需自己额外编写多余的类来进行管理。

.. code-block:: xml
    :linenos:

    // .初始化为全局
    Hasor.create("xxxx").asGlobalSingleton();
    // .每次使用 AppContext 这样就可以获取
    AppContext appContext = Hasor.localAppContext();


除了上面创建全局唯一 AppContext 之外，Hasor 还提供了另外几个场景的唯一。使用它们的方式就是将 “asGlobalSingleton” 换为你所需要的。

- asGlobalSingleton()：使用 JNDI 方式保证 JVM 内全局唯一。
- asStaticSingleton()：使用 static 方式保证全局唯一。
- asThreadSingleton()：线程级别的唯一。
- asContextSingleton()：基于当前线程的 ClassLoader 来保证 Loader 级别的唯一。

