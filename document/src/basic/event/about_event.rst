Hasor的事件
------------------------------------
虽然 Hasor 提供的 Module 可以帮助您模块化项目开发。但是在代码层面往往多个系统之间还会有直接或者间接的调用，耦合度还是很高。这个时候您可以适当的使用 Hasor 的事件机制来进行深度解耦。使用事件可以为程序的模块划清界限，明确了通知者和接受者之间的关系。同时使用事件还可以增加程序的可维护性和重用性。

Hasor 事件的执行分为三种： **同步(独享线程)**、 **同步(共享线程)**、 **异步**，它们的执行模型如下：

.. image:: http://files.hasor.net/uploader/20170316/033147/CC2_950A_FEDD_45ED.jpg

无论是同步的事件模型，还是异步事件模型。在 Hasor 事件体系中，它们都有以下共同性质：

1. 按注册顺序执行事件监听器
2. 事件监听器接口相同
3. 事件注册方式相同

后面会有专门的文章分别介绍 Hasor 的各类事件机制差异点。在开始这些内容之前，现在先让我们来看一看如何注册事件监听器。首先我们先实现一个事件监听器：

.. code-block:: java
    :linenos:

    import net.hasor.core.EventListener;
    public class MyListener implements EventListener<Object> {
        public void onEvent(String event, Object eventData) throws InterruptedException {
            Thread.sleep(500);
            System.out.println("Receive Message:" + JSON.toJSONString(eventData));
        }
    }


在 Hasor 中事件的注册和发送都是通过 EventContext 发送出去的。因此您需要首先拿到这个接口对象，才能将事件监听器注册上。下面是不同的情况下如何取得 EventContext 接口的例子：

.. code-block:: java
    :linenos:

    ApiBinder apiBinder = ...
    EventContext ec = apiBinder.getEnvironment().getEventContext();

.. code-block:: java
    :linenos:

    AppContext appContext = ...;
    EventContext eventContext = appContext.getInstance(EventContext.class);
    or
    EventContext eventContext = appContext.getEnvironment().getEventContext();


.. code-block:: java
    :linenos:

    public class MyBean{
        @Inject
        private EventContext eventContext;
    }


接着我们通过 EventContext 将事件注册到容器中。

.. code-block:: java
    :linenos:

    EventContext eventContext = ...
    eventContext.addListener("EventName",new MyListener());

在注册完毕之后我们可以引发事件：

.. code-block:: java
    :linenos:

    eventContext.fireSyncEvent("EventName",...);


事件链
------------------------------------
事件链，指的是在事件中引发另一个或多个事件，一个完整的事件链的例子如下：

.. code-block:: java
    :linenos:

    public class MyListener implements EventListener<Object> {
        public void onEvent(String event, Object eventData) throws InterruptedException {
            Thread.sleep(500);
            System.out.println("Receive Message:" + JSON.toJSONString(eventData));
            throw new NullPointerException();
        }
    }

    public class EventLinkTest {
        @Test
        public void syncEventTest() throws InterruptedException {
            System.out.println("--->>syncEventTest<<--");
            AppContext appContext = Hasor.createAppContext();
            EventContext ec = appContext.getEnvironment().getEventContext();
            //
            final String EventName = "MyEvent";//事件链的终端
            final String SeedEvent = "SeedEvent";//种子事件
            //1.添加事件监听器
            ec.addListener(EventName, new MyListener());
            ec.addListener(SeedEvent, new EventListener<AppContext>() {
                public void onEvent(String event, AppContext app) throws Throwable {
                    EventContext localEC = app.getEnvironment().getEventContext();
                    System.out.println("before MyEvent.");
                    localEC.fireAsyncEvent(EventName, 1);
                    localEC.fireAsyncEvent(EventName, 2);
                }
            });
            //2.引发种子事件
            ec.fireAsyncEvent(SeedEvent, appContext);
            //3.由于是异步事件，因此下面这条日志会在所有事件之前喷出
            System.out.println("before All Event.");
            Thread.sleep(1000);
        }
    }


事件线程池配置
------------------------------------
默认配置下，Hasor 执行事件的线程池是 20 您可以通过下面两种方式修改这个设定：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.environmentVar>
            <!-- 执行事件的线程池大小 -->
            <HASOR_LOAD_EVENT_POOL>20</HASOR_LOAD_EVENT_POOL>
        </hasor.environmentVar>
    </config>


.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <!-- 执行事件的线程池大小 -->
        <hasor.eventThreadPoolSize>20</hasor.eventThreadPoolSize>
    </config>
