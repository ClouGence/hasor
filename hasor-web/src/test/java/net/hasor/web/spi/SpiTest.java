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
package net.hasor.web.spi;
import net.hasor.core.AppContext;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.test.actions.mapping.MappingAction;
import net.hasor.test.actions.servlet.SimpleServlet;
import net.hasor.test.invoker.TestInvoker;
import net.hasor.test.invoker.TestInvoker2;
import net.hasor.test.spi.TestHttpRequestListener;
import net.hasor.test.spi.TestHttpSessionListener;
import net.hasor.test.spi.TestMappingDiscoverer;
import net.hasor.test.spi.TestServletContextListener;
import net.hasor.web.AbstractTest;
import net.hasor.web.Invoker;
import net.hasor.web.Mapping;
import net.hasor.web.MimeType;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.invoker.InvokerContext;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpiTest extends AbstractTest {
    @Test
    public void spi_1() throws Throwable {
        TestMappingDiscoverer mappingDiscoverer = new TestMappingDiscoverer();
        //
        AppContext appContext = buildWebAppContext(apiBinder -> {
            apiBinder.bindSpiListener(MappingDiscoverer.class, mappingDiscoverer);
            //
            apiBinder.jeeServlet("/*.do").with(1, new SimpleServlet());
            apiBinder.jeeServlet("/*abc.do").with(0, new SimpleServlet());
            //
            apiBinder.loadMappingTo(MappingAction.class);
        }, servlet30("/"), LoadModule.Web);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        ArrayList<Mapping> discovererData = mappingDiscoverer.getMappings();
        assert discovererData.size() == 3;
        assert discovererData.get(0).getMappingTo().equals("/*abc.do");
        assert discovererData.get(1).getMappingTo().equals("/mappingto_b.do");
        assert discovererData.get(2).getMappingTo().equals("/*.do");
    }

    @Test
    public void spi_2() {
        TestHttpSessionListener sessionListener1 = new TestHttpSessionListener();
        TestHttpSessionListener sessionListener2 = new TestHttpSessionListener();
        TestServletContextListener contextListener1 = new TestServletContextListener();
        TestServletContextListener contextListener2 = new TestServletContextListener();
        TestHttpRequestListener requestListener1 = new TestHttpRequestListener();
        TestHttpRequestListener requestListener2 = new TestHttpRequestListener();
        //
        AppContext appContext = buildWebAppContext(apiBinder -> {
            apiBinder.bindSpiListener(HttpSessionListener.class, sessionListener1);
            apiBinder.bindSpiListener(HttpSessionListener.class, sessionListener2);
            apiBinder.bindSpiListener(ServletContextListener.class, contextListener1);
            apiBinder.bindSpiListener(ServletContextListener.class, contextListener2);
            apiBinder.bindSpiListener(ServletRequestListener.class, requestListener1);
            apiBinder.bindSpiListener(ServletRequestListener.class, requestListener2);
        }, servlet30("/"), LoadModule.Web);
        //
        SpiTrigger spiTrigger = appContext.getInstance(SpiTrigger.class);
        //
        HttpSessionEvent event_1 = PowerMockito.mock(HttpSessionEvent.class);
        ServletContextEvent event_2 = PowerMockito.mock(ServletContextEvent.class);
        ServletRequestEvent event_3 = PowerMockito.mock(ServletRequestEvent.class);
        //
        spiTrigger.callSpi(HttpSessionListener.class, listener -> listener.sessionCreated(event_1));
        spiTrigger.callSpi(HttpSessionListener.class, listener -> listener.sessionDestroyed(event_1));
        spiTrigger.callSpi(ServletContextListener.class, listener -> listener.contextInitialized(event_2));
        spiTrigger.callSpi(ServletContextListener.class, listener -> listener.contextDestroyed(event_2));
        spiTrigger.callSpi(ServletRequestListener.class, listener -> listener.requestInitialized(event_3));
        spiTrigger.callSpi(ServletRequestListener.class, listener -> listener.requestDestroyed(event_3));
        //
        assert sessionListener1.isSessionCreated();
        assert sessionListener2.isSessionCreated();
        assert sessionListener1.isSessionDestroyed();
        assert sessionListener2.isSessionDestroyed();
        //
        assert contextListener1.isContextInitialized();
        assert contextListener2.isContextInitialized();
        assert contextListener1.isContextDestroyed();
        assert contextListener2.isContextDestroyed();
        //
        assert requestListener1.isRequestInitialized();
        assert requestListener2.isRequestInitialized();
        assert requestListener1.isRequestDestroyed();
        assert requestListener2.isRequestDestroyed();
    }

    @Test
    public void spi_3_1() throws Throwable {
        //
        AppContext appContext = buildWebAppContext("/net_hasor_web_invoker/root-creater.xml", apiBinder -> {
            //
        }, servlet30("/"), LoadModule.Web);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
        Invoker newInvoker = invokerContext.newInvoker(null, request, response);
        //
        assert newInvoker instanceof TestInvoker;
        assert newInvoker instanceof TestInvoker2;
        assert newInvoker instanceof MimeType;
    }

    @Test
    public void spi_3_2() throws Throwable {
        //
        AppContext appContext = buildWebAppContext(apiBinder -> {
            //
        }, servlet30("/"), LoadModule.Web);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        HttpServletResponse response = PowerMockito.mock(HttpServletResponse.class);
        Invoker newInvoker = invokerContext.newInvoker(null, request, response);
        //
        assert !(newInvoker instanceof TestInvoker);
        assert !(newInvoker instanceof TestInvoker2);
        assert newInvoker instanceof MimeType;
    }
}