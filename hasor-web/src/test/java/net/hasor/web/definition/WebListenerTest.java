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
import net.hasor.test.spi.TestHttpSessionListener;
import net.hasor.test.spi.TestServletContextListener;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
import java.util.EventListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class WebListenerTest {
    @Test
    public void webPluginTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends EventListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then((Answer<Object>) invocationOnMock -> {
            initCall.set(true);
            return new TestServletContextListener();
        });
        WebListenerDefinition definition = new WebListenerDefinition(bindInfo);
        //
        definition.setAppContext(appContext);
        definition.toString();
        definition.getWebListener(ServletContextListener.class).contextInitialized(null);
        definition.getWebListener(ServletContextListener.class).contextDestroyed(null);
        //
        assert initCall.get();
    }
    //
    @Test
    public void webPluginTest2() throws Throwable {
        //
        BindInfo<? extends EventListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((EventListener) appContext.getInstance(bindInfo)).thenReturn(new TestServletContextListener());
        WebListenerDefinition definition = new WebListenerDefinition(bindInfo);
        //
        //
        TestServletContextListener.resetCalls();
        assert !TestServletContextListener.isContextDestroyedCall();
        assert !TestServletContextListener.isContextInitializedCall();
        definition.setAppContext(appContext);
        definition.getWebListener(ServletContextListener.class).contextInitialized(null);
        definition.getWebListener(ServletContextListener.class).contextDestroyed(null);
        assert TestServletContextListener.isContextDestroyedCall();
        assert TestServletContextListener.isContextInitializedCall();
        //
    }
    //
    @Test
    public void webPluginTest3() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends TestHttpSessionListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then((Answer<Object>) invocationOnMock -> {
            initCall.set(true);
            return new TestHttpSessionListener();
        });
        WebListenerDefinition definition = new WebListenerDefinition(bindInfo);
        //
        definition.setAppContext(appContext);
        definition.toString();
        definition.getWebListener(HttpSessionListener.class).sessionCreated(null);
        definition.getWebListener(HttpSessionListener.class).sessionDestroyed(null);
        //
        assert initCall.get();
    }
    //
    @Test
    public void webPluginTest4() throws Throwable {
        //
        BindInfo<? extends TestHttpSessionListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((TestHttpSessionListener) appContext.getInstance(bindInfo)).thenReturn(new TestHttpSessionListener());
        WebListenerDefinition definition = new WebListenerDefinition(bindInfo);
        //
        //
        TestHttpSessionListener.resetCalls();
        assert !TestHttpSessionListener.isSessionCreatedCallCall();
        assert !TestHttpSessionListener.issSessionDestroyedCallCall();
        definition.setAppContext(appContext);
        definition.getWebListener(HttpSessionListener.class).sessionCreated(null);
        definition.getWebListener(HttpSessionListener.class).sessionDestroyed(null);
        assert TestHttpSessionListener.isSessionCreatedCallCall();
        assert TestHttpSessionListener.issSessionDestroyedCallCall();
        //
    }
}