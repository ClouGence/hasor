启动过程介绍
------------------------------------
Hasor 的生命周期大致分为三个阶段：`init`、`start`、`shutdown`，其中在启动时会一次性完成 init、start 两步操作。如果您想深入到代码层面了解 Hasor 启动和销毁的细节逻辑，那么请参考 `TemplateAppContext` 类。下面这张图是 Hasor 启动和销毁时执行的重要节点（左侧）

.. image:: http://files.hasor.net/uploader/20170305/165750/CC2_040A_4409_E756.jpg

面我们对每一个重要的节点做一个简单的介绍，这些过程代码你可以在 ``net.hasor.core.context.TemplateAppContext.start`` 找到它们。


一、Init阶段
------------------------------------

- `findModules` ：在配置文件中，查找找所有可以加载的 Module。
- `doInitialize` ：执行 init 阶段的起始标志，默认是空实现。
- `newApiBinder` ：创建 Module 在执行 loadModule 方法时用到的 ApiBinder 对象。包括 ApiBinder 的扩展机制也是在这里给予支持。
- `installModule` ：加载每一个 Module，简单来说就是一个 for。
- `doBind` ：容器级的初始化操作，这个过程细分为 doBindBefore、installModule、doBindAfter 三个部分。
- `doInitializeCompleted` ：执行 init 阶段的终止标志，默认是空实现。


二、Start阶段
------------------------------------

- `doStart` ：执行 start 阶段的起始标志。
- `ContextEvent_Started` ：通过事件机制发送 `AppContext#ContextEvent_Started` 事件。
- `doStartCompleted` ：执行 start 阶段的终止标志。


Start 阶段一共有三个扩展点（0.生命周期：图右侧）它们分别是 `ContextStartListener` 接口的 `doStart`、`doStartCompleted` 方法，以及 LifeModule 接口的 `onStart` 方法。下面这张图为我们展现了这三个节点对我们的重要意义。

.. image:: http://files.hasor.net/uploader/20170305/155728/CC2_040A_B4A8_396.jpg


三、Shutdown阶段
------------------------------------

- `doShutdown` ：执行 shutdown 阶段的起始标志。
- `ContextEvent_Shutdown` ：发送 `AppContext#ContextEvent_Shutdown` 事件。
- `doShutdownCompleted` ：执行 shutdown 阶段的终止标志。

相信您在看完前面 `Start 阶段` 相关介绍之后，Shutdown 心里也会有一定的了解。下面我们讲解一些 `Shutdown` 中你不知道的事情。您可能注意到，Hasor 的所有 Test Case 包括 本手册中所有地方，都没有去执行 shutdown。即便如此，Hasor 依然会感知到应用程序停止并完成调用 shutdown。

首先 Hasor 在 start 时候会通过 `Runtime.getRuntime().addShutdownHook(...)` 注册一个钩子用于在 JVM 推出时自动执行 shutdown。

