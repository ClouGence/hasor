package net.demo.hasor.scope;
import net.hasor.core.Provider;
import net.hasor.core.Scope;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Created by zhaoyongchun on 16/10/6.
 */
public class SessionScope implements Scope, Filter {
    private static final ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /**/ }
    @Override
    public void destroy() { /**/ }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (session.get() != null) {
                session.remove();
            }
            session.set(((HttpServletRequest) request).getSession(true));
            chain.doFilter(request, response);
        } finally {
            if (session.get() != null) {
                session.remove();
            }
        }
    }
    @Override
    public <T> Provider<T> scope(Object key, Provider<T> provider) {
        HttpSession httpSession = session.get();
        if (httpSession == null) {
            return provider;
        }
        String keyStr = "session_scope_" + key.toString();
        Object attribute = httpSession.getAttribute(keyStr);
        Provider<T> finalProvider = provider;
        if (attribute == null) {
            httpSession.setAttribute(keyStr, provider);
        } else {
            finalProvider = (Provider<T>) httpSession.getAttribute(keyStr);
        }
        return finalProvider;
    }
}