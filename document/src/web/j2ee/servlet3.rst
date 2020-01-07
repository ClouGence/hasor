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
