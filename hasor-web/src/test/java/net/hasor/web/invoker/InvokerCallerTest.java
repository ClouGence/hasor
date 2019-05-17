package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerFilter;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.beans.TestServlet;
import net.hasor.web.invoker.call.AsyncCallAction;
import net.hasor.web.invoker.call.SyncCallAction;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Matchers.anyObject;
//
public class InvokerCallerTest extends AbstractWeb30BinderDataTest {
    @Test
    public void basicTest1() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            //
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/abc.do").with(TestServlet.class);
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        assert definitions.size() == 1;
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        FilterChain chain = (request, response) -> atomicBoolean.set(true);
        //
        TestServlet.resetInit();
        atomicBoolean.set(false);
        assert !atomicBoolean.get();
        assert !TestServlet.isStaticCall();
        Invoker invoker1 = newInvoker(definitions.get(0), mockRequest("GET", new URL("http://www.hasor.net/abc.do"), appContext), appContext);
        Future<Object> invoke1 = new InvokerCaller(() -> invoker1, null).invoke(chain);
        assert TestServlet.isStaticCall();
        assert !atomicBoolean.get();
        assert invoke1.get() == null;
        //
        //
        TestServlet.resetInit();
        atomicBoolean.set(false);
        assert !atomicBoolean.get();
        assert !TestServlet.isStaticCall();
        Invoker invoker2 = newInvoker(definitions.get(0), mockRequest("GET", new URL("http://www.hasor.net/hello.do"), appContext), appContext);
        Future<Object> invoke2 = new InvokerCaller(() -> invoker2, null).invoke(chain);
        assert !TestServlet.isStaticCall();
        assert atomicBoolean.get();
        assert invoke2.get() == null;
    }
    @Test
    public void basicTest2() throws Throwable {
        final InvokerFilter webPluginCaller = (invoker, chain) -> {
            Method targetMethod = invoker.ownerMapping().findMethod(invoker.getHttpRequest());
            try {
                assert targetMethod.getName().equals("execute");
                assert targetMethod.getDeclaringClass() == SyncCallAction.class;
                assert targetMethod.getParameters().length == 0;
                return chain.doNext(invoker);
            } finally {
                assert targetMethod.getName().equals("execute");
                assert targetMethod.getDeclaringClass() == SyncCallAction.class;
                assert targetMethod.getParameters().length == 0;
            }
        };
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            //
            apiBinder.filter("/*").through(webPluginCaller);
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        assert definitions.size() == 1;
        //
        SyncCallAction.resetInit();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker1 = newInvoker(definitions.get(0), mockRequest("POST", new URL("http://www.hasor.net/sync.do"), appContext), appContext);
        new InvokerCaller(() -> invoker1, null).invoke(null).get();
        assert SyncCallAction.isStaticCall();
        //
        SyncCallAction.resetInit();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker2 = newInvoker(definitions.get(0), mockRequest("GET", new URL("http://www.hasor.net/abcc.do"), appContext), appContext);
        new InvokerCaller(() -> invoker2, null).invoke(null).get();
        assert !SyncCallAction.isStaticCall();
    }
    //
    @Test
    public void asyncInvokeTest1() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(AsyncCallAction.class);
        });
        //
        final HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/async.do"), appContext);
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            asyncCall.set(true);
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            return null;
        }).when(asyncContext).start(anyObject());
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        //
        AsyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !AsyncCallAction.isStaticCall();
        Invoker invoker = newInvoker(definitions.get(0), servletRequest, appContext);
        Object o = new InvokerCaller(() -> invoker, null).invoke(null).get();
        //
        assert asyncCall.get();
        assert AsyncCallAction.isStaticCall();
        assert "CALL".equals(o);
    }
    //
    @Test
    public void asyncInvokeTest2() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(AsyncCallAction.class);
        });
        //
        final HttpServletRequest servletRequest = mockRequest("get", new URL("http://www.hasor.net/async.do"), appContext);
        final AtomicBoolean asyncCall = new AtomicBoolean(false);
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        PowerMockito.when(servletRequest.startAsync()).thenReturn(asyncContext);
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            asyncCall.set(true);
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            return null;
        }).when(asyncContext).start(anyObject());
        //
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        //
        AsyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !AsyncCallAction.isStaticCall();
        Invoker invoker = newInvoker(definitions.get(0), servletRequest, appContext);
        try {
            new InvokerCaller(() -> invoker, null).invoke(null).get();
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
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            asyncCall.set(true);
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            return null;
        }).when(asyncContext).start(anyObject());
        //
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
            apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        //
        SyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker = newInvoker(definitions.get(0), mockRequest("post", new URL("http://www.hasor.net/sync.do"), appContext), appContext);
        Object o = new InvokerCaller(() -> invoker, null).invoke(null).get();
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
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            asyncCall.set(true);
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            return null;
        }).when(asyncContext).start(anyObject());
        //
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.bindType(HttpServletRequest.class).toInstance(servletRequest);
            apiBinder.bindType(HttpServletResponse.class).toInstance(httpServletResponse);
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(SyncCallAction.class);
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        //
        SyncCallAction.resetInit();
        assert !asyncCall.get();
        assert !SyncCallAction.isStaticCall();
        Invoker invoker = newInvoker(definitions.get(0), mockRequest("get", new URL("http://www.hasor.net/sync.do"), appContext), appContext);
        try {
            new InvokerCaller(() -> invoker, null).invoke(null).get();
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