package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.definition.beans.TestHttpSessionListener;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.atomic.AtomicBoolean;
//
public class HttpSessionListenerTest {
    @Test
    public void webPluginTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends TestHttpSessionListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                initCall.set(true);
                return new TestHttpSessionListener();
            }
        });
        HttpSessionListenerDefinition definition = new HttpSessionListenerDefinition(bindInfo);
        //
        definition.init(appContext);
        definition.toString();
        definition.sessionCreated(null);
        definition.sessionDestroyed(null);
        //
        assert initCall.get();
    }
    //
    @Test
    public void webPluginTest2() throws Throwable {
        //
        BindInfo<? extends TestHttpSessionListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestHttpSessionListener());
        HttpSessionListenerDefinition definition = new HttpSessionListenerDefinition(bindInfo);
        //
        //
        TestHttpSessionListener.resetCalls();
        assert !TestHttpSessionListener.isSessionCreatedCallCall();
        assert !TestHttpSessionListener.issSessionDestroyedCallCall();
        definition.init(appContext);
        definition.sessionCreated(null);
        definition.sessionDestroyed(null);
        assert TestHttpSessionListener.isSessionCreatedCallCall();
        assert TestHttpSessionListener.issSessionDestroyedCallCall();
        //
    }
}