package net.hasor.web.definition;
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
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        chain.doNext(invoker);
        super.doInvoke(invoker, chain);
    }
}
