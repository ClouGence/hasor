package net.hasor.web.definition;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerData;
import net.hasor.web.WebPlugin;

import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestCallerWebPlugin implements WebPlugin {
    private static AtomicBoolean beforeCall = new AtomicBoolean(false);
    private static AtomicBoolean afterCall  = new AtomicBoolean(false);
    //
    public static boolean isBeforeCall() {
        return beforeCall.get();
    }
    public static boolean isAfterCall() {
        return afterCall.get();
    }
    public static void resetCalls() {
        beforeCall.set(false);
        afterCall.set(false);
    }
    //
    @Override
    public void beforeFilter(Invoker invoker, InvokerData info) {
        beforeCall.set(true);
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData info) {
        afterCall.set(true);
    }
}
