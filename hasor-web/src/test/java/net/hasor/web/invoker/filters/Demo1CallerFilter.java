package net.hasor.web.invoker.filters;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
//
public class Demo1CallerFilter implements Filter, InvokerFilter {
    private static AtomicInteger initCall    = new AtomicInteger(0);
    private static AtomicInteger doCall      = new AtomicInteger(0);
    private static AtomicInteger destroyCall = new AtomicInteger(0);
    //
    public static boolean isInitCall() {
        return initCall.get() > 0;
    }
    public static boolean isDoCall() {
        return doCall.get() > 0;
    }
    public static boolean isDestroyCall() {
        return destroyCall.get() > 0;
    }
    public static void resetCalls() {
        initCall.set(0);
        doCall.set(0);
        destroyCall.set(0);
    }
    public static int getInitCall() {
        return initCall.get();
    }
    public static int getDoCall() {
        return doCall.get();
    }
    public static int getDestroyCall() {
        return destroyCall.get();
    }
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initCall.incrementAndGet();
    }
    @Override
    public void init(InvokerConfig config) throws Throwable {
        initCall.incrementAndGet();
    }
    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        doCall.incrementAndGet();
        return chain.doNext(invoker);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doCall.incrementAndGet();
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        destroyCall.incrementAndGet();
    }
}
