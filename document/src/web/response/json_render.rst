扩展：JSON渲染引擎
------------------------------------

.. code-block:: java
    :linenos:

    /**
     * 使用 FastJson 作为序列化工具的 Json 渲染器
     * @version : 2016年1月3日
     * @author 赵永春 (zyc@hasor.net)
     */
    @Render("json")
    public class JsonRender implements RenderEngine {
        public boolean exist(String template) throws IOException {
            return true;
        }

        public void process(RenderInvoker renderData, Writer writer) throws Throwable {
            Object data = renderData.get(Invoker.RETURN_DATA_KEY);
            JSON.writeJSONString(writer, data);
        }
    }


使用 Json 渲染器，execute 方法返回的对象使用 Json 渲染器自动序列化并输出给前端，同时设置ContentType。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.json")
    public class HelloAction {
        @Produces("json")
        public Object execute(RenderInvoker invoker) {
            invoker.renderType("json");
            return ...
        }
    }


进一步还可以利用 `InvokerFilter` 把设置渲染器的工作统一处理：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.json")
    public class UseJsonInvokerFilter implements InvokerFilter {
        public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
            ((RenderInvoker)invoker).renderType("json");
            return chain.doNext(invoker);
        }
    }
