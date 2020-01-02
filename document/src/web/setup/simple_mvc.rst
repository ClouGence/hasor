HelloWord
------------------------------------
我们通过一个 MVC 的例子作为 Hasor Web 框架的这一个小结通过 Web MVC 例子，来展示使用 Hasor 接收一个 Web 请求然后交给 jsp 去显示。

为了保证请求和响应的编码正确，我们要在启动入口中配置做一下声明。配置好以后 Hasor 框架会帮助我们设置 request/response 的编码为 UTF-8。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            //设置请求响应编码
            apiBinder.setEncodingCharacter("utf-8", "utf-8");
        }
    }


接着创建请求处理器，一个请求处理器可以简单的只包含一个 `execute` 方法。

.. code-block:: java
    :linenos:

    public class HelloMessage {
        public void execute(Invoker invoker) {
            invoker.put("message", "this message form Project.");
        }
    }


将请求处理器注册到框架中有两种办法，下面是通过手动注册的方式来集中管理。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.mappingTo("/hello.jsp").with(HelloMessage.class);
            ...
        }
    }

另一种是，通过 `@MappingTo` 注解让框架自动发现。不需要将每个请求控制器都进行注册，使用起来更加方便。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            // 加载 HelloMessage 请求处理器
            apiBinder.loadType(HelloMessage.class);
            ...
        }
    }

    @MappingTo("/hello.jsp")
    public class HelloMessage {
        ...
    }


如果您有多个请求处理器需要注册，可以使用注解扫描的方式来简化开发。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            // 扫描所有带有 @MappingTo 特征类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            // 对 aClass 集合进行发现并自动配置控制器
            apiBinder.loadMappingTo(aClass);
            ...
        }
    }


最后创建 `hello.jsp` 视图文件，我们把 `message` 打印出来：

.. code-block:: jsp
    :linenos:

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
        <head>
            <title>Hello Word</title>
        </head>
        <body>
            ${message}
        </body>
    </html>


当上面的一切都做好之后，启动您的 web 工程，访问： `http://localhost:8080/hello.jsp` 即可得到结果。

您还可以通过代码方式来决定最终渲染的视图，例如：

.. code-block:: java
    :linenos:

    apiBinder.mappingTo("/forward.do").with(HelloMessage.class);

    public class HelloMessage {
        public void execute(RenderInvoker invoker) {
            invoker.put("message", "this message form Project.");
            if (test){
                invoker.renderTo("jsp","/hello.jsp");
            } else {
                invoker.renderTo("jsp","/error.jsp");
            }
        }
    }


运行项目，请求 `http://localhost:8080/forward.do` 页面就会根据您的逻辑来渲染对应的视图。
