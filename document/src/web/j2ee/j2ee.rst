J2EE
------------------------------------
在 Hasor 中您可以直接使用 J2EE 的接口实现你想要的功能，然后通过 Hasor 的 Module 将其注册到框架中来。

通过注册 J2EE 的 Servlet 和 Filter 等常见接口，您可以不需要投入任何框架集成改造。就可以将 Spring、Struts 定一系列经典的 Web 框架集成到 Hasor 中来。

下面就在本章中介绍一下 Servlet 、Filter 、HttpSessionListener、ServletContextListener 的用法。

Servlet
------------------------------------
在 Hasor Web 中使用 Servlet 如下所示，首先编写我们自己的 HttpServlet，然后将它注册到 Hasor 中：

.. code-block:: java
    :linenos:

    public class DemoHttpServlet extends HttpServlet{
        protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            ...
        }
    }

第一种方式 Api 接口注册 Servlet 的地址。

.. code-block:: java
    :linenos:

    public class DemoModule extends WebModule{
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.jeeServlet("/your_point.do").with(DemoHttpServlet.class);
        }
    }


第二种方式，通过 @MappingTo 注册 Servlet，如下：

.. code-block:: java
    :linenos:

    @MappingTo("/your_point.do")
    public class DemoHttpServlet extends HttpServlet{
        protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            ...
        }
    }


扫描所有 @MappingTo

.. code-block:: java
    :linenos:

    public class DemoModule extends WebModule{
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            // 扫描所有带有 @MappingTo 特征类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            // 对 aClass 集合进行发现并自动配置控制器
            apiBinder.loadType(aClass);
            ...
        }
    }


Filter
------------------------------------
在 Hasor Web 中使用 Filter 如下所示，首先编写我们自己的 Filter，然后将它注册到 Hasor 中：

.. code-block:: java
    :linenos:

    public class MyFilter implements Filter {
        ...
    }


然后将其注册到 Hasor 框架中：

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.jeeFilter("/*").through(MyFilter.class);
            ...
        }
    }


HttpSessionListener
------------------------------------
在 Hasor Web 中使用 HttpSessionListener 如下所示，首先编写我们自己的 HttpSessionListener，然后将它注册到 Hasor 中：

.. code-block:: java
    :linenos:

    public class MyHttpSessionListener implements HttpSessionListener {
        ...
    }


然后将其注册到 Hasor 框架中：

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.addSessionListener(MyHttpSessionListener.class);
            ...
        }
    }


ServletContextListener
------------------------------------
在 Hasor Web 中使用 ServletContextListener 如下所示，首先编写我们自己的 ServletContextListener，然后将它注册到 Hasor 中：

.. code-block:: java
    :linenos:

    public class MyServletContextListener implements ServletContextListener {
        ...
    }

然后将其注册到 Hasor 框架中：

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.addServletListener(MyServletContextListener.class);
            ...
        }
    }
