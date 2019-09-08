渲染器
------------------------------------
Hasor的渲染器是专门用来处理 Response 响应的，您可以根据不同的渲染器向客户端做出不同格式的响应，其地位相当于 MVC 中的 View。

一个渲染器必须是实现了 net.hasor.web.render.RenderEngine 接口的具体类，Hasor 中所有内置渲染器都位于 hasor-plugins 中。您需要引入相关依赖才可以使用，如果您使用的是 Freemarker 渲染器那么还需要引入 freemarker 相关的 jar包。


.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-plugins</artifactId>
        <version>2.3.2</version>
    </dependency>

注册渲染器
------------------------------------
我们假定您使用的是标准的内置 Freemarker 渲染器，那么有两种方式让您可以进行初始化渲染器。

方式一，编码

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        @Override
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.suffix("htm").bind(FreemarkerRender.class);//设置 Freemarker 渲染器
        }
    }


方式二，注解扫描

.. code-block:: java
    :linenos:

    @Render({ "html", "htm" })
    public class UserRender implements RenderEngine {
        ...
    }
    // -----
    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            //扫描所有 Render 注解
            apiBinder.scanAnnoRender();
        }
    }