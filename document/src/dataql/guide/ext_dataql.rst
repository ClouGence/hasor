外部代码片段(FragmentProcess)
------------------------------------
外部代码片段是 DataQL 特有能力，它允许在 DataQL 查询中混合其它语言的脚本。并将引入的外部语言脚本转换为 Udf 形式进行调用。

使用这一特性时需要扩展 FragmentProcess 接口，并注册对应的外部语言执行器。例如：增强 DataQL 让其可以执行数据库查询的 SQL 语句

.. code-block:: js
    :linenos:

    var dataSet = @@sql(item_code) <%
        select * from category where co_code = :item_code
    %>
    return dataSet() => [
        { "id","name","code","body" }
    ]

在上面查询中通过 ``@@`` 指令，开启了一段外部代码片段的定义。这个外部代码片段的执行器名字是 ``sql``

要想实现上述功能，首先编写一个外部代码执行器。执行器接口中接收 ``<%``、``%>``包裹的所有代码然后调用 JdbcTemplate 类的 query 系列方法执行SQL 查询。

.. code-block:: java
    :linenos:

    @DimFragment("sql")
    public class SqlQueryFragment implements FragmentProcess {
        @Inject
        private JdbcTemplate jdbcTemplate;

        @Override
        public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
            return this.jdbcTemplate.queryForList(fragmentString, paramMap);
        }
    }

然后在初始化阶段注册这个外部代码执行器，之后就可以在查询中使用这个外部代码片段了。

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build((QueryModule) apiBinder -> {
        //扫描所有标记了@DimFragment注解的类并加载它
        apiBinder.loadFragment(queryBinder.findClass(DimFragment.class));
    });


资源加载器(Finder)
------------------------------------
资源加载器完整名称是 ``net.hasor.dataql.Finder`` 其主要负责 import 语句导入资源/对象的加载。
通常情况下不会接触到它。

例如：下面这个查询语句中通过 import 引用一个容器中的一个 Bean。这就需要用到 Finder。

.. code-block:: js
    :linenos:

    import 'userBean' as ub;//userBean 是 Bean 的名字
    return ub().name;


下面是 Finder 接口的定义。

.. code-block:: java
    :linenos:

    public interface Finder {
        // 负责处理 import @"/net/hasor/demo.ql" as demo; 的资源加载
        public InputStream findResource(String resourceName);

        // 负责处理 <code>import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;</code>方式的资源的加载。
        public Object findBean(String beanName);

        // 负责处理 <code>import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;</code>方式的资源的加载。
        public Object findBean(Class<?> beanType);
    }

.. CAUTION::
    在默认的接口实现中，findBean(String) 会加载 Class 然后调用 findBean(Class<?>) 完成最终的加载。
    ``findBean(String)``、``findBean(Class<?>)`` 这一组方法建议同时重写。