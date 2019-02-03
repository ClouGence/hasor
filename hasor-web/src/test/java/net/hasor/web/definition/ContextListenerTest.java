package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.definition.beans.TestServletContextListener;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class ContextListenerTest {
    @Test
    public void webPluginTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends ServletContextListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                initCall.set(true);
                return new TestServletContextListener();
            }
        });
        ContextListenerDefinition definition = new ContextListenerDefinition(bindInfo);
        //
        definition.init(appContext);
        definition.toString();
        definition.contextInitialized(null);
        definition.contextDestroyed(null);
        //
        assert initCall.get();
    }
    //
    @Test
    public void webPluginTest2() throws Throwable {
        //
        BindInfo<? extends ServletContextListener> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestServletContextListener());
        ContextListenerDefinition definition = new ContextListenerDefinition(bindInfo);
        //
        //
        TestServletContextListener.resetCalls();
        assert !TestServletContextListener.isContextDestroyedCall();
        assert !TestServletContextListener.isContextInitializedCall();
        definition.init(appContext);
        definition.contextInitialized(null);
        definition.contextDestroyed(null);
        assert TestServletContextListener.isContextDestroyedCall();
        assert TestServletContextListener.isContextInitializedCall();
        //
    }
}