在 Hasor 中可以直接使用 J2EE 的接口实现想要的功能，J2EE 的原始接口好处有两个
- 1. 使用框架的学习成本降低。
- 2. 可以不需要投入任何框架集成改造就可以将一系列经典的框架进来。

下面就在本章中介绍一下 Servlet 、Filter 、HttpSessionListener、ServletContextListener 的用法。

Servlet
------------------------------------
使用 Servlet 如下所示：

.. code-block:: java
    :linenos:

    @MappingTo("/your_point.do")
    public class DemoHttpServlet extends HttpServlet{
        protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            ...
        }
    }

然后注册 Servlet

.. code-block:: java
    :linenos:

    public class DemoModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            // 扫描所有带有 @MappingTo 注解类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            // 对 aClass 集合进行发现并自动配置控制器
            apiBinder.loadType(aClass);
        }
    }


Filter
------------------------------------
使用 Filter 如下所示：

.. code-block:: java
    :linenos:

    public class MyFilter implements Filter {
        ...
    }


然后注册 Filter：

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.jeeFilter("/*").through(MyFilter.class);
            ...
        }
    }


J2EE的Listener
------------------------------------
J2EE 规范中定义了各种各样的 Listener 例如 `javax.servlet.http.HttpSessionListener`

这些 Listener 基本在 Hasor 中都是都是支持的，配置它们需要通过 SPI 的形式来注册。例如：

.. code-block:: java
    :linenos:

    public class MyHttpSessionListener implements HttpSessionListener {
        ...
    }
    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.bindSpiListener(HttpSessionListener.class, new MyHttpSessionListener());
            ...
        }
    }


目前 Hasor 已经支持的 J2EE Listener清单有：

+---------------------------------------------+
| 接口                                        |
+=============================================+
| `javax.servlet.http.HttpSessionListener`    |
+---------------------------------------------+
| `javax.servlet.ServletContextListener`      |
+---------------------------------------------+
| `javax.servlet.ServletRequestListener`      |
+---------------------------------------------+
