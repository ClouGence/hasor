package net.hasor.web.definition.beans;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestServletContextListener implements ServletContextListener {
    private static AtomicBoolean contextInitializedCall = new AtomicBoolean(false);
    private static AtomicBoolean contextDestroyedCall   = new AtomicBoolean(false);
    //
    public static boolean isContextInitializedCall() {
        return contextInitializedCall.get();
    }
    public static boolean isContextDestroyedCall() {
        return contextDestroyedCall.get();
    }
    public static void resetCalls() {
        contextInitializedCall.set(false);
        contextDestroyedCall.set(false);
    }
    //
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextInitializedCall.set(true);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        contextDestroyedCall.set(true);
    }
}
