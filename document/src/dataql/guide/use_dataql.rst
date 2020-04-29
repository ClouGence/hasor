--------------------
执行查询
--------------------

引入依赖
------------------------------------
无论接下来以何种方式使用 DataQL 都需要引入依赖：

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-dataql</artifactId>
        <version>4.1.4</version>
    </dependency>

通过Hasor使用DataQL
------------------------------------
由于 ``AppContext`` 有自身的声明周期特性，因此需要做一个单例模式来创建 DataQL 接口。

.. code-block:: java
    :linenos:

    public class DataQueryContext {
        private static AppContext appContext = null;
        private static DataQL     dataQL     = null;
        public static DataQL getDataQL() {
            if (appContext == null) {
                appContext = Hasor.create().build();
                dataQL = appContext.getInstance(DataQL.class);
            }
            return dataQL;
        }
    }


然后执行查询

.. code-block:: java
    :linenos:

    HashMap<String, Object> tempData = new HashMap<String, Object>() {{
        put("uid", "uid is 123");
        put("sid", "sid is 456");
    }};
    DataQL dataQL = DataQueryContext.getDataQL();
    Query dataQuery = dataQL.createQuery("return [${uid},${sid}]");
    QueryResult queryResult = dataQuery.execute(tempData);
    DataModel dataModel = queryResult.getData();

dataModel 的值就是 ``['uid is 123','sid is 456']``


通过JSR223使用DataQL
------------------------------------
JDK1.6开始 Java引入了 JSR223 规范，通过该规范可以用一致的形式在JVM上执行一些脚本语言。
DataQL 默认实现了这一规范的接入API，这就使得开发者可以脱离 Hasor 容器的特性独立的使用 DataQL 查询引擎。
这种方法无需考虑 Hasor 容器生命周期问题。

.. code-block:: java
    :linenos:

    ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("dataql");
    SimpleScriptContext params = new SimpleScriptContext();
    params.setBindings(scriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE);
    params.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
    params.setAttribute("uid", "uid form engine", ScriptContext.ENGINE_SCOPE);
    params.setAttribute("sid", "sid form global", ScriptContext.GLOBAL_SCOPE);
    //
    Object eval = scriptEngine.eval("return [${uid},${sid}]", params);
    DataModel dataModel = ((QueryResult) eval).getData();

dataModel 的值就是 ``['uid form engine','sid form global']``

.. HINT::
    在 JSR223 方式下：不提供CustomizeScope接口支持。三个Dim注解无效：@DimFragment、@DimUdf、@DimUdfSource

基于底层接口使用DataQL
------------------------------------
这种方式更加底层，依然是脱离了 Hasor 容器环境来执行 DataQL 查询。
事实上无论是 Hasor 方式还是 JSR223 方式它们都是通过这个底层接口封装 DataQL 的。

DataQL 的运行基于三个步骤： 1.解析DataQL查询、 2.编译查询、 3.执行查询。

**解析DataQL查询**

解析 DataQL 查询就是把 DataQL 查询字符串通过解析器解码为 AST(抽象语法树)

.. code-block:: java
    :linenos:

    QueryModel queryModel = QueryHelper.queryParser(query1);


**编译查询**

编译是指将DataQL 的 AST(抽象语法树) 编译为 QIL 指令序列。

.. code-block:: java
    :linenos:

    QIL qil = QueryHelper.queryCompiler(queryModel, null, Finder.DEFAULT);


**执行查询**

最后在根据 QIL 创建对应的 Query 接口即可。

.. code-block:: java
    :linenos:

    Query dataQuery = QueryHelper.createQuery(qil, Finder.DEFAULT);

.. HINT::
    在实际开发中可以最大限度的挖掘 ``QueryHelper`` 接口，没有必要严格照搬上述三个步骤。


查询接口(Query)
------------------------------------
无论使用的是何种方式查询都会通过 DataQL 的查询接口发出查询指令。查询接口的完整类名为 ``net.hasor.dataql.Query``，接口源码为：

.. code-block:: java
    :linenos:

    /** 执行查询 */
    public default QueryResult execute() throws InstructRuntimeException {
        return this.execute(symbol -> Collections.emptyMap());
    }
    /** 执行查询 */
    public default QueryResult execute(Map<String, ?> envData) throws InstructRuntimeException {
        return this.execute(symbol -> envData);
    }
    /** 执行查询 */
    public default QueryResult execute(Object[] envData) throws InstructRuntimeException {
        if (envData == null) {
            return this.execute(Collections.emptyMap());
        }
        Map<String, Object> objectMap = new HashMap<>();
        for (int i = 0; i < envData.length; i++) {
            objectMap.put("_" + i, envData[i]);
        }
        return this.execute(objectMap);
    }
    /** 执行查询 */
    public QueryResult execute(CustomizeScope customizeScope) throws InstructRuntimeException;


查询接口提供了三种不同参数类型的查询重载，所有入参数最后都被转换成为 ``Map`` 结构然后统一变换成为 ``CustomizeScope`` 数据域形式。

.. HINT::
    有关数据域的作用请查阅 ``语法手册->访问符->取值域`` 的相关内容。


查询结果(QueryResult)
------------------------------------
发出DataQL查询后，如果顺利执行完查询，结果会以 ``QueryResult`` 接口形式返回。QueryResult 接口定义了四个方法来获取返回值相关信息。

.. code-block:: java
    :linenos:

    /** 执行结果是否通过 EXIT 形式返回的 */
    public boolean isExit();
    /** 获得退出码。如果未指定退出码，则默认值为 0 */
    public int getCode();
    /** 获得返回值 */
    public DataModel getData();
    /** 获得本次执行耗时 */
    public long executionTime();


.. HINT::
    DataQL 的所有返回值都会包装成 ``DataModel`` 接口类型。如果想拿到 ``Map/List`` 结构数据，只需要调用 ``unwrap`` 方法即可。
