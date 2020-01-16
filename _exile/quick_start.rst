处理Request请求
------------------------------------
Hasor 套件中携带了 web 子框架，通过前面的工程配置，您的项目已经工作在 Web 子框架之下。
对于一个 web 应用程序第一件重要的事就是接收 Request 请求并处理。
下面我们创建一个 Hasor 的请求处理器，来处理我们的 `/my/my.htm` 请求。`execute` 方法是 Hasor 处理请求的执行入口。

.. code-block:: java
    :linenos:

    import net.hasor.web.WebController;
    public class My extends WebController {
        public void execute(){
            ...
        }
    }


接下来将我们的请求处理类配置到 Hasor 框架中。

.. code-block:: java
    :linenos:

    package net.demo.core;
    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            apiBinder.mappingTo("/my/my.htm").with(My.class);
            ...
        }
    }


上面这种配置方式的优点是可以统一管理所有 Action 的注册，缺点是每新增一个 Action 都要进行注册，这会比较麻烦。
我们可以通过在请求处理器上标记 `@MappingTo` 注解，然后通过扫描的方式自动配置。例如：

.. code-block:: java
    :linenos:

    import net.hasor.web.WebController;
    @MappingTo("/my/my.htm")
    public class My extends WebController {
        public void execute(){
            ...
        }
    }

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...
            // 扫描所有带有 @MappingTo 特征类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            // 对 aClass 集合进行发现并自动配置控制器
            apiBinder.looking4MappingTo(aClass);
            ...
        }
    }


配置模板引擎
------------------------------------
本例使用 freemarker 作为渲染引擎来处理 Response。
Hasor 内置了 freemarker 渲染引擎的封装，因此本小结会以两种方式来讲解如何搞定一个渲染引擎。
首先无论哪种方式您都要先引入 freemarker 的 jar 包依赖。

.. code-block:: xml
    :linenos:

        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.23</version>
        </dependency>


**方式1：**
最简的方式就是使用 Hasor Plugins 封装好的渲染引擎。首先添加下面这个插件依赖，然后配置渲染器。
如果您对 Hasor 插件自带的渲染器有制定的需求，例如：增加 freemarker 的 shareVars。
那么可以 继承 FreemarkerRender 来扩展您的需要。

.. code-block:: xml
    :linenos:

    <!-- 渲染器插件依赖 -->
    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-plugins</artifactId>
        <version>3.2.2</version>
    </dependency>

    配置渲染器
    apiBinder.suffix("htm").bind(FreemarkerRender.class);


**方式2：**
自己封装 freemarker 到 Hasor中，不使用 hasor-plugins 中的插件。

.. code-block:: java
    :linenos:

    public class FreemarkerRender implements RenderEngine {
        protected Configuration freemarker;

        /* 初始化引擎 */
        public void initEngine(AppContext appContext) throws Throwable {
            ...
            ServletContext servletContext = appContext.getInstance(ServletContext.class);
            ...
            this.freemarker = ...
            ...
        }

        /* 在执行 process 之前 Hasor 会调用渲染器来判断是否可以处理这个渲染，
           如果不能处理那么 Hasor 就把它交还给 Servlet 容器处理 */
        public boolean exist(String template) throws IOException {
            return freemarker.getTemplateLoader().findTemplateSource(template) != null;
        }

        /* 执行渲染引擎，渲染模板结果到 writer 中即可 */
        public void process(RenderInvoker renderData, Writer writer) throws Throwable {
            Template temp = this.freemarker.getTemplate(renderData.renderTo());
            if (temp == null)
                return;

            HashMap<String, Object> data = new HashMap<String, Object>();
            for (String key : renderData.keySet()) {
                data.put(key, renderData.get(key));
            }
            temp.process(data, writer);
        }
    }


最后在把您自定义的渲染器注册到 Hasor 中即可

.. code-block:: java
    :linenos:

    apiBinder.suffix("htm").bind(FreemarkerRender.class);


使用模板引擎
------------------------------------
经过前面的配置，我们Web开发的几个重要元素都已经齐备。现在来展示一下如何使用 Hasor 进行 Web MVC 的开发。
首先编写一个用于处理 Request 请求的控制器，我们以大家都非常熟悉的登录场景为例：


.. code-block:: java
    :linenos:

    @MappingTo("login.htm")
    public class Login extends WebController {
        public void execute() throws IOException {

            String username = getPara("username");
            String password = getPara("password");
            boolean authCheck = ...

            if ( authCheck ) {
                putData("messageInfo", "登录成功.");
                renderTo("htm", "succeed.htm");
            } else {
                putData("messageInfo", "登录失败.");
                renderTo("htm", "failed.htm");
            }
        }
    }


