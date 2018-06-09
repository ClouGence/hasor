构造方法注入
------------------------------------
Hasor 的构造方法注入十分简单，你只需要在构造方法中需要注入的参数前面加上 `net.hasor.core.Inject` 注解就可以了。

.. code-block:: java
    :linenos:

    public class CustomBean {
        private FunBean funBean = null;
        public CustomBean(@Inject() FunBean funBean) {
            this.funBean = funBean;
        }
        public void callFoo() {
            this.funBean.foo();
        }
    }

接下来您只需要创建一个容器，然后通过容器创建对象就可以了。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext();
    TradeService myBean = appContext.getInstance(TradeService.class);


属性注入
------------------------------------
属性注入也是同样的，只需要在需要注入的字段前面加上 `net.hasor.core.Inject` 注解就可以了，无需复杂的 Xml 配置。

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
如果 PayService 为一个接口而非具体的实现类，那么您需要在 PayService 接口上通过 @ImplBy 注释标记出它的具体实现类。

.. code-block:: java
    :linenos:

    @ImplBy(PayServiceImpl.class)
    public interface PayService {
        ...
    }

小贴士1：如果 @ImplBy 指定的类型上也标记了 @ImplBy，那么 Hasor 会进一步向下寻找。这就意味着例如下面这样的 case 也是成立的。

.. code-block:: java
    :linenos:

    @ImplBy(PayServiceImpl2.class)
    public class PayServiceImpl implements PayService {
        ...
    }
    public class PayServiceImpl2 implements PayService {
        ...
    }

小贴士2：被注入的接口类型不一定非要强制通过 @ImplBy 注解来指定类型。如果您不打算使用注解来指定实现类，
那么就需要在 Hasor 加载过程中指明实现类。这里会用到 `非侵入式注入`

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext(new Module() {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindType(PayService.class).to(PayServiceImpl.class);
        }
    });


回调注入
------------------------------------
回调式注入是 Hasor 特有的一种注入方式，它要求被注入的 Bean 必须实现 InjectMembers 接口。因此这种方式具备很强的侵入性。
优点是，这种方式可以让我们更加直接的通过 java code 来执行注入过程。

需要提示的是 @Inject 和 InjectMembers 两者是排他性的。而且 InjectMembers 具备较高的优先级

.. code-block:: java
    :linenos:

    public class OrderManager implements InjectMembers {
        @Inject  // <-因为实现了InjectMembers接口，因此@Inject注解将会失效。
        public StockManager stockBeanTest;
        public StockManager stockBean;
        //
        public void doInject(AppContext appContext) throws Throwable {
            boolean useCaseA = ...
            if (useCaseA){
                this.iocBean = appContext.findBindingBean(
                    "caseA",PojoBean.class);
            }else{
                this.iocBean = appContext.findBindingBean(
                    "caseB",PojoBean.class);
            }
            //
        }
    }


非侵入式注入
------------------------------------
上面的注入方式无论那种都需要您在被注入Bean 和注入 Bean 之间进行配置，因此它们都属于侵入式的。
下面介绍一下使用 Hasor 的 Module 来声明依赖关系的方式：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext(new Module() {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            // .类型 TradeService 的 payService 字段要求依赖注入，注入的类型是 PayService
            apiBinder.bindType(TradeService.class).inject("payService", PayService.class);
            // .由于 PayService 是一个接口，因此指定了 PayService 的实现类为 PayServiceImpl2
            apiBinder.bindType(PayService.class).to(PayServiceImpl2.class);
        }
    });
    TradeService myBean = appContext.getInstance(TradeService.class);


ID和Name
------------------------------------
**A. ID**
Hasor 和 Spring一样，您可以为 Bean 指定一个唯一的名称。这样一来在进行依赖注入的时候您就可以通过 ID 标识它。

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

当然您也可以通过 AppContext 根据 ID 获取Bean
`AppContext.getInstance("beanA")`

**B. Name**
Name不同于 ID 的是，它允许在不同的类型之间重复定义同一个名字。

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

注入配置
------------------------------------
我们先来举例一个场景，假定我们有一个类用来封装数据库连接信息。首先我们有一个配置文件用于存放数据库连接信息：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <jdbcSettings>
            <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
            <jdbcURL>jdbc:mysql://127.0.0.1:3306/test</jdbcURL>
            <userName>sa</userName>
            <userPassword></userPassword>
        </jdbcSettings>
    </config>

接下来我们创建一个 DataBaseBean 然后将这些属性注入到这个 Bean 中，这一次我们使用 `@InjectSettings` 注解：

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("jdbcSettings.jdbcDriver")
        private String jdbcDriver;
        @InjectSettings("jdbcSettings.jdbcURL")
        private String jdbcURL;
        @InjectSettings("jdbcSettings.user")
        private String user;
        @InjectSettings("jdbcSettings.password")
        private String password;
        ...
    }

最后从 AppCcontext 中创建这个 Bean 就好了。由于使用到了配置文件，您需要在创建 Hasor 的时候指定一下配置文件即可。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext("hasor-config.xml");

小贴士：像这样 @InjectSettings 还会支持类型的自动转换。

.. code-block:: java
    :linenos:

    public class TestBean {
        @InjectSettings("userInfo.myAge")
        private int myAge;
    }

注入环境变量
------------------------------------
环境变量，指的是操作系统层面设置的环境变量，例如：JAVA_HOME，还有当前登录用户的主目录：USER.HOME。
当然这些环境变量你也可以通过“System.getenv()” 或 “System.getProperties()” 自己拿到。

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("${JAVA_HOME}")
        private String javaHome;
    }

为了保密，我们选择在应用程序启动的时候通过 “-D” 参数把用户名、密码传递给程序。然后让 Hasor 框架为我们把传入的敏感信息，注入到 DataBaseBean 类中。

.. code-block:: java
    :linenos:

    public class DataBaseBean {
        @InjectSettings("${db.user}")
        private String user;
        @InjectSettings("${db.pwd}")
        private String password;
        ...
    }
    public class TestMain {
        public static void main(String[] args) throws Throwable {
            AppContext appContext = Hasor.createAppContext();
            appContext.getInstance(DataBaseBean.class);
        }
    }

最后我们要通过命令行的方式启动这个程序：
`java TestMain -Ddb.user=username -Ddb.pwd=password`