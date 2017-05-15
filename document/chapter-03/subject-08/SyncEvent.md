&emsp;&emsp;同步事件，同步事件是指当主流程引发事件时，是否阻塞主流程执行，等待所有事件监听器都执行完毕之后在恢复主流程的执行。同步调用的好处是相当于在不动原有代码的情况下通过事件监听器的方式可以动态的增减业务逻辑代码。下面就同步事件的特点加以详细说明

![事件模型](http://files.hasor.net/uploader/20170316/043353/CC2_950A_504E_AC82.jpg "事件模型")

&emsp;&emsp;同步特点：
- 主流程的调用等待事件执行完毕。

&emsp;&emsp;根据执行事件监听器线程模型的不同还可以分为：
- 独享线程
- 共享线程

&emsp;&emsp;独享线程，指的是当 Hasor 开始执行事件监听器时，使用一个全新的线程去执行监听器。引发这种事件执行的方式是：
```java
EventContext eventContext = ...
eventContext.fireSyncEventWithEspecial(EventName, ...);
```

&emsp;&emsp;共享线程，指的是当 Hasor 开始执行事件监听器时，使用当前线程执行监听器。引发这种事件执行的方式是：
```java
EventContext eventContext = ...
eventContext.fireSyncEvent(EventName, ...);
```

&emsp;&emsp;注意事项：
- 独享线程下，如果大量的事件抛出，可能会因为频繁的线程切换而导致性能下降。
- 因此独享线程，仅当您有需要时在使用，因为无论是共享线程还是独享，对于同步调用来说结果是一样的都要消耗等待时间。