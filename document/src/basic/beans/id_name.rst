唯一性
------------------------------------
于Spring一样，可以为 Bean 指定唯一的名称这就是 ID

.. code-block:: java
    :linenos:

    package net.test.hasor;
    public class HelloModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(InfoBean.class).idWith("beanA");
            apiBinder.bindType(InfoBean.class).idWith("beanB");
        }
    }

    public class UseBean {
        @Inject(value = "beanA" , byType = Type.ByID)
        private InfoBean pojoA;
        @Inject(value = "beanB" , byType = Type.ByID)
        private InfoBean pojoB;
    }

也可以通过 AppContext 根据 ID 获取Bean ``appContext.getInstance("beanA")``

同类型的不同Bean
------------------------------------
Name的意义是同一个类型的不同 Bean 配置不同的名字,例如：

.. code-block:: java
    :linenos:

    package net.test.hasor;
    public class HelloModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(ICache.class).nameWith("user").to(...);
            apiBinder.bindType(ICache.class).nameWith("data").to(...);
        }
    }

    public class UseBean {
        @Inject("user")
        private ICache user;
        @Inject("data")
        private ICache data;
    }
