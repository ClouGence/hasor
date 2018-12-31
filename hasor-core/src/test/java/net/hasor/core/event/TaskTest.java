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
import net.hasor.utils.future.FutureCallback;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class TaskTest {
    @Test
    public void listenerTest0() throws Throwable {
        //
        EventContext ec = new StandardEventManager(20, "TestEvent", Thread.currentThread().getContextClassLoader());
        final AtomicInteger runInteger = new AtomicInteger(0);
        final AtomicInteger completedInteger = new AtomicInteger(0);
        final AtomicInteger failedInteger = new AtomicInteger(0);
        //
        FutureCallback<Void> futureCallback = new FutureCallback<Void>() {
            @Override
            public void completed(Void result) {
                completedInteger.incrementAndGet();
            }
            @Override
            public void failed(Throwable ex) {
                failedInteger.incrementAndGet();
            }
        };
        class TestRunnable implements Runnable {
            private boolean b;
            public TestRunnable(boolean b) {
                this.b = b;
            }
            @Override
            public void run() {
                runInteger.incrementAndGet();
                if (!b) {
                    throw new RuntimeException();
                }
            }
        }
        class TestCallable implements Callable<Void> {
            private boolean b;
            public TestCallable(boolean b) {
                this.b = b;
            }
            @Override
            public Void call() throws Exception {
                runInteger.incrementAndGet();
                if (!b) {
                    throw new RuntimeException();
                }
                return null;
            }
        }
        //
        //
        ec.asyncTask(new TestRunnable(true));
        ec.asyncTask(new TestRunnable(false), futureCallback);
        ec.asyncTask(new TestCallable(true));
        ec.asyncTask(new TestCallable(false), futureCallback);
        //
        //
        //
        Thread.sleep(500);
        assert runInteger.get() == 4;
        assert completedInteger.get() == 0;
        assert failedInteger.get() == 2;
    }
}