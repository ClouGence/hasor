package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerData;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.beans.TestServlet;
import net.hasor.web.invoker.call.AsyncCallAction;
import net.hasor.web.invoker.call.SyncCallAction;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
//
public class InvokerCallerTest extends AbstractWeb30BinderDataTest {
    protected Invoker newInvoker(String mappingTo, final String httpMethod, final AppContext appContext, final boolean mockRequest) {
        Invoker invoker = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker.getRequestPath()).thenReturn(mappingTo);
        PowerMockito.when(invoker.getHttpRequest()).thenAnswer(new Answer<HttpServletRequest>() {
            @Override
            public HttpServletRequest answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (mockRequest) {
                    HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
                    PowerMockito.when(servletRequest.getMethod()).thenReturn(httpMethod);
                    return servletRequest;
                } else {
                    return appContext.getInstance(HttpServletRequest.class);
                }
            }
        });
        PowerMockito.when(invoker.getHttpResponse()).thenAnswer(new Answer<HttpServletResponse>() {
            @Override
            public HttpServletResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (mockRequest) {
                    return PowerMockito.mock(HttpServletResponse.class);
                } else {
                    return appContext.getInstance(HttpServletResponse.class);
                }
            }
        });
        PowerMockito.when(invoker.getAppContext()).thenReturn(appContext);
        //
        final Map<String, Object> context = new HashMap<String, Object>();
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return context.put((String) invocation.getArguments()[0], invocation.getArguments()[1]);
            }
        }).when(invoker).put(anyString(), anyObject());
        PowerMockito.when(invoker.get(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return context.get(invocation.getArguments()[0]);
            }
        });
        return invoker;
    }
    @Test
    public void basicTest1() throws Throwable {
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                //
                apiBinder.tryCast(WebApiBinder.class).jeeServlet("/abc.do").with(TestServlet.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        assert definitions.size() == 1;
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                atomicBoolean.set(true);
            }
        };
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        //
        TestServlet.resetInit();
        atomicBoolean.set(false);
        assert !atomicBoolean.get();
        assert !TestServlet.isStaticCall();
        Invoker invoker1 = newInvoker("/abc.do", "GET", appContext, true);
        Future<Object> invoke1 = caller.invoke(invoker1, chain);
        assert TestServlet.isStaticCall();
        assert !atomicBoolean.get();
        assert invoke1.get() == null;
        //
        //
        TestServlet.resetInit();
        atomicBoolean.set(false);
        assert !atomicBoolean.get();
        assert !TestServlet.isStaticCall();
        Invoker invoker2 = newInvoker("/hello.do", "GET", appContext, true);
        Future<Object> invoke2 = caller.invoke(invoker2, chain);
        assert !TestServlet.isStaticCall();
        assert atomicBoolean.get();
        assert invoke2.get() == null;
    }
    @Test
    public void basicTest2() throws Throwable {
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                //
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        assert definitions.size() == 1;
        final AtomicBoolean beforeFilterBoolean = new AtomicBoolean(false);
        final AtomicBoolean afterFilterBoolean = new AtomicBoolean(false);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, new WebPluginCaller() {
            @Override
            public void beforeFilter(Invoker invoker, InvokerData info) {
                assert info.getMappingTo().getMappingTo().startsWith("/sync.do");
                assert info.targetMethod().getName().equals("execute");
                assert info.targetMethod().getDeclaringClass() == SyncCallAction.class;
                assert info.getParameters().length == 0;
                beforeFilterBoolean.set(true);
            }
            @Override
            public void afterFilter(Invoker invoker, InvokerData info) {
                assert info.getMappingTo().getMappingTo().startsWith("/sync.do");
                assert info.targetMethod().getName().equals("execute");
                assert info.targetMethod().getDeclaringClass() == SyncCallAction.class;
                assert info.getParameters().length == 0;
                afterFilterBoolean.set(true);
            }
        });
        //
        SyncCallAction.resetInit();
        beforeFilterBoolean.set(false);
        afterFilterBoolean.set(false);
        assert !beforeFilterBoolean.get();
        assert !afterFilterBoolean.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker1 = newInvoker("/sync.do", "POST", appContext, true);
        caller.invoke(invoker1, null).get();
        assert beforeFilterBoolean.get();
        assert afterFilterBoolean.get();
        assert SyncCallAction.isStaticCall();
        //
        SyncCallAction.resetInit();
        beforeFilterBoolean.set(false);
        afterFilterBoolean.set(false);
        assert !beforeFilterBoolean.get();
        assert !afterFilterBoolean.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker2 = newInvoker("/abcc.do", "GET", appContext, true);
        caller.invoke(invoker2, null).get();
        assert !beforeFilterBoolean.get();
        assert !afterFilterBoolean.get();
        assert !SyncCallAction.isStaticCall();
    }
    //
    @Test
    public void asyncInvokeTest1() throws Throwable {
        final HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.when(servletRequest.getMethod()).thenReturn("post");
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncCall.set(true);
                ((Runnable) invocationOnMock.getArguments()[0]).run();
                return null;
            }
        }).when(asyncContext).start((Runnable) anyObject());
        //
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
                apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(AsyncCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        //
        AsyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !AsyncCallAction.isStaticCall();
        Invoker invoker = newInvoker("/async.do", "post", appContext, false);
        Object o = caller.invoke(invoker, null).get();
        //
        assert asyncCall.get();
        assert AsyncCallAction.isStaticCall();
        assert "CALL".equals(o);
    }
    //
    @Test
    public void asyncInvokeTest2() throws Throwable {
        final HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.when(servletRequest.getMethod()).thenReturn("get");
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncCall.set(true);
                ((Runnable) invocationOnMock.getArguments()[0]).run();
                return null;
            }
        }).when(asyncContext).start((Runnable) anyObject());
        //
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
                apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(AsyncCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        //
        AsyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !AsyncCallAction.isStaticCall();
        Invoker invoker = newInvoker("/async.do", "get", appContext, false);
        try {
            caller.invoke(invoker, null).get();
            assert false;
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            assert cause instanceof NullPointerException && cause.getMessage().equals("CALL");
        }
        //
        assert asyncCall.get();
        assert AsyncCallAction.isStaticCall();
    }
    //
    @Test
    public void syncInvokeTest1() throws Throwable {
        final HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.when(servletRequest.getMethod()).thenReturn("post");
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncCall.set(true);
                ((Runnable) invocationOnMock.getArguments()[0]).run();
                return null;
            }
        }).when(asyncContext).start((Runnable) anyObject());
        //
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
                apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        //
        SyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker = newInvoker("/sync.do", "post", appContext, false);
        Object o = caller.invoke(invoker, null).get();
        //
        assert !asyncCall.get();
        assert SyncCallAction.isStaticCall();
        assert "CALL".equals(o);
    }
    //
    @Test
    public void syncInvokeTest2() throws Throwable {
        final HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        final HttpServletResponse httpServletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.when(servletRequest.getMethod()).thenReturn("get");
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                asyncCall.set(true);
                ((Runnable) invocationOnMock.getArguments()[0]).run();
                return null;
            }
        }).when(asyncContext).start((Runnable) anyObject());
        //
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
                apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        //
        SyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker = newInvoker("/sync.do", "get", appContext, false);
        try {
            caller.invoke(invoker, null).get();
            assert false;
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            assert cause instanceof NullPointerException && cause.getMessage().equals("CALL");
        }
        //
        assert !asyncCall.get();
        assert SyncCallAction.isStaticCall();
    }
}