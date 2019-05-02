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
            apiBinder.addServletListener(new TestServletContextListener());
            apiBinder.addSessionListener(new TestHttpSessionListener());
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