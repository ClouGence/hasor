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
package net.hasor.web.listener;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.definition.beans.TestHttpSessionListener;
import net.hasor.web.definition.beans.TestServletContextListener;
import net.hasor.web.invoker.AbstractWeb30BinderDataTest;
import net.hasor.web.invoker.params.QueryCallAction;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
//
public class ListenerTest extends AbstractWeb30BinderDataTest {
    @Test
    public void chainTest1() throws Throwable {
        ServletContextEvent contextEvent = PowerMockito.mock(ServletContextEvent.class);
        HttpSessionEvent sessionEvent = PowerMockito.mock(HttpSessionEvent.class);
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
            apiBinder.bindType(String.class).idWith("abc").toInstance("abcdefg");
            //
            apiBinder.addWebListener(new TestServletContextListener());
            apiBinder.addWebListener(new TestHttpSessionListener());
        });
        //
        ManagedListenerPipeline pipeline = new ManagedListenerPipeline();
        //
        TestServletContextListener.resetCalls();
        TestHttpSessionListener.resetCalls();
        assert !TestServletContextListener.isContextInitializedCall();
        assert !TestServletContextListener.isContextDestroyedCall();
        assert !TestHttpSessionListener.isSessionCreatedCallCall();
        assert !TestHttpSessionListener.issSessionDestroyedCallCall();
        //
        pipeline.contextInitialized(contextEvent);
        pipeline.sessionCreated(sessionEvent);
        pipeline.sessionDestroyed(sessionEvent);
        pipeline.contextDestroyed(contextEvent);
        assert !TestServletContextListener.isContextInitializedCall();
        assert !TestServletContextListener.isContextDestroyedCall();
        assert !TestHttpSessionListener.isSessionCreatedCallCall();
        assert !TestHttpSessionListener.issSessionDestroyedCallCall();
        //
        pipeline.init(appContext);
        pipeline.init(appContext);
        //
        pipeline.contextInitialized(contextEvent);
        pipeline.sessionCreated(sessionEvent);
        pipeline.sessionDestroyed(sessionEvent);
        pipeline.contextDestroyed(contextEvent);
        //
        assert TestServletContextListener.isContextInitializedCall();
        assert TestServletContextListener.isContextDestroyedCall();
        assert TestHttpSessionListener.isSessionCreatedCallCall();
        assert TestHttpSessionListener.issSessionDestroyedCallCall();
    }
}