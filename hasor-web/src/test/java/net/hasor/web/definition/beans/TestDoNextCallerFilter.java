package net.hasor.web.definition.beans;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
public class TestDoNextCallerFilter extends TestCallerFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        super.doFilter(request, response, chain);
    }
    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        Object resultData = chain.doNext(invoker);
        return super.doInvoke(invoker, chain);
    }
}
