使用 Freemarker
------------------------------------
使用 Freemarker 作为模板引擎，Hasor 框架内置了 Freemarker 渲染引擎插件（位于hasor-plugins）

引入依赖并配置渲染器插件

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-plugins</artifactId>
        <version>2.3.2</version>
    </dependency>
    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
        <version>2.3.23</version>
    </dependency>


.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        @Override
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.suffix("htm").bind(FreemarkerRender.class);//设置 Freemarker 渲染器
        }
    }


您可以通过 `*.htm` 的形式访问您的请求处理器。然后使用 freemarker 进行渲染。

.. code-block:: java
    :linenos:

    @MappingTo("/index.htm")
    public class Index {
        public void execute(RenderInvoker invoker) {
            ...
        }
    }


如果您是 restful 方式，您还可以通过 @Produces 注解指定渲染器

.. code-block:: java
    :linenos:

    @MappingTo("/index.htm")
    public class Index {
        @Produces("htm")
        public void execute(RenderInvoker invoker) {
            ...
        }
    }


你也可以在处理表单设置渲染的页面时，指定渲染引擎。如下：

.. code-block:: java
    :linenos:

    @MappingTo("/login.do")
    public class Login {
        public void execute(RenderInvoker invoker) {
            if (test){
                invoker.renderTo("htm","/welcome.htm");
            } else {
                invoker.renderTo("jsp","/error.jsp");
            }
        }
    }


高级制订 Freemarker
------------------------------------
Hasor 的内置 Freemarker 还允许使用您自定义的 Freemarker 引擎作为渲染器引擎，而不是内置创建的。（使用用户自己创建的 Freemarker 引擎会让开发者更加灵活的掌控整个程序）

我们有两种方式进行扩展，第一种方式继承已有的 Freemarker 渲染器进行扩展。

.. code-block:: java
    :linenos:

    public class MyFreemarkerRender extends FreemarkerRender {
        @Override
        protected Configuration newConfiguration(AppContext appContext,
                        ServletContext servletContext) throws IOException {
            // 创建 Freemarker 引擎
            return ...
        }
        @Override
        protected void configSharedVariable(AppContext appContext,
                        ServletContext servletContext, Configuration freemarker) throws TemplateModelException {
            ...
            super.configSharedVariable(appContext, servletContext, freemarker);
            freemarker.setSharedVariable("varKey", ...); // 自定义变量
        }
    }
    public class StartModule extends WebModule {
        @Override
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.suffix("htm").bind(MyFreemarkerRender.class);//设置 Freemarker 渲染器
        }
    }


第二种方式，比较简单，您只需要将 Freemarker 的 Configuration 对象提供出来即可。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        @Override
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.suffix("htm").bind(FreemarkerRender.class);   //设置 Freemarker 渲染器
            apiBinder.bind(Configuration.class).to( ... );          //配置渲染引擎
        }
    }
