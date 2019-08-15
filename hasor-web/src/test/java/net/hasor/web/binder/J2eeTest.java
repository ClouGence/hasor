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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.test.actions.servlet.SimpleServlet;
import net.hasor.test.filters.SimpleFilter;
import net.hasor.web.AbstractTest;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.anyObject;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class J2eeTest extends AbstractTest {
    @Test
    public void j2eeTest_1() throws Throwable {
        SimpleFilter j2eeFilter = new SimpleFilter();
        J2eeFilterAsFilter asFilter = new J2eeFilterAsFilter(() -> j2eeFilter);
        OneConfig oneConfig1 = new OneConfig("test", () -> PowerMockito.mock(AppContext.class)) {{
            put("config_a", "a");
        }};
        OneConfig oneConfig2 = new OneConfig("test", () -> PowerMockito.mock(AppContext.class)) {{
            put("config_a", "aa");
        }};
        //
        assert !j2eeFilter.isInit();
        asFilter.init((InvokerConfig) oneConfig1);
        asFilter.init((InvokerConfig) oneConfig2);
        assert j2eeFilter.isInit();
        assert j2eeFilter.getConfig().getInitParameter("config_a").equals("aa");
        //
        assert !j2eeFilter.isDestroy();
        asFilter.destroy();
        asFilter.destroy();
        assert j2eeFilter.isDestroy();
        //
        assert !j2eeFilter.isDoCall();
        asFilter.doInvoke(PowerMockito.mock(Invoker.class), PowerMockito.mock(InvokerChain.class));
        assert j2eeFilter.isDoCall();
    }

    @Test
    public void j2eeTest_2() throws Throwable {
        AtomicInteger index = new AtomicInteger(0);
        Exception[] exceptions = new Exception[] {  //
                new IOException("x"),               //
                new ServletException("x"),          //
                new InvocationTargetException(null) //
        };
        InvokerChain chain = PowerMockito.mock(InvokerChain.class);
        PowerMockito.when(chain.doNext(anyObject())).thenAnswer(invocationOnMock -> {
            throw exceptions[index.get()];
        });
        //
        //
        SimpleFilter j2eeFilter = new SimpleFilter();
        J2eeFilterAsFilter asFilter = new J2eeFilterAsFilter(() -> j2eeFilter);
        //
        try {
            index.set(0);
            asFilter.doInvoke(PowerMockito.mock(Invoker.class), chain);
            assert false;
        } catch (Exception e) {
            assert e == exceptions[0];
        }
        try {
            index.set(1);
            asFilter.doInvoke(PowerMockito.mock(Invoker.class), chain);
            assert false;
        } catch (Exception e) {
            assert e == exceptions[1];
        }
        try {
            index.set(2);
            asFilter.doInvoke(PowerMockito.mock(Invoker.class), chain);
            assert false;
        } catch (Exception e) {
            assert e instanceof RuntimeException;
            assert e.getCause() == exceptions[2];
        }
    }

    @Test
    public void j2eeTest_3() throws Throwable {
        OneConfig oneConfig = new OneConfig("test", () -> PowerMockito.mock(AppContext.class)) {{
            put("config_a", "a");
        }};
        SimpleServlet j2eeServlet = new SimpleServlet();
        //
        J2eeServletAsMapping mapping = new J2eeServletAsMapping(oneConfig, () -> j2eeServlet);
        assert mapping.getTarget().get() == j2eeServlet;
        assert !j2eeServlet.isInit();
        assert !j2eeServlet.isDoCall();
        //
        assert mapping.getInitParams().getInitParameter("config_a").equals("a");
        assert mapping.getInitParams().getServletName().equals("test");
        assert j2eeServlet.getConfig() == null;
        //
        AppContext appContext = Hasor.create().asCore().build();
        Invoker invoker = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker.getAppContext()).thenReturn(appContext);
        //
        try {
            mapping.doInvoke(PowerMockito.mock(HttpServletRequest.class), PowerMockito.mock(HttpServletResponse.class));
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("this Servlet uninitialized.");
        }
        //
        mapping.initController(invoker);
        mapping.initController(invoker);
        assert j2eeServlet.isInit();
        assert j2eeServlet.getConfig().getInitParameter("config_a").equals("a");
        assert !j2eeServlet.isDestroy();
        //
        mapping.doInvoke(PowerMockito.mock(HttpServletRequest.class), PowerMockito.mock(HttpServletResponse.class));
        assert j2eeServlet.isDoCall();
        //
        appContext.shutdown();
        assert j2eeServlet.isDestroy();
        mapping.destroy();
        assert j2eeServlet.isDestroy();
    }
}