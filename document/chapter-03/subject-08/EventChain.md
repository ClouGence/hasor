&emsp;&emsp;事件链，指的是在事件中引发另一个或多个事件，一个完整的事件链的例子如下：
```java
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
```