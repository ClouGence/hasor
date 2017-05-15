&emsp;&emsp;异步事件，异步事件是指当主流程引发事件时，不阻塞主流程，事件的执行完全交给事件线程进行异步调用。如果同时引发多个异步事件，会存在并发执行的情况。异步事件非常适合用于不影响主流程的消息通知。下面就异步事件的特点加以详细说明

![事件模型](http://files.hasor.net/uploader/20170316/044840/CC2_950A_8E4E_2972.jpg "事件模型")

&emsp;&emsp;异步特点：
- 主流程的调用在引发事件之后即刻返回，事件执行异步并发进行。

&emsp;&emsp;触发异步事件的方法为：
```java
EventContext eventContext = ...
eventContext.fireSyncEvent(EventName, ...);
```

&emsp;&emsp;异步事件有两种工作方式，由 FireType 枚举定义：
- Interrupt（顺序执行所有监听器，如果中途出错，那么终止执行）
- Continue（顺序执行所有监听器，如果中途出错，那么继续执行下一个监听器）

&emsp;&emsp;fireSyncEvent 方法默认的执行方式是 Interrupt，下面是两者的触发方式：
```java
EventContext eventContext = ...
eventContext.fireSyncEvent(EventName, ... ,FireType.Interrupt);
or
eventContext.fireSyncEvent(EventName, ... ,FireType.Continue);
```
