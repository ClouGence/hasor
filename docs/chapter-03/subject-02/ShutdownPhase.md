- `doShutdown` ：执行 shutdown 阶段的起始标志。
- `ContextEvent_Shutdown` ：发送 `AppContext#ContextEvent_Shutdown` 事件。
- `doShutdownCompleted` ：执行 shutdown 阶段的终止标志。
---

&emsp;&emsp;相信您在看完前面 `Start 阶段` 相关介绍之后，Shutdown 心里也会有一定的了解。下面我们讲解一些 `Shutdown` 中你不知道的事情。您可能注意到，Hasor 的所有 Test Case 包括 本手册中所有地方，都没有去执行 shutdown。即便如此，Hasor 依然会感知到是否需要 shutdown。

- 首先 Hasor 在 start 时候会通过 `Runtime.getRuntime().addShutdownHook(...)` 注册一个钩子用于在 JVM 推出时自动执行 shutdown。