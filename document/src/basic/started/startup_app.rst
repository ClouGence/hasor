引入依赖
------------------------------------
截止到目前为止 Hasor 的最新版本为：**4.0.7** ，下面以 maven 为例。

- 这个网站会提供给您各种依赖管理框架的引入配置，支持：Maven、Gradle、SBT、Ivy、Grape、Leiningen、Buildr
- https://mvnrepository.com/artifact/net.hasor

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-core</artifactId>
        <version>4.0.7</version>
    </dependency>


引入依赖包之后通过一行代码即可创建 Hasor

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build();


启动模式
------------------------------------
Hasor 的启动有三种模式，所有模式都在 `net.hasor.core.Hasor.Level` 类中定义。Hasor 默认运行在 Full 模式中，无需特意指定。


明确指定 Hasor 启动模式的方法：

+--------+----------------------------------------+-----------------------------------------+
| 模式   | 启动方式                               | 功效                                    |
+========+========================================+=========================================+
| Full   | Hasor.create().asFull().build();       | 完整加载框架和可以发现的所有插件模块    |
+--------+----------------------------------------+-----------------------------------------+
| Core   | Hasor.create().asCore().build();       | 只完整的加载 hasor-core                 |
+--------+----------------------------------------+-----------------------------------------+
| Tiny   | Hasor.create().asTiny().build();       | 最小化启动，放弃一切插件加载。          |
+--------+----------------------------------------+-----------------------------------------+

.. CAUTION::
    Tiny 模式下，相当于设置 `HASOR_LOAD_MODULE` 和 `HASOR_LOAD_EXTERNALBINDER` 两个环境变量为 false。


Module
------------------------------------
.. HINT::
    Module 是使用 Hasor 的统一入口，它的地位类似于 java 的 main 方法。

在 Hasor 里一切的开始都是 `net.hasor.core.Module` 接口，您需要编写一个类实现这个接口，接口中只有一个方法需要您实现。下面这段代码就是我们实现的第一个 Module：

.. code-block:: java
    :linenos:

    public class FirstModule implements Module {
        public void loadModule(ApiBinder apiBinder) {
            ...
        }
    }

接着在启动的中加载这个 Module

.. code-block:: java
    :linenos:

    Hasor.create().build(new FirstModule());


如果有多个 Module 那么都指定进来

.. code-block:: java
    :linenos:

    Hasor.create().build(new UserModule(),new ClassModule() ...);


另外还可以在 Module 中加载其它 Module

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
