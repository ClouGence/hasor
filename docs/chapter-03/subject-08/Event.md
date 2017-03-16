&emsp;&emsp;虽然 Hasor 提供的 Module 可以帮助您模块化项目开发。但是在代码层面往往多个系统之间还会有直接或者间接的调用，耦合度还是很高。这个时候您可以适当的使用 Hasor 的事件机制来进行深度解耦。使用事件可以为程序的模块划清界限，明确了通知者和接受者之间的关系。同时使用事件还可以增加程序的可维护性和重用性。

&emsp;&emsp; Hasor 事件的执行分为三种：**同步(独享线程)**、**同步(共享线程)**、**异步**，它们的执行模型如下：
![事件模型](http://files.hasor.net/uploader/20170316/033147/CC2_950A_FEDD_45ED.jpg "事件模型")

&emsp;&emsp; 无论是同步的事件模型，还是异步事件模型。在 Hasor 事件体系中，它们都有以下共同性质：
- 1.按注册顺序执行事件监听器
- 2.事件监听器接口相同
- 3.事件注册方式相同

&emsp;&emsp;后面会有专门的文章分别介绍 Hasor 的各类事件机制差异点。在开始这些内容之前，现在先让我们来看一看如何注册事件监听器。首先我们先实现一个事件监听器：
```java
import net.hasor.core.EventListener;
public class MyListener implements EventListener<Object> {
    public void onEvent(String event, Object eventData) throws InterruptedException {
        Thread.sleep(500);
        System.out.println("Receive Message:" + JSON.toJSONString(eventData));
    }
}
```

&emsp;&emsp;在 Hasor 中事件的注册和发送都是通过 EventContext 发送出去的。因此您需要首先拿到这个接口对象，才能将事件监听器注册上。下面是不同的情况下如何取得 EventContext 接口的例子：
```java
ApiBinder apiBinder = ...
EventContext ec = apiBinder.getEnvironment().getEventContext();
```

```java
AppContext appContext = ...;
EventContext eventContext = appContext.getInstance(EventContext.class);
or
EventContext eventContext = appContext.getEnvironment().getEventContext();
```

```java
public class MyBean{
    @Inject
    private EventContext eventContext;
}
```

&emsp;&emsp;接着我们通过 EventContext 将事件注册到容器中。
```java
EventContext eventContext = ...
eventContext.addListener("EventName",new MyListener());
```

&emsp;&emsp;在注册完毕之后我们可以引发事件：
```java
eventContext.fireSyncEvent("EventName",...);
```