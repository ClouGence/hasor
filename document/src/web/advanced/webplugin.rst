WebPlugin接口
------------------------------------
WebPlugin 接口的作用是 Hasor Web 框架在开始执行 拦截器 和 执行结束 之后允许用户增加一个自定义扩展操作。

WebPlugin 接口不同于拦截器的是，它会确保在所有拦截器前执行。具体用法如下：

.. code-block:: java
    :linenos:

    public class DemoWebPlugin implements WebPlugin {
        @Override
        public void beforeFilter(Invoker invoker, InvokerData define) {
            ...
        }
        @Override
        public void afterFilter(Invoker invoker, InvokerData define) {
            //
        }
    }


编写好 WebPlugin 之后您还需要通过 WebModule 注册到框架中。

.. code-block:: java
    :linenos:

    public class DemoModule extends WebModule{
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            apiBinder.addPlugin(DemoWebPlugin.class);
        }
    }
