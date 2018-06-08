构造方法注入
------------------------------------



属性注入
------------------------------------
使用 Hasor 的依赖注入您不需要进行复杂的 Xml 配置，只需要在需要注入的字段上标记一个 `net.hasor.core.Inject` 注解即可：

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

接下来您只需要创建一个容器，然后通过容器创建对象就可以了。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext();
    TradeService myBean = appContext.getInstance(TradeService.class);

接口注入
------------------------------------
如果 PayService 为一个接口而非具体的实现类，那么您需要在 PayService 接口上通过 @ImplBy 注释标记出它的具体实现类。

.. code-block:: java
    :linenos:

    @ImplBy(PayServiceImpl.class)
    public interface PayService {
        ...
    }

回调式注入
------------------------------------



回调式注入
------------------------------------



非侵入式注入
------------------------------------
上面是侵入式的配置，如果您需要在非侵入式场景下配置依赖注入。可以使用 Hasor 的 Module 来声明依赖关系例如：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext(new Module() {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(TradeService.class).inject("payService", PayService.class);
        }
    });
    TradeService myBean = appContext.getInstance(TradeService.class);
