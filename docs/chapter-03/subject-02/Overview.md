&emsp;&emsp;Hasor 的生命周期大致分为三个阶段：`init`、`start`、`shutdown`，其中在启动时会一次性完成 init、start 两步操作。如果您想深入到代码层面了解 Hasor 启动和销毁的细节逻辑，那么请参考 `TemplateAppContext` 类。下面这张图是 Hasor 启动和销毁时执行的重要节点（左侧）

![启动流程](http://files.hasor.net/uploader/20170305/165750/CC2_040A_4409_E756.jpg "Hasor生命周期")

&emsp;&emsp;下面我们对每一个重要的节点做一个简单的介绍。

---
#### Init阶段
- `findModules` ：查找所有可以加载的 Module。
- `doInitialize` ：执行 init 阶段的起始标志。
- `newApiBinder` ：创建 Module 在执行 loadModule 方法时用到的 ApiBinder 参数。
- `installModule` ：加载每一个 Module。
- `doBind` ：容器级的初始化操作
- `doInitializeCompleted` ：执行 init 阶段的终止标志。

---
#### Start阶段
- `doStart` ：执行 start 阶段的起始标志。
- `ContextEvent_Started` ：通过事件机制发送 `AppContext#ContextEvent_Started` 事件。
- `doStartCompleted` ：执行 start 阶段的终止标志。

---
#### Shutdown阶段
- `doShutdown` ：执行 shutdown 阶段的起始标志。
- `ContextEvent_Shutdown` ：发送 `AppContext#ContextEvent_Shutdown` 事件。
- `doShutdownCompleted` ：执行 shutdown 阶段的终止标志。
