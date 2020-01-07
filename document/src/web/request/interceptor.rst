在 Hasor 中，一共有三种不同的方式实现请求拦截。

1. 通过 `InvokerFilter` 接口拦截请求（推荐）
2. 通过 `javax.servlet.Filter` 接口拦截请求
3. 通过 Aop 实现请求拦截器

其中 `InvokerFilter` 接口和 `Filter` 接口的工作方式和原理是等价的，只是用了不同的接口。

InvokerFilter形式
------------------------------------

.. code-block:: java
    :linenos:

    public class MyInvokerFilter implements InvokerFilter {
        public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
            try {
                // before
                chain.doNext(invoker);
                // after
            } catch (Throwable e) {
                // error
                throw e;
            }
        }
    }


Filter形式
------------------------------------
传统的 J2EE的 `Filter` 充当拦截器。例如：

.. code-block:: java
    :linenos:

    public class MyFilter implements Filter {
        ...
    }


配置拦截器
------------------------------------
最后对拦截器进行声明注册就可以正常使用了。

.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.filter("/*").through(MyInvokerFilter.class); // InvokerFilter形式
            apiBinder.jeeFilter("/*").through(MyFilter.class);     // Filter形式
            ...
        }
    }
