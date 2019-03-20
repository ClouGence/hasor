package net.hasor.web.invoker.filters;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class Demo2CallerFilter implements Filter, InvokerFilter {
    private static AtomicBoolean initCall    = new AtomicBoolean(false);
    private static AtomicBoolean doCall      = new AtomicBoolean(false);
    private static AtomicBoolean destroyCall = new AtomicBoolean(false);
    //
    public static boolean isInitCall() {
        return initCall.get();
    }
    public static boolean isDoCall() {
        return doCall.get();
    }
    public static boolean isDestroyCall() {
        return destroyCall.get();
    }
    public static void resetCalls() {
        initCall.set(false);
        doCall.set(false);
        destroyCall.set(false);
    }
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initCall.set(true);
    }
    @Override
    public void init(InvokerConfig config) throws Throwable {
        initCall.set(true);
    }
    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        doCall.set(true);
        return chain.doNext(invoker);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doCall.set(true);
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        destroyCall.set(true);
    }
}
