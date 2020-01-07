渲染器
------------------------------------
Hasor 的渲染器实际上是 View 层的组件，它的最大意义是帮助应用把数据渲染成可见的样子。一个典型场景是请求处理器在执行完毕后，产生一个数据然后交给 JSP 生成 HTML 页面。

这里 JSP 就是渲染引擎，和 Hasor 所指的渲染器是同一个东西。一个渲染器必须是来自 `net.hasor.web.render.RenderEngine` 接口。

举个例子：一个请求在处理之后要使用 Freemarker 来渲染成 HTML，这时候需要一个渲染器。例如：

.. code-block:: java
    :linenos:

    // 渲染器名字叫 flt
    @Render("flt")
    public class FreemarkerRender implements RenderEngine {
        protected Configuration freemarker;

        public void initEngine(AppContext appContext) throws Throwable {
            // 初始化过程，只会执行一次。在这里初始化 freemarker
            this.freemarker = ...
        }

        public boolean exist(String template) throws IOException {
            // 表示渲染器是否要将渲染过程交还给 Servlet 容器。
            // 如果渲染器不准备处理这个视图，那么返回 false。
            //  - 如果模版不存在那么交还给 Servlet 容器
            return freemarker.getTemplateLoader().findTemplateSource(template) != null;
        }

        public void process(RenderInvoker renderData, Writer writer) throws Throwable {
            // 执行 Freemarker 渲染
            Template temp = this.freemarker.getTemplate(renderData.renderTo());
            HashMap<String, Object> data = new HashMap<>();
            renderData.forEach(data::put);
            temp.process(data, writer);
        }
    }


渲染器在编写好之后需要被注册到框架中

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            // 扫描所有带有 @Render 特征类
            Set<Class<?>> classSet = apiBinder.findClass(Render.class);
            // 配置渲染器
            apiBinder.loadRender(classSet);
        }
    }


最后在请求处理器中指明使用具体的渲染器是什么

.. code-block:: java
    :linenos:

    @MappingTo("/my.html")
    public class HtmlProduces {
        @Any
        public void testProduces1() {
            invoker.renderTo("flt", "/my.flt");
        }
    }


ContentType
------------------------------------
作为 html 为结果的响应，设置 ContentType 需要通过 `@Produces` 注解。如下：

.. code-block:: java
    :linenos:

    @MappingTo("/my.html")
    public class HtmlProduces {
        @Any
        @Produces("test/html")
        public void testProduces1() {
            invoker.renderTo("flt", "/my.flt");
        }
    }


.. HINT::
    如果没有指定 `@Produces` 注释，Hasor 也不会主动设置 ContentType。
