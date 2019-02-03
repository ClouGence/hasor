package net.hasor.web.definition.beans;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestHttpSessionListener implements HttpSessionListener {
    private static AtomicBoolean sessionCreatedCall   = new AtomicBoolean(false);
    private static AtomicBoolean sessionDestroyedCall = new AtomicBoolean(false);
    //
    public static boolean isSessionCreatedCallCall() {
        return sessionCreatedCall.get();
    }
    public static boolean issSessionDestroyedCallCall() {
        return sessionDestroyedCall.get();
    }
    public static void resetCalls() {
        sessionCreatedCall.set(false);
        sessionDestroyedCall.set(false);
    }
    //
    @Override
    public void sessionCreated(HttpSessionEvent sce) {
        sessionCreatedCall.set(true);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent sce) {
        sessionDestroyedCall.set(true);
    }
}
