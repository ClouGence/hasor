加载Module
------------------------------------
当您使用 Hasor 时让框架跑起来只是第一步，您还需要一个启动入口用于配置您项目。在这个启动入口里您可以为您的项目做一些实际的设定。

在 Hasor 里一切的开始都是 `net.hasor.core.Module` 接口，您需要编写一个类实现这个接口，接口中只有一个方法需要您实现。下面这段代码就是我们实现的第一个 Module：

.. code-block:: java
    :linenos:

    public class FirstModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
        }
    }


接下来我们迫切需要的是让 Hasor 可以加载这个 Module。对于一般情况下而言我们可以在 `Hasor.create` 时进行加载，例如：

.. code-block:: java
    :linenos:

    Hasor.createAppContext(new FirstModule());


有时候为了减少代码行数我们会把 FirstModule 简写成一个匿名类，例如：

.. code-block:: java
    :linenos:

    Hasor.createAppContext(new Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
        }
    });


如果您打算同时指定 Hasor 配置文件那么会变成如下写法：

.. code-block:: java
    :linenos:

    Hasor.createAppContext("my-config.xml", new FirstModule());
    or
    Hasor.createAppContext("my-config.xml", new Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
        }
    });



如果我有多个启动入口怎么办？没关系 Hasor 支持多个启动入口，你只需要按照你想要的启动顺序排好，然后在 `Hasor.createAppContext` 启动容器时候传递进去就可以了，Hasor 会自动照顾好它们。

加载多个Module
------------------------------------
前面我们一直说的是如何在启动时候通过参数形式加载启动入口，现在我们就来想象一下如下情景。一个项目，按照业务纬度划分了若干个模块。每个模块都可以通过 Hasor 的 Module 来管理，接下来那么为了方便开发，每个模块负责人会负责开发自己的 Module，最后在统一的地方进行加载。

**方式一：** `Hasor.createAppContext` 在初始化 Hasor 容器时指定所有模块。这种方式简单粗暴，无需过多语言进行描述。它的缺点比较明显，那就是随着项目复杂度的提升 Module 可能会越来越多。到时候创建 Hasor 会看起来很复杂。

.. code-block:: java
    :linenos:

    Hasor.createAppContext("my-config.xml", new UserModule(),new ClassModule() ...);


**方式二：** 为了解决第一种方式中带来的不足，我们可以预先创建一个总入口，然后在总入口中逐个加载所有模块。不光如此总入口我们也可以设置多个，甚至总入口中加载的模块可以是项目中其它模块的入口。这种方式解决了 Module 的规划和加载问题。

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


**方式三：** 第三种方式下，您可以通过 xml 的方式来管理 Module。例如文件：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <!-- 默认要装载的模块 -->
        <hasor.modules>
            <module>net.hasor.web.valid.ValidWebPlugin</module>
            <module>net.hasor.web.render.RenderWebPlugin</module>
            <module>net.hasor.plugins.startup.StartupModule</module>
            <module>net.hasor.plugins.aop.AopModule</module>
        </hasor.modules>
    </config>


最小化启动
------------------------------------
如果您想要一个最纯粹的 Hasor，不希望它在初始化时加载任何插件，您可以通过 `HASOR_LOAD_MODULE` 环境变量禁用 `findModules`。例如：下面这个配置中 `HelloModule` 就不会被加载，同时任何一个内置的 Module 都不会被加载。

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor>
            <environmentVar>
                <!-- 是否加载模块 -->
                <HASOR_LOAD_MODULE>false</HASOR_LOAD_MODULE>
            </environmentVar>
            <modules>
                <module>net.test.hasor.HelloModule</module>
            </modules>
        </hasor>
    </config>


下面这个配置文件可以达到和上面配置文件等同。有关配置文件特性的细节请访问配置文件相关章节。

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.modules loadModule="false">
            <module>net.test.hasor.HelloModule</module>
        </hasor.modules>
    </config>

除了配置文件你也可以在创建 Hasor 时通过下面这样的方式来构建一个最小的 Hasor：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().asSmaller().build();

