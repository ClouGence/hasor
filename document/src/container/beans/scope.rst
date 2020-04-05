Hasor 在管理 Bean 的时候支持作用域，一个典型的作用域应用场景就是“单例”。单例作用域的表现是整个应用程序中只保存一份。
另外一个作用域的例子是用户登录网站之后 web 应用程序通过 Session 保持会话。

单例模式(Singleton)
------------------------------------
声明 Bean 的单例一般通过下面这种注解方式：

.. code-block:: java
    :linenos:
    
    @Singleton()
    public class AopBean {
        ...
    }


如果您使用的 ApiBinder 方式进行代码形式声明单例，那么需要这样：

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) {
            apiBinder.bindType(PojoInfo.class).asEagerSingleton();
        }
    }


原型模式(Prototype)
------------------------------------
`原型模式` 和单例模式是正反的一对关系。Hasor 默认使用的是原型模式，因此开发者不需要做任何配置。

.. code-block:: java
    :linenos:

    @Prototype()
    public class AopBean {
        ...
    }


或者您可以通过 ApiBinder 方式进行代码形式声明：

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) {
            apiBinder.bindType(PojoInfo.class).asEagerPrototype();
        }
    }


设置默认为单例模式
------------------------------------
Hasor 是不直接支持默认单例的。不过可以借助 SPI 实现这个功能。首先创建SPI监听器：

.. code-block:: java
    :linenos:

    public class MyCollectScopeListener implements CollectScopeListener {
        public Supplier<Scope>[] collectScope(BindInfo<?> bindInfo, AppContext appContext,
                Supplier<Scope>[] suppliers) {
            // 注册的 Bean 无论是否已经单例，都追加一个单例。
            return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
        }

        public Supplier<Scope>[] collectScope(Class<?> targetType, AppContext appContext,
                Supplier<Scope>[] suppliers) {
            // 非注册的 Bean 无论是否已经单例，都追加一个单例。
            return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
        }
    }


然后创建容器并且设置 SPI：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build(apiBinder -> {
        // 设置默认单例SPI
        apiBinder.bindSpiListener(CollectScopeListener.class, new MyCollectScopeListener());
    });


最后测试两次创建的 Bean 就是一样的了：

.. code-block:: java
    :linenos:

    PojoBean pojoBean1 = appContext.getInstance(PojoBean.class);
    PojoBean pojoBean2 = appContext.getInstance(PojoBean.class);
    assert pojoBean1 == pojoBean2;


自定义作用域
------------------------------------
以 HttpSession 为例，实现一个 HttpSession 作用域。

.. code-block:: java
    :linenos:

    public class SessionScope implements Scope {
        public static final ThreadLocal<HttpSession> session
            = new ThreadLocal<HttpSession>();

        public <T> Provider<T> scope(Object key, Provider<T> provider) {
            HttpSession httpSession = session.get();
            if (httpSession == null) {
                return provider;
            }
            // 为了避免保存到 Session 中的 Bean 和本身 Session 中的数据 key
            // 出现冲突，增加一个前缀用于区分
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


然后通过一个 Filter 每次 request 请求到来的时候把 Session 对象设置到 ThreadLocal 中。

.. code-block:: java
    :linenos:

    public class ConfigSession implements Filter {
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            try {
                if (SessionScope.session.get() != null) {
                    SessionScope.session.remove();
                }
                SessionScope.session.set(((HttpServletRequest) request).getSession(true));
                chain.doFilter(request, response);
            } finally {
                if (SessionScope.session.get() != null) {
                    SessionScope.session.remove();
                }
            }
        }
    }


最后我们在创建 Hasor 的时候把 Scope 配置上，这里由于要配置 Filter 因此使用 `WebModule`

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.filter("/*").through(0, new ConfigSession());
            apiBinder.registerScope("session", new SessionScope());
            ...
        }
    }


接下来配置每次创建 UserInfo 对象时都是 Session 内唯一：

.. code-block:: java
    :linenos:

    apiBinder.bindType(UserInfo.class).toScope("session");


作用域链
------------------------------------
(暂略)详细暂时请看：`net.hasor.core.Scope` 接口

