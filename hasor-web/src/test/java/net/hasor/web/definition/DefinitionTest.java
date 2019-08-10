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
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerFilter;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//
public class DefinitionTest {
    @Test
    public void baseicDefineTest() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((Filter) appContext.getInstance(bindInfo)).thenReturn(new TestCallerFilter());
        //
        //
        Map<String, String> initParams = new HashMap<>();
        initParams.put("arg1s", "1");
        initParams.put("args2", "2");
        UriPatternMatcher uriPatternMatcher = UriPatternType.get(UriPatternType.SERVLET, "/servlet");
        FilterDefinition filterDefine = new FilterDefinition(123, "abc", uriPatternMatcher, bindInfo, initParams);
        //
        filterDefine.toString();
        assert "abc".equals(filterDefine.getPattern());
        assert 123 == filterDefine.getIndex();
        assert filterDefine.getUriPatternType() == uriPatternMatcher.getPatternType();
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(initParams, appContext));
        //
        Invoker mock = PowerMockito.mock(Invoker.class);
        PowerMockito.when(mock.getRequestPath()).thenReturn("/servlet");
        assert filterDefine.matchesInvoker(mock);
        PowerMockito.when(mock.getRequestPath()).thenReturn("/servlet22");
        assert !filterDefine.matchesInvoker(mock);
        //
        new FilterDefinition(123, "abc", uriPatternMatcher, bindInfo, null);
    }

    @Test
    public void filterDefineTest1() throws Throwable {
        TestCallerFilter callerFilter = new TestCallerFilter();
        BindInfo<Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(callerFilter);
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(null, appContext));
        filterDefine.beanCreated(callerFilter, bindInfo);
        assert TestCallerFilter.isInitCall();
        assert filterDefine.getTarget() == callerFilter;
        //
        filterDefine.destroy();
        assert !TestCallerFilter.isDestroyCall(); // 没有init 不会调用 destroy
    }

    @Test
    public void filterDefineTest2() throws Throwable {
        TestDoNextCallerFilter nextCallerFilter = new TestDoNextCallerFilter();
        BindInfo<Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(nextCallerFilter);
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        filterDefine.beanCreated(nextCallerFilter, bindInfo);
        assert filterDefine.getTarget() == nextCallerFilter;
        //
        assert TestCallerFilter.isInitCall();
        filterDefine.doInvoke(mockInvoker, invoker -> null);
        assert TestCallerFilter.isDoCall();
        //
        filterDefine.destroy();
        assert TestCallerFilter.isDestroyCall();
    }

    @Test
    public void filterDefineTest3() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((Filter) appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new IOException("TEST_ERROR");
            });
            assert false;
        } catch (IOException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new ServletException("TEST_ERROR");
            });
            assert false;
        } catch (ServletException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new Exception("TEST_ERROR");
            });
            assert false;
        } catch (RuntimeException t) {
            assert t.getCause().getMessage().equals("TEST_ERROR");
            assert t.getCause() instanceof Exception;
        } catch (Throwable throwable) {
            assert false;
        }
    }

    @Test
    public void invokerDefineTest1() throws Throwable {
        TestCallerFilter callerFilter = new TestCallerFilter();
        //
        BindInfo<InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((Filter) appContext.getInstance(bindInfo)).thenReturn(callerFilter);
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.destroy();
        assert !TestCallerFilter.isDestroyCall(); // 没有init 不会调用 destroy
        filterDefine.init(new InvokerMapConfig(null, appContext));
        filterDefine.beanCreated(callerFilter, bindInfo);
        assert callerFilter == filterDefine.getTarget();
        //
        assert TestCallerFilter.isInitCall();
    }

    @Test
    public void invokerDefineTest2() throws Throwable {
        //
        TestDoNextCallerFilter nextCallerFilter = new TestDoNextCallerFilter();
        BindInfo<InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((Filter) appContext.getInstance(bindInfo)).thenReturn(nextCallerFilter);
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        filterDefine.beanCreated(nextCallerFilter, bindInfo);
        assert nextCallerFilter == filterDefine.getTarget();
        //
        assert TestCallerFilter.isInitCall();
        filterDefine.doInvoke(mockInvoker, invoker -> null);
        assert TestCallerFilter.isDoCall();
        //
        filterDefine.destroy();
        assert TestCallerFilter.isDestroyCall();
    }

    @Test
    public void invokerDefineTest3() throws Throwable {
        //
        BindInfo<? extends InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when((Filter) appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new IOException("TEST_ERROR");
            });
            assert false;
        } catch (IOException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new ServletException("TEST_ERROR");
            });
            assert false;
        } catch (ServletException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, invoker -> {
                throw new Exception("TEST_ERROR");
            });
            assert false;
        } catch (Throwable e) {
            assert e.getMessage().equals("TEST_ERROR");
            assert e instanceof Exception;
        }
    }
}