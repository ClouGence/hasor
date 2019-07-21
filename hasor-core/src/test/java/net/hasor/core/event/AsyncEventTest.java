/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.event;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.FireType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 仅会被执行一次的事件
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsyncEventTest {
    @Test
    public void asyncEventTest() throws InterruptedException {
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        final CopyOnWriteArraySet<String> eventDataSet = new CopyOnWriteArraySet<>();
        ec.addListener(EventName, (event, eventData) -> {
            eventDataSet.add(event + eventData);
            Thread.sleep(110);
        });
        //2.引发异步事件
        ArrayList<String> eventData = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            eventData.add(EventName + i);
            ec.fireAsyncEvent(EventName, i);
        }
        while (eventDataSet.size() != 50) {
            Thread.sleep(500);
            System.out.println("wait for Event Queue.");
        }
        long endTime = System.currentTimeMillis();
        //
        //3.check事件必须都执行到
        for (String key : eventData) {
            assert eventDataSet.contains(key);
        }
        assert (endTime - startTime) < (100 * 50);
    }

    @Test
    public void onesAsyncEventTest() throws Throwable {
        EventContext ec = new StandardEventManager(10, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        final CopyOnWriteArraySet<String> eventDataSet = new CopyOnWriteArraySet<>();
        ec.addListener(EventName, (event, eventData) -> {
            eventDataSet.add(event + eventData);
            Thread.sleep(100); // 100ms
        });
        //2.引发异步事件
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            ec.fireAsyncEvent(EventName, i);
        }
        long endTime = System.currentTimeMillis();
        Thread.sleep(1000);
        //
        //3.check
        assert eventDataSet.size() == 50;// 线程池大小为 10 ，执行完至少要 500ms
        assert (endTime - startTime) < (50 * 100); // 并发执行，所以总时间不应该大于 50 * 100
    }

    @Test
    public void syncTest() throws Throwable {
        //
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        final AtomicInteger exceptionInteger = new AtomicInteger();
        final AtomicInteger completeInteger = new AtomicInteger();
        //
        assert ec.addListener("EventA", (EventListener<EventContext>) (event, eventEC) -> {
            //
        });
        assert ec.addListener("EventB", (EventListener<EventContext>) (event, eventEC) -> {
            throw new Exception("1111");
        });
        EventCallBackHook<Object> callBack = new EventCallBackHook<Object>() {
            @Override
            public void handleException(String eventType, Object eventData, Throwable e) {
                exceptionInteger.incrementAndGet();
            }

            @Override
            public void handleComplete(String eventType, Object eventData) {
                completeInteger.incrementAndGet();
            }
        };
        //
        ec.fireAsyncEvent("EventA", null, FireType.Continue, callBack);
        ec.fireAsyncEvent("EventB", null, FireType.Continue, callBack);
        //
        //
        Thread.sleep(500);
        assert completeInteger.get() == 1;
        assert exceptionInteger.get() == 1;
    }
}