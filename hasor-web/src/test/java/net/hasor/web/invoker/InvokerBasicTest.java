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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.test.web.actions.async.ErrorAsyncAction;
import net.hasor.test.web.actions.async.MethodAsyncAction;
import net.hasor.test.web.actions.render.HtmlProduces;
import net.hasor.test.web.render.SimpleRenderEngine;
import net.hasor.web.AbstractTest;
import net.hasor.web.Mapping;
import net.hasor.web.WebApiBinder;
import net.hasor.web.binder.MappingDef;
import net.hasor.web.binder.OneConfig;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.anyString;

public class InvokerBasicTest extends AbstractTest {
    @Test
    public void contentType_1() throws Throwable {
        SimpleRenderEngine renderEngine = new SimpleRenderEngine();
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.addRender("html").toInstance(renderEngine);
            apiBinder.loadMappingTo(HtmlProduces.class);
        }, servlet30("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        //
        List<MappingDef> definitions = appContext.findBindingBean(MappingDef.class);
        assert definitions.size() == 2;
        final Set<String> responseType = new HashSet<>();
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.doAnswer((Answer<Void>) invocation -> {
            responseType.add(invocation.getArguments()[0].toString());
            return null;
        }).when(servletResponse).setContentType(anyString());
        //
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        {
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("post", new URL("http://www.hasor.net/abc.do")), servletResponse);
            caller.invoke(null);
            assert responseType.contains("test/html");
        }
        //
        {
            responseType.clear();
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("get", new URL("http://www.hasor.net/abc.do")), servletResponse);
            caller.invoke(null);
            assert responseType.contains("text/javacc_jj");
        }
    }

    /* 无任何匹配的请求，不报错 */
    @Test
    public void none_matching_test_1() throws Throwable {
        MethodAsyncAction action = new MethodAsyncAction();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abcefg.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        ExceuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        assert true;
        assert !action.isExecute();
    }

    @Test
    public void asyncInvocationWorker_test_1() {
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        final Method targetMethod = PowerMockito.mock(Method.class);
        //
        AsyncInvocationWorker worker = new AsyncInvocationWorker(asyncContext, targetMethod) {
            @Override
            public void doWork(Method method) {
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
    public void asyncInvocationWorker_test_2() {
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

    @Test
    public void asyncAction_test_1() throws Throwable {
        MethodAsyncAction action = new MethodAsyncAction();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet30("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        Object obj = new Object();
        action.getData().set(obj);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        ExceuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        assert action.getData().get() == obj;
    }

    @Test
    public void asyncAction_test_2() throws Throwable {
        MethodAsyncAction action = new MethodAsyncAction();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        Object obj = new Object();
        action.getData().set(obj);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        ExceuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        assert action.getData().get() != obj;
    }

    @Test
    public void error_action_test_async() throws Throwable {
        ErrorAsyncAction action = new ErrorAsyncAction();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet30("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        Object obj = new Object();
        action.getData().set(obj);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        ExceuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        try {
            caller.invoke(null).get();
            assert false;
        } catch (Exception e) {
            assert e.getCause() instanceof IllegalStateException;
            assert e.getCause().getMessage().equals("aaaa");
        } finally {
            assert action.getData().get() == obj; // 异步
        }
    }

    @Test
    public void error_action_test_sync() throws Throwable {
        ErrorAsyncAction action = new ErrorAsyncAction();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        Object obj = new Object();
        action.getData().set(obj);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        ExceuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        try {
            caller.invoke(null).get();
            assert false;
        } catch (Exception e) {
            assert e.getCause() instanceof IllegalStateException;
            assert e.getCause().getMessage().equals("aaaa");
        } finally {
            assert action.getData().get() != obj; // 同步
        }
    }

    @Test
    public void invokerSupplier_test_1() throws Throwable {
        AppContext appContext = PowerMockito.mock(AppContext.class);
        HttpServletRequest httpRequest = super.mockRequest("get", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"));
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        InvokerSupplier supplier = new InvokerSupplier(PowerMockito.mock(Mapping.class), appContext, httpRequest, httpResponse);
        //
        assert supplier.getHttpRequest() == httpRequest;
        assert supplier.getHttpResponse() == httpResponse;
        assert supplier.getAppContext() == appContext;
        //
        supplier.put("abc", "abc");
        assert "abc".equals(supplier.get("abc"));
        supplier.remove("abc");
        assert supplier.get("abc") == null;
        //
        supplier.put("key", "kv");
        assert "kv".equals(supplier.get("key"));
        supplier.lockKey("key");
        try {
            supplier.put("key", "111");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" is lock key.");
        }
        try {
            supplier.remove("key");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" is lock key.");
        }
        //
        Set<String> strings = supplier.keySet();
        assert strings.contains("key");
    }
}