Bean 的启动
------------------------------------
有时候我们希望有 Bean 可以在被创建时自动调用一个 init 方法，本小节就来向大家展示一下 Hasor 这方面的能力。

方式一：通过 `net.hasor.core.Init`或`javax.annotation.PostConstruct` 注解，例如下面这样。

.. code-block:: java
    :linenos:

    public class PojoBean {
        @Init
        public void init(){
            ...
        }
    }


方式二：通过编码方式在 Module 初始化时指定，例如下面这样。

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(PojoBean.class).initMethod("init");
        }
    }

.. HINT::
    如果您组合使用 @Singleton 注解和 @Init 注解，同时这个类在 Hasor 启动时通过 Module 预先注册了。那么 Hasor 会在启动时自动创建这个类并调用 init 方法。例如：

.. code-block:: java
    :linenos:

    @Singleton
    public class PojoBean {
        @Init
        public void init(){
            ...
        }
    }

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(PojoBean.class);
        }
    }


或者下面这样的方式也可以达到同样的效果：

.. code-block:: java
    :linenos:

    public class PojoBean {
        public void init(){
            ...
        }
    }

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(PojoBean.class)
                .initMethod("init")    // 初始化方法，相当于 @Init 注解
                .asEagerSingleton();   // 单例，相当于 @Singleton 注解
        }
    }

Bean 的销毁
------------------------------------
销毁Bean同样也支持两种模式，需要注意的是只有单例的对象才支持销毁能力。

- 注解换成 `net.hasor.core.Destroy`或`javax.annotation.PreDestroy`
- ApiBinder 的方法对应的是 `destroyMethod`
