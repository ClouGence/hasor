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
package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.definition.beans.TestServletContextListener;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class ContextListenerTest {
    @Test
    public void webPluginTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends ServletContextListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then((Answer<Object>) invocationOnMock -> {
            initCall.set(true);
            return new TestServletContextListener();
        });
        ContextListenerDefinition definition = new ContextListenerDefinition(bindInfo);
        //
        definition.init(appContext);
        definition.toString();
        definition.contextInitialized(null);
        definition.contextDestroyed(null);
        //
        assert initCall.get();
    }
    //
    @Test
    public void webPluginTest2() throws Throwable {
        //
        BindInfo<? extends ServletContextListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((ServletContextListener) appContext.getInstance(bindInfo)).thenReturn(new TestServletContextListener());
        ContextListenerDefinition definition = new ContextListenerDefinition(bindInfo);
        //
        //
        TestServletContextListener.resetCalls();
        assert !TestServletContextListener.isContextDestroyedCall();
        assert !TestServletContextListener.isContextInitializedCall();
        definition.init(appContext);
        definition.contextInitialized(null);
        definition.contextDestroyed(null);
        assert TestServletContextListener.isContextDestroyedCall();
        assert TestServletContextListener.isContextInitializedCall();
        //
    }
}