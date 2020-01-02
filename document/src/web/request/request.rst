MappingTo详解
------------------------------------
本节将全面的为您展现 Hasor 的请求处理器各种形态以及特性。

最简形态，许多功能受限。用途：通过 request 触发某个事件或操作。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion {
        public void execute() {
            ...
        }
    }

在最简形态上可以通过 execute 的参数，让其功能丰富起来，例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion {
        public void execute(Invoker invoker) {
            ...
        }
    }

    or

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion {
        public void execute(RenderInvoker invoker) {
            ...
        }
    }


您还可以通过继承 WebController 类得到更加完整的请求处理器功能，例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            ...
        }
    }

WebController 类中 90% 的方法是来自于 JFinal，通过它你可以非常简单的操作 cookie，session，attr，及文件上传。


区分请求类型
------------------------------------
如果您想区分请求是 POST 还是 GET。那么可以想如下这样。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        @Post
        public void post(RenderInvoker invoker) {
            ...
        }
        @Get
        public void get(RenderInvoker invoker) {
            ...
        }
    }


execute 是一个默认的处理入口，如果您想使用其它方法来替代 execute。例如上面例子。您就必须要通过 `@Any`、`@Get`、`@Head`、`@Options`、`@Post`、`@Put` 注解标记您的方法，这样 Hasor Web 框架才会知道如何路由到您的入口中。

一个请求处理类正如上面的例子，可以包含多个方法。不同的方法用来指定不同的 Http Method。 在这些用来限定 HTTP 请求方法的注解中， @Any 是最特殊的一个。如果您使用了 @Any，那么请确保不要在使用其它注解。因为 @Any 代表的是任意，假如您同时使用了 @Any、@Post 那么可能会给框架造成一个假象。当你发起一个 post 请求时，框架可能会在两个方法中随机指定。而不是按照您的意愿进入到标记了 post 的方法中。


可以使用的请求类型为：

- @Any，任意类型的请求
- @Get，GET类请求
- @Head，HEAD类请求
- @Options，OPTION类请求
- @Post，POST类请求
- @Put，PUT类请求

如果你不知道什么是请求类型，那么请看这里

.. image:: ../../_static/CC2_11EF_EF69_F9BE.png

自定义请求类型
------------------------------------
通常请求是浏览器发起的，请求类型也是固定的几个。但是如果我们使用了 ajax 框架或者非浏览器发起请求，那么请求类型实际上是可以被修改的。

下面这个类是 @Get 注解的源码：

.. code-block:: java
    :linenos:

    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod(HttpMethod.GET)
    public @interface Get {
    }

你可以仿造这个源码新建一个特定的请求类型 “ABC”，例如：

.. code-block:: java
    :linenos:

    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("ABC")
    public @interface Abc {
    }

然后可以向 @Get 一样使用它

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        @Abc
        public void abc(RenderInvoker invoker) {
            ...
        }
        @Get
        public void get(RenderInvoker invoker) {
            ...
        }
    }


RESTful
------------------------------------
Hasor Web 框架除了前面提到的 传统 MVC 开发方式，它还支持 RESTful 形式的请求。 restful 已经被广泛的应用在 http 协议下的微服务实现手段。

Hasor Web 框架的 Api 已经混合了 RESTful 和 传统的 MVC 声明。因此使用 Hasor 开发 RESTful 您不必理解和记忆更多的 API 接口。下面我们以 User 操作为例，介绍一下 Hasor 的 RESTful Api 的用法。

首先：查询 User。我们在 MappingTo 中通过表达式 `${userID}` 声明一个路径参数 `userID`。然后我们在 execute 方法中 userID 参数上映射这个路径参数。

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @PathParam("userID") long userID) {
            ...
        }
    }


下面我们加入 User 的修改功能，为了区分 User 查询，我们使用 Post、Get 加以区分。

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}")
    public class HelloAcrion extends WebController {
        @Post
        public void updateUser(RenderInvoker invoker, @PathParam("userID") long userID) {
            ...
        }
        @Get
        public void queryByID(RenderInvoker invoker, @PathParam("userID") long userID) {
            ...
        }
    }


或者我们可以通过两个 RESTful 参数来简化一下思路。

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}/${action}")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                    @PathParam("userID") long userID, @PathParam("action") String action) {
            if ("update".equals(action)){
                ...
            } else if ("delete".equals(action)){
                ...
            } else {
                ...
            }
        }
    }


异步请求(Servlet3.0)
------------------------------------
在Servlet 3.0之前，Servlet采用Thread-Per-Request的方式处理请求，即每一次Http请求都由某一个线程从头到尾负责处理。如果一个请求需要进行IO操作，比如访问数据库、调用第三方服务接口等，那么其所对应的线程将同步地等待IO操作完成。而IO操作是非常慢的，所以此时的线程并不能及时地释放回线程池以供后续使用，在并发量越来越大的情况下，这将带来严重的性能问题。

即便是像Spring、Struts这样的高层框架也脱离不了这样的桎梏，因为他们都是建立在Servlet之上的。为了解决这样的问题，Servlet 3.0引入了异步处理，然后在Servlet 3.1中又引入了非阻塞IO来进一步增强异步处理的性能。

Hasor 会自动识别容器的 Servlet 版本。因此 Hasor 在自动识别的帮助下可以做到 Servlet 2.x 和 Servlet 3.x 标准互容，这似的 Hasor 可以同时工作在两种 Servlet 平台之上。

如果你想使用 Servlet 3.0 的异步请求，先要确保你的 Web 容器支持 Servlet 3.0，否则异步请求会当做普通请求处理。

然后像如下这样标记一个 @Async 就可以了，Hasor 会自动在 Servlet 3.0 容器下通过 javax.servlet.AsyncContext.start 方法启动异步处理。

.. code-block:: java
    :linenos:

    @Async
    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            ...
        }
    }


自动类型转换
------------------------------------
Hasor Web 框架还可以帮助你进行类型转换。例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @ReqParam("name") String name,
                            @ReqParam("age") int age) {
            ...
        }
    }


可以转换的类型有：

- 基础类型：byte、short、int、long、float、double、boolean、String
- 大数类型：BigInteger、BigDecimal
- 时间日期：java.util.Date、java.util.Calendar、java.sql.Date、java.sql.Time、java.sql.Timestamp
- 其它类型：Enum、File、URL、URI

类型转换是使用的 net.hasor.utils.convert.ConverterUtils 工具，因此设置时间格式需要通过下面这段代码来配置 ConverterUtils 工具。

整个程序启动时执行一次就可以。

.. code-block:: java
    :linenos:

    DateConverter converter = new DateConverter();
    converter.setPatterns(new String[] { "yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss" });
    ConverterUtils.register(converter, Date.class);
