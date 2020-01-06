构造方法注入
------------------------------------
.. HINT::
    当类中有且只有一个无参的构造方法时，是不需要通过 ConstructorBy 来指明构建 Bean 的构造方法。

首先，在要被注入的构造方法上标记 `net.hasor.core.ConstructorBy` 注解，以表示在创建 Bean 的时候使用这个构造方法。
然后，如果有参数要被注入，那么在需要注入的参数前面加上 `net.hasor.core.Inject` 注解。以表示某个参数的来源是通过依赖注入进来的。

.. code-block:: java
    :linenos:

    public class CustomBean {
        private FunBean funBean = null;

        @ConstructorBy
        public CustomBean(@Inject() FunBean funBean) {
            this.funBean = funBean;
        }
        public void callFoo() {
            this.funBean.foo();
        }
    }

创建 Bean 也很简单，无需任何注册或者声明Bean的步骤。只需要从容器中按类型获取即可，Hasor 会自动在创建 Bean 过程中解析配置。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build();
    CustomBean myBean = appContext.getInstance(CustomBean.class);


属性注入
------------------------------------
属性注入只需要在需要注入的字段前面加上 `net.hasor.core.Inject` 注解就可以了

.. code-block:: java
    :linenos:

    public class TradeService {
        @Inject
        private PayService payService;

        public boolean foo(){
            ...
        }
    }
    public class PayService {
        ...
    }


接口注入
------------------------------------
如果要注入的对象类型是一个接口，那么需要在这个接口上设置设置 `net.hasor.core.ImplBy` 注解。已确定其具体实现类是谁。

.. code-block:: java
    :linenos:

    @ImplBy(PayServiceImpl.class)
    public interface PayService {
        ...
    }

也可以使用代码方式在 Module 初始化过程中声明接口和实现类的关系。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build(apiBinder -> {
        apiBinder.bindType(PayService.class).to(PayServiceImpl.class);
    });

.. HINT::
    @ImplBy 注解具有传导性。ImplBy的那个目标类型也可以再次被 ImplBy。


InjectMembers接口
------------------------------------
Bean 一旦实现 `net.hasor.core.spi.InjectMembers` 接口，那么其它所有注入方式全部失效。具体的注入的全部过程会被委托给 InjectMembers 接口处理。

.. code-block:: java
    :linenos:

    public class OrderManager implements InjectMembers {
        @Inject  // <-因为实现了InjectMembers接口，因此@Inject注解将会失效。
        public StockManager stockBeanTest;
        public StockManager stockBean;

        public void doInject(AppContext appContext) throws Throwable {
            assert this.stockBeanTest == null;
        }
    }


声明式注入
------------------------------------
用代码的形式来说明类的注入依赖关系就叫声明式注入，也可以利用 Xml 等配置文件来替代代码。但是本质是一样的。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build(apiBinder -> {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            // .类型 TradeService 的 payService 字段要求依赖注入，注入的类型是 PayService
            apiBinder.bindType(TradeService.class).inject("payService", PayService.class);
            // .由于 PayService 是一个接口，因此指定 PayService 的实现类为 PayServiceImpl2
            apiBinder.bindType(PayService.class).to(PayServiceImpl2.class);
        }
    });
    TradeService myBean = appContext.getInstance(TradeService.class);
