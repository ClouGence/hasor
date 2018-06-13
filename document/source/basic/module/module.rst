模块划分
------------------------------------
在设计软件系统时，通过划分模块可以让应用的结构更加清晰合理。模块还可以根据实际需要，设计成可更换的单元。因此模块化是一种处理复杂系统分解成为更好的可管理模式。它可以通过在不同组件设定不同的功能，把一个问题分解成多个小的独立、互相作用的组件，来处理复杂、大型的系统。

Hasor 是一个支持模块化的开发框架，它的模块化有着非常清晰的模块接口定义。实现一个 Hasor 模块很简单，只需要实现 `net.hasor.core.Module` 接口。即便是各种 Hasor 插件也都是通过 `Module` 接口提供的扩展。所以说，无论您是在编写 Hasor 插件，还是使用 Hasor 进行模块化开发，都要用到 Module。这也是 Hasor “微内核 + 插件” 架构特定的一种体现。


区别 OSGi ，OSGi有着更加完整的模块隔离和管理能力，这些是 Hasor 不具备的。因此你可以理解对比 OSGi 来说 Hasor 的模块化是非常弱的。举个例子：OSGi支持模块热插拔，Hasor 的模块化只能支持启动时统一加载。另外 OSGi 支持模块在 ClassLoader 级别上的隔离，而 Hasor 是共享同一个 ClassLoader。


尽管如此弱模块化依然是我们的好帮手，在你的应用中总是会存在各种业务纬度或功能纬度上的职责划分。每一个职责你都可以使用一个 Module 作为承载。如果你已经这样做了，那么说明你已经走在 Hasor 模块化的道路上了。

模块类别
------------------------------------
前面讲解了有关 Module 的一些特点，在这一小节我们将专门讲解一下作为我们开发人员都会使用到哪些 Module。为了让大家非常直观的有一个认识。先向大家展示一张 Module 家族的类图：

.. image:: http://files.hasor.net/uploader/20170310/114317/CC2_C40A_FE51_98E3.jpg

这是早期的 Hasor 模块，用的频率比较高的有：Module、WebModule、RsfModule。而例如：MyBatisModule、SpringModule 这些则是一些插件。下面我们看看下面这三兄弟怎么使用。

**Module，标准模块**

.. code-block:: java
    :linenos:

    public class DemoModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
        }
    }


**WebModule，Web模块**

.. code-block:: java
    :linenos:

    public class MyWebModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
        }
    }


**RsfModule，分布式RPC模块**

.. code-block:: java
    :linenos:

    public class MyRPCModule extends RsfModule {
        public void loadModule(RsfApiBinder apiBinder) throws Throwable {
            ...
        }
    }


依赖
------------------------------------
Hasor 的模块之间依赖关系，主要是靠加载顺序来保证。很遗憾的是 Hasor 没有提供一种渠道让您想使用 maven 那样，明确的配置 模块之间相互的依赖关系。我们以一个例子来向您展示 Hasor 的模块依赖是如何进行的。

现在假定我们在开发一个用户管理系统，系统根据功能大致分为：用户模块、权限模块、分类打标、登录认证。

上述模块划分是业务纬度，在实际开发中我们真正落实写代码时往往会有一个技术纬度的功能分类。例如上述的业务模块可能最终的技术模块会这样分：数据库读写、OAuth、SSO、Domain、User、Auth。它们分别对应的是：

- ORM --> 数据模型和数据库的映射，并提供数据库操作。
- OAuth --> 合作网站登录。
- SSO --> 单点登录方案。
- Auth --> 权限认证和查询接口。
- Domain --> 数据模型。
- User  --> 用户系统的业务逻辑。

接着使用 Hasor 的 Module 可以定义每一个模块，这里很简单无外乎就是定义了 几个类。例如：

.. code-block:: java
    :linenos:

    public class MyBatisModule implements Module { ... }
    public class OAuthModule implements Module { ... }
    public class SSOModule implements Module { ... }
    ...

现在我们已经可以用不同的模块类将不同的部分隔离开，接下来我们需要一个地方将它们整体配置到一起，并指定它们的顺序。

第一个方式，就是我们前面几次经常看到的方式，通过 xml 配置这些 Module。当然在这里您也可沿用这种方式。

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.modules>
            <module>net.myproject.db.MyBatisModule</module>
            <module>net.myproject.auth.OAuthModule</module>
            ...
        </hasor.modules>
    </config>

第二种方式，代码方式，首先找到我们项目的入口 Module，我们假定这个 Module 名字叫做 RootModule。那么接下来我们只要在 RootModule 中想下面这样把这几个 Module 安装进去就可以了。

.. code-block:: java
    :linenos:

    public class RootModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.installModule(new MyBatisModule());
            apiBinder.installModule(new OAuthModule());
            ...
        }
    }


多工程项目
------------------------------------
这个小节，我们讲一讲如何在一个多工程的系统中实践 Hasor 模块化。

当项目的规模大到一定量的时候，我们通常会按照不同功能把项目拆分成若干部分。然后每个部分单独放到一个工程中。例如 Hasor 的首页项目就拆分为多个工程，如下：

.. code-block:: xml
    :linenos:

    website-domain       // 模型定义
      ^      ^
      | website-client   // RPC服务接口
      |      ^
    website-core         // 服务类和业务逻辑
      ^  ^   ^
      |  | website-login // OAuth
      |  |   ^
      | website-web      // 处理Web请求和响应
    website-test         // 各类单元测试


Hasor 在拆分多个工程时通常你不需要做什么特别的事，只要在不同的工程里写自己的 Module 就可以了，最后在统一把 Module 汇总一下万事大吉。没错 Hasor 在的多工程的项目中模块化实践的确就是这么干的。