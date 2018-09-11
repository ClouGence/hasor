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
package test.net.hasor.core.binder;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.binder.ApiBinderInvocationHandler;
import net.hasor.core.binder.BinderHelper;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static net.hasor.core.AppContext.ContextEvent_Shutdown;
import static net.hasor.core.AppContext.ContextEvent_Started;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvocationHandlerTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    @Test
    public void apiBinderInvocationHandler_test() {
        Map<Class<?>, Object> supportMap = new HashMap<Class<?>, Object>();
        new ApiBinderInvocationHandler(supportMap);
        //
        supportMap.put(Object.class, new Object());
        new ApiBinderInvocationHandler(supportMap);
        //
        try {
            supportMap.put(ApiBinderInvocationHandler.class, null);
            new ApiBinderInvocationHandler(supportMap);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
    //
    @Test
    public void binderHelperTest() {
        final AtomicReference<Object> referenceStarted = new AtomicReference<Object>();
        final AtomicReference<Object> referenceShutdown = new AtomicReference<Object>();
        Answer<Object> answer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (ContextEvent_Started.equalsIgnoreCase(invocationOnMock.getArguments()[0].toString())) {
                    referenceStarted.set(invocationOnMock.getArguments()[1]);
                }
                if (ContextEvent_Shutdown.equalsIgnoreCase(invocationOnMock.getArguments()[0].toString())) {
                    referenceShutdown.set(invocationOnMock.getArguments()[1]);
                }
                return null;
            }
        };
        //
        EventContext event = PowerMockito.mock(EventContext.class);
        PowerMockito.doAnswer(answer).when(event).pushListener(anyString(), (EventListener<?>) anyObject());
        PowerMockito.doAnswer(answer).when(event).pushListener(anyString(), (EventListener<?>) anyObject());
        Environment mock = PowerMockito.mock(Environment.class);
        PowerMockito.when(mock.getEventContext()).thenReturn(event);
        //
        EmptyLifeModule module = (EmptyLifeModule) BinderHelper.onInstall(mock, new EmptyLifeModule());
        //
        assert referenceStarted.get() instanceof EventListener;
        assert referenceShutdown.get() instanceof EventListener;
        try {
            ((EventListener) referenceStarted.get()).onEvent(null, null);
            ((EventListener) referenceShutdown.get()).onEvent(null, null);
            assert module.isDoStart() && module.isDoStop();
        } catch (Throwable e) {
            e.printStackTrace();
            assert false;
        }
        //
        assert BinderHelper.onInstall(mock, null) == null;
    }
}