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
package net.hasor.web.startup;
import net.hasor.core.Module;
import net.hasor.test.actions.args.QueryArgsAction;
import net.hasor.test.actions.throwerr.*;
import net.hasor.web.AbstractTest;
import net.hasor.web.WebModule;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.spi.AfterResponseListener;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilterTest extends AbstractTest {
    @Test
    public void basic_test_1() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-root-module", StartModule.class.getName());
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        RuntimeListener runtimeListener = new RuntimeListener();
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("abc", () -> RuntimeListener.getAppContext(servletContext)));
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        runtimeFilter.destroy();
        runtimeFilter.destroy();
        assert true;
    }

    @Test
    public void basic_test_2() throws Throwable {
        ServletContext servletContext = servletInitParams(servlet25("/"), new HashMap<>());
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.setEncodingCharacter("iso-8859-1", "utf-8");
                    apiBinder.filter("/*").through((invoker, chain) -> {
                        reference.set(new HashMap<String, String>() {{
                            put("request", invoker.getHttpRequest().getCharacterEncoding());
                            put("response", invoker.getHttpResponse().getCharacterEncoding());
                        }});
                        return chain.doNext(invoker);
                    });
                    apiBinder.mappingTo("/query_param.do").with(QueryArgsAction.class);
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        mockCharacterEncoding(servletRequest, servletResponse);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        runtimeFilter.doFilter(servletRequest, servletResponse, chain);
        //
        Object o = reference.get();
        assert o instanceof HashMap;
        assert ((HashMap) o).get("request").equals("iso-8859-1");
        assert ((HashMap) o).get("response").equals("utf-8");
    }

    @Test
    public void action_test_1() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.mappingTo("/query_param.do").with(QueryArgsAction.class);
                    apiBinder.bindSpiListener(AfterResponseListener.class, (appContext, request, response, invokerResult) -> {
                        reference.set(invokerResult);
                    });
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        runtimeFilter.doFilter(servletRequest, servletResponse, chain);
        //
        Object o = reference.get();
        assert o instanceof Map;
        assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
        assert ((Integer) ((Map) o).get("intParam")) == 321;
        assert ((String) ((Map) o).get("strParam")).equals("5678");
        assert ((String) ((Map) o).get("eptParam")) == null;
        runtimeFilter.destroy();
        assert true;
    }

    @Test
    public void throw_ioerror_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.mappingTo("/query_param.do").with(ThrowIOErrorAction.class);
                    apiBinder.bindSpiListener(AfterResponseListener.class, (appContext, request, response, invokerResult) -> {
                        reference.set(invokerResult);
                    });
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        try {
            runtimeFilter.doFilter(servletRequest, servletResponse, chain);
            assert false;
        } catch (Exception e) {
            assert e instanceof IOException;
            assert e.getMessage().equalsIgnoreCase("IOException - doPost");
        }
        runtimeFilter.destroy();
        assert true;
    }

    @Test
    public void throw_runtimeerror_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.mappingTo("/query_param.do").with(ThrowRuntimeErrorAction.class);
                    apiBinder.bindSpiListener(AfterResponseListener.class, (appContext, request, response, invokerResult) -> {
                        reference.set(invokerResult);
                    });
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        try {
            runtimeFilter.doFilter(servletRequest, servletResponse, chain);
            assert false;
        } catch (Exception e) {
            assert e instanceof RuntimeException;
            assert e.getMessage().equalsIgnoreCase("RuntimeException - doPost");
        }
        runtimeFilter.destroy();
        assert true;
    }

    @Test
    public void throw_servleterror_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.mappingTo("/query_param.do").with(ThrowServletErrorAction.class);
                    apiBinder.bindSpiListener(AfterResponseListener.class, (appContext, request, response, invokerResult) -> {
                        reference.set(invokerResult);
                    });
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        try {
            runtimeFilter.doFilter(servletRequest, servletResponse, chain);
            assert false;
        } catch (Exception e) {
            assert e instanceof ServletException;
            assert e.getMessage().equalsIgnoreCase("ServletException - doPost");
        }
        runtimeFilter.destroy();
        assert true;
    }

    @Test
    public void throw_exceptionerror_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        AtomicReference<Object> reference = new AtomicReference<>();
        RuntimeListener runtimeListener = new RuntimeListener() {
            protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
                return (WebModule) apiBinder -> {
                    apiBinder.mappingTo("/query_param.do").with(ThrowExceptionErrorAction.class);
                    apiBinder.bindSpiListener(AfterResponseListener.class, (appContext, request, response, invokerResult) -> {
                        reference.set(invokerResult);
                    });
                };
            }
        };
        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
        RuntimeFilter runtimeFilter = new RuntimeFilter();
        runtimeFilter.init(new OneConfig("", () -> RuntimeListener.getAppContext(servletContext)));
        //
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        FilterChain chain = PowerMockito.mock(FilterChain.class);
        try {
            runtimeFilter.doFilter(servletRequest, servletResponse, chain);
            assert false;
        } catch (Exception e) {
            assert e instanceof ServletException;
            assert e.getCause() instanceof MyException;
            assert e.getCause().getMessage().equalsIgnoreCase("MyException - doPost");
        }
        runtimeFilter.destroy();
        assert true;
    }
}