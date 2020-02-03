--------------------
函数/函数包
--------------------

开发Udf
------------------------------------
一个 UDF 必须是实现了 ``net.hasor.dataql.Udf`` 接口，例如下面这个 Udf。

.. code-block:: java
    :linenos:

    public class UserByIdUdf implements Udf {
        private UserManager userManager;
        @Override
        public Object call(Hints readOnly, Object[] params) {
            return userManager.findById(params[0]);
        }
    }

注册Udf的方式和添加全局变量相同，这里不再复述。最后执行查询并提取姓名和性别：

.. code-block:: java
    :linenos:

    DataQL dataQL = appContext.getInstance(DataQL.class);//得到 DataQL接口
    Query dataQuery = dataQL.createQuery("return findUserById(1) => { 'name','sex' }"); // 创建查询
    QueryResult queryResult = dataQuery.execute();//执行查询
    DataModel dataModel = queryResult.getData();//获得查询结果

参数中的Udf
------------------------------------
DataQL 允许在执行查询时通过参数形式提供 Udf 。这种方式传入的 Udf 在调用时也需要使用 ``${...}`` 来获取，例如：

.. code-block:: java
    :linenos:

    HashMap<String, Object> tempData = new HashMap<String, Object>() {{
        put("findUserById", new UserByIdUdf());
    }};
    AppContext appContext = Hasor.create().build();
    DataQL dataQL = appContext.getInstance(DataQL.class);//得到 DataQL接口
    Query dataQuery = dataQL.createQuery("return ${findUserById}(1) => { 'name','sex' }"); // 创建查询
    QueryResult queryResult = dataQuery.execute(tempData);
    DataModel dataModel = queryResult.getData();

函数包(UdfSource)
------------------------------------
``UdfSource`` 是一个函数包接口，接口中只有一个 ``getUdfResource`` 方法，用于返回函数包中的所有 Udf（Map形式返回）但是一般情况下更推荐使用 ``UdfSourceAssembly`` 接口。

使用函数包的好处是可以像平常开发一样编写 Udf，无需考虑 Udf 接口的细节。装配器会自动帮助进行参数和结果的转换。例如：

.. code-block:: java
    :linenos:

    public class DateTimeUdfSource implements UdfSourceAssembly {
        /** 返回当前时间戳 long 格式 */
        public long now() { ... }
        /** 返回当前系统时区的：年 */
        public int year(long time) { ... }
        /** 返回当前系统时区的：月 */
        public int month(long time) { ... }
        /** 返回当前系统时区的：日 */
        public int day(long time) { ... }
        ...
    }

最后在查询中通过 ``<函数包名>.<函数>`` 的形式调用函数包。


import导入(函数/函数包)
------------------------------------
如果Classpath 中已经存在某个 Udf 类，还可以通过 ``import`` 语句导入使用。

.. code-block:: js
    :linenos:

    import @'net.xxxx.foo.udfs.UserByIdUdf' as findUserById;
    return findUserById(1) => { 'name','sex' }

函数包的导入语句相同，只是在调用函数包中函数的时需要指明函数包，例如：

.. code-block:: js
    :linenos:

    import @'net.xxxx.foo.udfs.DateTimeUdfSource' as timeUtil;
    return timeUtil.now()


使用注解批量注册
------------------------------------
通过 ``@DimUdf`` 注解可以快速的声明函数：

.. code-block:: java
    :linenos:

    @DimUdf("findUserById")
    public class UserByIdUdf implements Udf {
        private UserManager userManager;
        @Override
        public Object call(Hints readOnly, Object[] params) {
            return userManager.findById(params[0]);
        }
    }

通过 ``@DimUdfSource`` 注解可以快速的声明函数包：

.. code-block:: java
    :linenos:

    @DimUdfSource("time_util")
    public class DateTimeUdfSource implements UdfSourceAssembly {
        ...
    }

然后在初始化时扫描加载它们：

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build(apiBinder -> {
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        queryBinder.loadUdf(queryBinder.findClass(DimUdf.class));
        queryBinder.loadUdfSource(queryBinder.findClass(DimUdfSource.class));
    });
