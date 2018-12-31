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
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 该例子演示了事件链。及由事件引发的事件。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class EventLinkTest {
    @Test
    public void syncEventTest() throws InterruptedException {
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        final String SubEvent = "SubEvent";
        final String FirstEvent = "FirstEvent";
        final AtomicInteger atomicInteger = new AtomicInteger();
        //1.添加事件监听器
        ec.addListener(SubEvent, new EventListener<Object>() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                atomicInteger.incrementAndGet();
            }
        });
        ec.addListener(FirstEvent, new EventListener<EventContext>() {
            public void onEvent(String event, EventContext eventEC) throws Throwable {
                eventEC.fireAsyncEvent(SubEvent, 1);
                eventEC.fireAsyncEvent(SubEvent, 2);
            }
        });
        //2.引发种子事件
        ec.fireAsyncEvent(FirstEvent, ec);
        Thread.sleep(1000);
        assert atomicInteger.get() == 2;
    }
}