接着我们需要三个页面分别是：login.htm、succeed.htm、failed.htm

.. code-block:: html
    :linenos:

    // login.htm
    <!DOCTYPE html>
    <html lang="en"><body>
        <form action="login.htm" method="post">
            账号：<input name="username" type="text"/></br>
            密码：<input name="password" type="text"/></br>
            <input type="submit" value="登录"/>
        </form>
    </body></html>

    // succeed.htm
    <!DOCTYPE html>
    <html lang="en"><body>
        成功消息：${messageInfo}
    </body></html>

    // failed.htm
    <!DOCTYPE html>
    <html lang="en"><body>
        失败消息：${messageInfo}
    </body></html>


配置文件
------------------------------------
启动 Hasor 通常您不需要配置任何配置文件，Hasor 会自动从它jar包中加载默认配置。
但是通常我们的应用程序都有一些自己的专有信息需要通过配置文件来承载，例如：数据库连接串。

Hasor 支持 Xml 和 Properties 两种格式的配置文件作为输入。
当配置文件名以 “.xml” 结尾时会被判定为 xml 类型，其它类型输入都会被归类到 Properties 类型。

我们以 Xml 方式为例。首先，新建一个 Xml 文件，并命名为 ``hasor-config.xml`` 您需要把它放置在 classpath 的跟路径下。
在您没有明确指定具体名称时，Hasor 会尝试加载位于 classpath 中的 ``hasor-config.xml`` 配置文件。这个配置文件的基本内容如下：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://www.hasor.net/sechma/main">
        <!-- 可选项，建议配置成你的应用程序所处包 -->
        <hasor.loadPackages>net.demo.hasor.*</hasor.loadPackages>

        <!-- 你自己的应用配置 -->
        <myApp>
            <jdbcURL>jdbc:mysql://127.0.0.1:3306/test</jdbcURL>
            <userName>sa</userName>
            <userPassword></userPassword>
        </myApp>
    </config>


.. Note::
    Hasor 配置有包扫描功能，当遇到需要扫描包中类时候 Hasor 会根据预先配置的范围进行扫描，为了尽量缩短扫描范围提升时间。
    我们一般会重新配置 ``hasor.loadPackages`` 选项。


接下来最后一个环节读取这些配置，并替换之前写死在代码里的那些数据库配置信息。下面是在应用程序 init 阶段，读取配置文件的样例代码：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext(new Module() {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            Settings settings = apiBinder.getEnvironment().getSettings();
            String urlStr = settings.getString("myApp.jdbcURL");
            String userStr = settings.getString("myApp.userName");
            String pwdStr = settings.getString("myApp.userPassword");
            ......
        }
    }
    // 或者可以通过 appContext 来获取。
    Settings settings = appContext.getEnvironment().getSettings();
    String urlStr = settings.getString("myApp.jdbcURL");
    String userStr = settings.getString("myApp.userName");
    String pwdStr = settings.getString("myApp.userPassword");


.. Note::
    Hasor 读取 xml 配置文件的规则可以简单理解为将元素节点的父子关系以 ``.`` 进行连接。
    例如：上面配置文件中元素 <jdbcURL> 的节点为 ``myApp.jdbcURL``，其中根元素默认省略不写。


读写数据库
------------------------------------
在使用 Hasor 数据库框架之前需要先引入 Hasor 数据库框架的依赖，如下：

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-db</artifactId>
        <version>3.2.2</version>
    </dependency>


例如我们使用 c3p0 作为数据库连接池，连接并操作我们的数据库，首先要做的就是创建连接池然后初始化 Hasor 的数据库框架。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.createAppContext(new Module() {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("......");
            dataSource.setJdbcUrl("......");
            dataSource.setUser("......");
            dataSource.setPassword("......");
            //
            apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
        }
    });


当我们想使用数据库连接时，只需要获取 ``JdbcTemplate`` 接口即可。下面列出了最简单方式获取 JdbcTemplate 接口的方法。

.. code-block:: java
    :linenos:

    JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);


熟悉 SpringJDBC 的同学可能会比较容易上手，Hasor 的 JdbcTemplate 接口正式来自于 SpringJDBC。
只不过在接口上做了大量精简工作，并完全重新实现。在功能保留不变的情况下精简了 90% 的代码量。


数据库事务
------------------------------------
您在 ``new JdbcModule(Level.Full, dataSource)`` 的时候，它会为您自动的配置相关的数据库事务管理器，您不需要为了事务管理做任何多余配置。
Hasor 的事务管理十分强大，它支持多达七种事务传播属性以及全部的事务隔离级别。
即便是配置了多数据源的场景，它也可以很好的在混合使用情况下，为每个数据源提供独立的事务控制功能。

