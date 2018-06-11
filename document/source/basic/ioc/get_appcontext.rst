注入容器
--------------------
用过 Spring 的同学都知道要想注入 Spring 容器本身您必须要实现 ApplicationContextAware 接口。在Hasor 中您也可以使用相同的方式：

.. code-block:: java
    :linenos:

    public class AwareBean implements AppContextAware {
        public void setAppContext(AppContext appContext) {
            ...
        }
    }

    appContext.getInstance(AwareBean.class);


但是 Hasor 中也允许你用更简单的方式，直接通过 @Inject 进行注入，如下：

.. code-block:: java
    :linenos:

    public class TestBean {
        @Inject()
        private AppContext appContext;
    }


另外在一些特殊场景下您还可以利用 Hasor 的事件机制来拿到 AppContext，例如：

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            Hasor.autoAware(apiBinder.getEnvironment(),new AwareBean());
        }
    }

`Hasor.autoAware` 方法使用时，要注意，一定要在 Hasor onStart 阶段之前调用，否则您即便是调用了这个方法也不会得到 AppContext 对象。这是因为 aware 是通过 ContextEvent_Started 事件完成 AppContext 对象获取的。