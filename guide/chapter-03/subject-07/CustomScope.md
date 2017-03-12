&emsp;&emsp;我们以 HttpSession 为例，通过实际例子向大家展示。如何通过Hasor Scope 实现一个 HttpSession 作用域。
```java
public class SessionScope implements Scope{
    private static final ThreadLocal<HttpSession> session 
        = new ThreadLocal<HttpSession>();

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
```

&emsp;&emsp;在例子中为了避免保存到 Session 中的 Bean 和本身 Session 中的数据 key 出现冲突，我们特意加了一个前缀用于区分。

&emsp;&emsp;现在作用域的功能是有了，但是我们的 HttpSession 对象的还没有做初始化。这次我们来实现 Filter 接口，在每次 request 请求到来的时候把 Session 都更新到 ThreadLocal 中。在访问结束之后再把 ThreadLocal 清理掉。

&emsp;&emsp;下面来看改造了之后的 Scope 代码：
```java
public class SessionScope implements Scope, Filter {
    private static final ThreadLocal<HttpSession> session
     = new ThreadLocal<HttpSession>();

    public void init(FilterConfig filterConfig) { ... }
    public void destroy() { ... }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
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
```

&emsp;&emsp;从上面例子代码中看到进入 filter 时做了 Session 的初始化将其保存到 ThreadLocal ，离开之后又把 ThreadLocal 清理掉。

&emsp;&emsp;最后我们在 Hasor 初始化的时候把 Scope 配置到 Hasor 中：
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        SessionScope scope = new SessionScope();
        apiBinder.filter("/*").through(0, scope);
        apiBinder.registerScope("session", scope);
        ...
    }
}
```

&emsp;&emsp;接下来使用这个 Scope：
```java
apiBinder.bindType(UserInfo.class).toScope(new SessionScope());
//or
apiBinder.bindType(UserInfo.class).toScope("session");
```