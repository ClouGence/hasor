package net.hasor.web.definition.beans;
import net.hasor.core.AppContext;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestRenderEngine implements RenderEngine {
    private static AtomicBoolean initEngineCall = new AtomicBoolean(false);
    private static AtomicBoolean processCall    = new AtomicBoolean(false);
    private static AtomicBoolean existCall      = new AtomicBoolean(false);
    //
    public static boolean isInitEngineCall() {
        return initEngineCall.get();
    }
    public static boolean isProcessCall() {
        return processCall.get();
    }
    public static boolean isExistCall() {
        return existCall.get();
    }
    public static void resetCalls() {
        initEngineCall.set(false);
        processCall.set(false);
        existCall.set(false);
    }
    //
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        initEngineCall.set(true);
    }
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        processCall.set(true);
    }
    @Override
    public boolean exist(String template) throws IOException {
        existCall.set(true);
        return false;
    }
}