在 Hasor 中进行事务控制有三个途径：
    - 第一种，通过 ``@Transactional`` 注解方式。
    - 第二种，通过 ``TransactionTemplate`` 接口。
    - 第三种，通过 ``TransactionManager`` 事务管理器接口手动控制事务。

在本节会展示第一种注解方式的事务控制，注解方式这种用途比较广泛，用起来也十分简单方便。
您只要在方法上加上一个注解，当方法之行完毕，同时没有异常抛出时，事务就会被递交到数据库。
具体示例如下：

.. code-block:: java
    :linenos:

    public class TradeService {
        @Transactional
        public boolean payItem(long itemId , String creditCard){
            ....
        }
        @Transactional
        public boolean check(long itemId , String creditCard){
            ....
        }
    }


.. Note::

    **嵌套事务**：Hasor 的事务管理是自动支持嵌套事务的，您无需做任何配置和干预。
    例如上面 ``TradeService`` 类，假定 payItem 方法中又调用了 check 方法，这就组成了一层的嵌套事务。
    在 Hasor 中嵌事务的层数是没有限制的，只要 jvm 堆栈允许您可以一直创建下去。

默认使用的事务传播属性配置为：REQUIRED - 尝试加入已经存在的事务中，如果没有则开启一个新的事务。
如果你想修改事务的传播级别为其它的，例如使用独立事务。那么可以这样修改 ``@Transactional`` 注解

.. code-block:: java
    :linenos:

    @Transactional(propagation = Propagation.REQUIRES_NEW)


IoC
------------------------------------
IoC 是 Hasor 提供的一项基础功能，在使用这两个功能时您无需引入任何包。
下面我们通过整合上述的功能到为契机，演示一下如何使用 IoC 将前面介绍过的 MVC、数据库操作、事务、Web 整合到一起。

首先改造 StartModule 将渲染引擎、数据库方面的初始化等整合到一起。


.. code-block:: java
    :linenos:

    public class StartModule extends WebModule {
        public void loadModule(WebApiBinder apiBinder) throws Throwable {
            ...

            // 一、自动发现并配置 WebController
            //  - 扫描所有带有 @MappingTo 特征类
            Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
            //  - 对 aClass 集合进行发现并自动配置控制器
            apiBinder.looking4MappingTo(aClass);
            ...

            // 二、配置页面渲染引擎，使用 freemarker
            apiBinder.suffix("htm").bind(FreemarkerRender.class);
            ...

            // 三、使用配置文件配置数据库
            //  - 数据库连接池
            Settings settings = apiBinder.getEnvironment().getSettings();
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("......");
            dataSource.setJdbcUrl(settings.getString("myApp.jdbcURL"));
            dataSource.setUser(settings.getString("myApp.userName"));
            dataSource.setPassword(settings.getString("myApp.userPassword"));
            //  - 数据库框架
            apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
            ...
        }
    }


然后我们新建一个 Dao 类，用于承载业务上所有数据库操作，然后利用 IoC。将 JdbcTemplate 接口注入进去。

.. code-block:: java
    :linenos:

    public class MyDAO {
        // 依赖注入 JdbcTemplate 到 MyDAO 中
        @Inject
        private JdbcTemplate jdbcTemplate;

        ...
        // 根据用户名获取用户
        public User getUserByUserName(String userName) {
            String querySQL =
                    "select userName,userPassword where UserInfo where userName = ?";
            return jdbcTemplate.queryForObject(querySQL, User.class, userName);
        }
    };


由于我们的场景比较简单，下面就以 WebController 代替 Manager。我们在做用户 check 操作时使用数据库事务。
下面改造 login 请求处理器。

.. code-block:: java
    :linenos:

    @MappingTo("login.htm")
    public class Login extends WebController {
        @Inject
        private MyDAO myDAO;

        @Transactional // 数据库事务控制注解
        public void execute() throws IOException {

            String username = getPara("username");
            String password = getPara("password");
            User userInfo = myDAO.getUserByUserName(username);

            password = password ==null ? "" : password;
            if (userInfo !=null && password.equals(userInfo.getUserPassword())  ) {
                putData("messageInfo", "登录成功.");
                renderTo("htm", "succeed.htm");
            } else {
                putData("messageInfo", "登录失败.");
                renderTo("htm", "failed.htm");
            }
        }
    }


示例项目
------------------------------------
最后这里是示例项目的下载地址，祝您使用 Hasor 的路途愉快：
http://files.hasor.net/resources/example-hasor.zip

