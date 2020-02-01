--------------------
引入和使用
--------------------

引入依赖
------------------------------------
无论接下来以何种方式使用 DataQL 都需要引入依赖：

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-dataql</artifactId>
        <version>4.1.0</version>
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


然后利用整个单例执行查询

.. code-block:: java
    :linenos:

    HashMap<String, Object> tempData = new HashMap<String, Object>() {{
        put("uid", "uid is 123");
        put("sid", "sid is 456");
    }};
    DataQL dataQL = DataQueryContext.getDataQL();
    QueryResult queryResult = dataQL.createQuery("return [${uid},${sid}]").execute(tempData);
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

    QIL qil = QueryHelper.createQuery(qil, Finder.DEFAULT);

.. HINT::
    在实际开发中可以最大限度的挖掘 ``QueryHelper`` 接口，没有必要严格照搬上述三个步骤。
