同步事件
------------------------------------
同步事件，同步事件是指当主流程引发事件时，是否阻塞主流程执行，等待所有事件监听器都执行完毕之后在恢复主流程的执行。同步调用的好处是相当于在不动原有代码的情况下通过事件监听器的方式可以动态的增减业务逻辑代码。下面就同步事件的特点加以详细说明

.. image:: http://files.hasor.net/uploader/20170316/043353/CC2_950A_504E_AC82.jpg

同步特点：

- 主流程的调用等待事件执行完毕。

根据执行事件监听器线程模型的不同还可以分为：

- 独享线程：指的是当 Hasor 开始执行事件监听器时，使用一个全新的线程去执行监听器。
- 共享线程：指的是当 Hasor 开始执行事件监听器时，使用当前线程执行监听器。

.. code-block:: java
    :linenos:

    // 独享线程
    EventContext eventContext = ...
    eventContext.fireSyncEventWithEspecial(EventName, ...);

    // 共享线程
    EventContext eventContext = ...
    eventContext.fireSyncEvent(EventName, ...);

.. CAUTION::
    独享线程下，如果大量的事件抛出，可能会导致性能下降。


异步事件
------------------------------------
异步事件，异步事件是指当主流程引发事件时，不阻塞主流程，事件的执行完全交给事件线程进行异步调用。如果同时引发多个异步事件，会存在并发执行的情况。异步事件非常适合用于不影响主流程的消息通知。下面就异步事件的特点加以详细说明

.. image:: http://files.hasor.net/uploader/20170316/044840/CC2_950A_8E4E_2972.jpg

异步特点是：

- 主流程的调用在引发事件之后即刻返回，事件执行异步并发进行。

触发异步事件的方法为：

.. code-block:: java
    :linenos:

    EventContext eventContext = ...
    eventContext.fireSyncEvent(EventName, ...);


异步事件有两种工作方式，由 FireType 枚举定义：

- Interrupt（顺序执行所有监听器，如果中途出错，那么终止执行）
- Continue（顺序执行所有监听器，如果中途出错，那么继续执行下一个监听器）

fireSyncEvent 方法默认的执行方式是 Interrupt，下面是两者的触发方式：

.. code-block:: java
    :linenos:

    EventContext eventContext = ...
    eventContext.fireSyncEvent(EventName, ... ,FireType.Interrupt);
    or
    eventContext.fireSyncEvent(EventName, ... ,FireType.Continue);


执行一次的事件
------------------------------------
执行一次的事件，这是一类特殊的事件执行方式。无论您注册的是 `同步事件` 还是 `异步事件` 都可以将事件监听器注册为只执行一次这种模式。

只执行一次，这种事件通常是用在 Hasor 在 init 过程中注册一个 `ContextEvent_Started` 事件。当应用启动引发 Started 事件之后自动注销事件监听器。

下面这行代码就是注册方式：

.. code-block:: java
    :linenos:

    EventContext eventContext = ...
    eventContext.pushListener("EventName",new MyListener());
