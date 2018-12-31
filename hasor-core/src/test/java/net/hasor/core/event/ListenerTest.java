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

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ListenerTest {
    @Test
    public void listenerTest0() throws Throwable {
        //
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        final String FirstEvent = "FirstEvent";
        final AtomicInteger atomicInteger = new AtomicInteger();
        final EventListener<EventContext> listener = new EventListener<EventContext>() {
            public void onEvent(String event, EventContext eventEC) throws Throwable {
                atomicInteger.incrementAndGet();
            }
        };
        //
        //
        assert ec.addListener(FirstEvent, listener);
        ec.fireSyncEvent(FirstEvent, ec);
        assert ec.removeListener(FirstEvent, listener);
        //
        ec.fireSyncEvent(FirstEvent, ec);
        assert atomicInteger.get() == 1;
    }
    @Test
    public void listenerTest1() throws Throwable {
        //
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        final String FirstEvent = "FirstEvent";
        final AtomicInteger atomicInteger = new AtomicInteger();
        final EventListener<EventContext> listener = new EventListener<EventContext>() {
            public void onEvent(String event, EventContext eventEC) throws Throwable {
                atomicInteger.incrementAndGet();
            }
        };
        //
        //
        assert ec.addListener(FirstEvent, listener);
        ec.fireSyncEvent(FirstEvent, ec);
        assert ec.clearListener(FirstEvent);
        //
        ec.fireSyncEvent(FirstEvent, ec);
        assert atomicInteger.get() == 1;
    }
    //
    @Test
    public void listenerTest2() throws Throwable {
        //
        StandardEventManager ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        //
        //
        assert !ec.pushListener(null, null);
        assert !ec.addListener(null, null);
        assert !ec.removeListener(null, null);
        assert !ec.clearListener(null);
        //
        assert ec.asyncTask((Runnable) null) == null;
        assert ec.asyncTask((Callable) null) == null;
        //
        assert !ec.asyncTask((Runnable) null, null);
        assert !ec.asyncTask((Callable) null, null);
        //
        ec.release();
    }
}