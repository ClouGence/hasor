作用域
------------------------------------
Hasor 在管理 Bean 的时候支持作用域。一个典型的作用域应用场景就是“单例”，单例作用域的表现是整个应用程序中只保存一份。

另外一个作用域的例子是用户登录网站之后web应用程序通过 Session 保持会话，当然你可以根据应用业务实际场景来决定作用域的用处。

在例如：在一个应用平台的项目中。每个应用都是一个独立的包，每个应用都有自己的运行信息。这些运行信息彼此隔离在不同的应用中，这种场景就很适合使用作用域。

好了，有关作用域究竟是个什么东西，已经说的很明白了。那么下面的各个小节就开始讲解 Hasor 在 Bean 管理中作用域的功能。

单例模式(Singleton)
------------------------------------
Hasor 支持单例，声明 Bean 的单例一般通过下面这种注解方式即可。

.. code-block:: java
    :linenos:
    
    @Singleton()
    public class AopBean {
        ...
    }


如果您使用的 Apibinder 方式进行代码形式声明单例，那么需要这样：

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) {
            apiBinder.bindType(PojoInfo.class).asEagerSingleton();
        }
    }


如果说您的项目中要大量应用到单例模式，在每个类上都标记 `@Singleton` 注解也是一件不小的工作量。Hasor 允许让你增加一个配置，通过配置让 Hasor 框架默认讲所有类在创建时都进行单例化配置。

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.default>
            <!-- 改为 true，让 Hasor 框架默认工作在单例模式下 -->
            <asEagerSingleton>true</asEagerSingleton>
        </hasor.default>
    </config>


原型模式(Prototype)
------------------------------------
本小节重点介绍 `原型模式` 原型模式和单例模式是正反的一对关系。一般情况下 Hasor 在创建 Bean 时候，都是原型模式下的Bean。因此开发者不需要做任何配置。

如果您使用了前一个小节上提到的 `default` 配置修改了 Hasor 的默认配置。那么就相当于每个类都加上了 @Singleton 注解，如果此时创建某个 Bean 不想要它是一个单例 Bean，那么就需要明确指定原型模式。例如下面：

.. code-block:: java
    :linenos:

    @Prototype()
    public class AopBean {
        ...
    }


或者您可以通过 Apibinder 方式进行代码形式声明：

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) {
            apiBinder.bindType(PojoInfo.class).asEagerPrototype();
        }
    }


自定义作用域
------------------------------------
我们以 HttpSession 为例，通过实际例子向大家展示。如何通过Hasor Scope 实现一个 HttpSession 作用域。

.. code-block:: java
    :linenos:

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


在例子中为了避免保存到 Session 中的 Bean 和本身 Session 中的数据 key 出现冲突，我们特意加了一个前缀用于区分。

现在作用域的功能是有了，但是我们的 HttpSession 对象的还没有做初始化。这次我们来实现 Filter 接口，在每次 request 请求到来的时候把 Session 都更新到 ThreadLocal 中。在访问结束之后再把 ThreadLocal 清理掉。下面来看改造了之后的 Scope 代码：

.. code-block:: java
    :linenos:

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


从上面例子代码中看到进入 filter 时做了 Session 的初始化将其保存到 ThreadLocal ，离开之后又把 ThreadLocal 清理掉。最后我们在 Hasor 初始化的时候把 Scope 配置到 Hasor 中：

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            SessionScope scope = new SessionScope();
            apiBinder.filter("/*").through(0, scope);
            apiBinder.registerScope("session", scope);
            ...
        }
    }


接下来使用这个 Scope：

.. code-block:: java
    :linenos:

    apiBinder.bindType(UserInfo.class).toScope(new SessionScope());
    //or
    apiBinder.bindType(UserInfo.class).toScope("session");
