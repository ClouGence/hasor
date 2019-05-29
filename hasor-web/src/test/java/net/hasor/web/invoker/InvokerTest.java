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
package net.hasor.web.invoker;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.AsyncContext;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class InvokerTest {
    @Test
    public void asyncInvocationWorkerTest() throws Throwable {
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        final Method targetMethod = PowerMockito.mock(Method.class);
        //
        AsyncInvocationWorker worker = new AsyncInvocationWorker(asyncContext, targetMethod) {
            @Override
            public void doWork(Method method) throws Throwable {
                assert method == targetMethod;
            }
            @Override
            public void doWorkWhenError(Method targetMethod, Throwable e) {
                assert false;
            }
        };
        //
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            atomicBoolean.set(true);
            return null;
        }).when(asyncContext).complete();
        //
        worker.run();
        assert atomicBoolean.get();
    }
    @Test
    public void errorAsyncInvocationWorkerTest() throws Throwable {
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        final Method targetMethod = PowerMockito.mock(Method.class);
        final Exception error = new Exception();
        //
        AsyncInvocationWorker worker = new AsyncInvocationWorker(asyncContext, targetMethod) {
            @Override
            public void doWork(Method method) throws Throwable {
                throw error;
            }
            @Override
            public void doWorkWhenError(Method targetMethod, Throwable e) {
                assert error == e;
            }
        };
        //
        worker.run();
    }
}