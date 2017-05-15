&emsp;&emsp;执行一次的事件，这是一类特殊的事件执行方式。无论您注册的是 `同步事件` 还是 `异步事件` 都可以将事件监听器注册为只执行一次这种模式。

&emsp;&emsp;只执行一次，这种事件通常是用在 Hasor 在 init 过程中注册一个 `ContextEvent_Started` 事件。当应用启动引发 Started 事件之后自动注销事件监听器。

&emsp;&emsp;下面这行代码就是注册方式：
```java
EventContext eventContext = ...
eventContext.pushListener("EventName",new MyListener());
```