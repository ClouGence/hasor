在页面中配置，在程序中调用
------------------------------------
Dataway 在 4.1.4 版本开始提供了 ``DatawayService`` 接口。通过这个接口，允许应用通过这个接口来调用界面中配置的 DataQL 查询。

使用这个功能首先需要获取到 ``DatawayService`` 接口：

.. code-block:: java
    :linenos:

    AppContext appContext = ...;
    DatawayService dataway = appContext.getInstance(DatawayService.class);


接着通过 API 的 Method 和 Path 就可以直接调用它了，需要注意的是被调用的 DataQL API 必须是处于发布状态的。

.. code-block:: java
    :linenos:

    // 参数
    Map<String, Object> paramData = new HashMap<>() {{
        put("userName", "1");
    }}
    // 结果
    Map<String, Object> result = dataway.invokeApi("post", "/api/demos/find_user_by_name", paramData);


PreExecute拦截器
------------------------------------
当一个API发起调用之后，可以通过API请求拦截器处理一些特殊的逻辑。比方说下面这些场景：

- 所有 API 请求都加入某个固定的参数
- 通过拦截器实现接口权限控制
- 配合 ResultProcessChainSpi 对接口进行结果缓存。

.. code-block:: java
    :linenos:

    // 处理逻辑
    public class MyPreExecuteChainSpi implements PreExecuteChainSpi {
        public void preExecute(ApiInfo apiInfo, BasicFuture<Object> future) {
            ...
        }
    }
    // 注册接口拦截器
    public class ExampleModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindSpiListener(PreExecuteChainSpi.class, new MyPreExecuteChainSpi());
        }
    }


.. HINT::
    查询执行拦截器接口名为 ``net.hasor.dataway.spi.PreExecuteChainSpi`` 它是一个 ChainSpi。
    这意味着 ``PreExecuteChainSpi`` 可以串成一个串来执行。


**所有 API 请求都加入某个固定的参数**

.. code-block:: java
    :linenos:

    apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
        apiInfo.getParameterMap().put("self", "me");
    });


**没有权限抛出异常**

.. code-block:: java
    :linenos:

    apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
        String apiPath = apiInfo.getApiPath();
        String apiMethod = apiInfo.getMethod()
        if (...) {
            // （方式1）通过 future 设置异常信息
            future.failed(new StatusMessageException(401, "not power"));
            // （方式2）或者直接 throw 一个异常
            throw new StatusMessageException(401, "not power");
        }
    });
    // Result
    // {
    //   "success": false,
    //   "message": "not power",
    //   "code": 401,
    //   "lifeCycleTime": 42,
    //   "executionTime": -1,
    //   "value": "not power"
    // }


**返回预先准备好的数据**

.. code-block:: java
    :linenos:

    apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
        String apiPath = apiInfo.getApiPath();
        String apiMethod = apiInfo.getMethod()
        if (...) {
            future.completed(...);
        }
    });
    // Result
    // {
    //   "success": true,
    //   "message": "OK",
    //   "code": 0,
    //   "lifeCycleTime": 22,
    //   "executionTime": 21,
    //   "value": ...
    // }


ResultProcess拦截器
------------------------------------
一个已经发布的接口被调用之后，一定会触发这个拦截器。而 ``ResultProcessChainSpi`` 拦截器的处理有两个方法，分别应对了两个不同的情况：

- callAfter：结果拦截，用于处理 Query 正确执行之后的二次结果处理。
- callError：异常拦截，当 Query 执行发生异常时。

.. HINT::
    异常拦截器十分强大，除了 DataQL 执行异常之外。它还能拦截 ``PreExecuteChainSpi`` 的异常。
    甚至它还可以拦截自己 ``callAfter`` 过程引发的异常。

.. HINT::
    查询执行拦截器接口名为 ``net.hasor.dataway.spi.ResultProcessChainSpi`` 它也是一个 ChainSpi。


**响应结果改写**

.. code-block:: java
    :linenos:

    // 所有返回的结果，都把 API 的 Method 和 path 返回
    apiBinder.bindSpiListener(ResultProcessChainSpi.class, new ResultProcessChainSpi() {
        public Object callAfter(boolean formPre, ApiInfo apiInfo, Object result) {
            return new HashMap<String, Object>() {{
                put("method", apiInfo.getMethod());
                put("path", apiInfo.getApiPath());
                put("result", result);
            }};
        }
    });

    // DataQL 查询
    //   return 123
    //
    // Result
    // {
    //   "success": true,
    //   "message": "OK",
    //   "code": 0,
    //   "lifeCycleTime": 14,
    //   "executionTime": 8,
    //   "value": {
    //     "method": "POST",
    //     "path": "/api/demos/find_user_by_name_post",
    //     "result": 123
    //   }
    // }


**异常统一处理**

.. code-block:: java
    :linenos:

    // 所有返回的结果，都把 API 的 Method 和 path 返回
    apiBinder.bindSpiListener(ResultProcessChainSpi.class, new ResultProcessChainSpi() {
        public Object callError(boolean formPre, ApiInfo apiInfo, Throwable e) {
            return new HashMap<String, Object>() {{
                put("method", apiInfo.getMethod());
                put("path", apiInfo.getApiPath());
                put("errorMessage", e.);
            }};
        }
    });

    // DataQL 查询
    //   throw 123
    //
    // Result
    // {
    //   "success": false,
    //   "message": "0 : 123",
    //   "code": 0,
    //   "lifeCycleTime": 320,
    //   "executionTime": 39,
    //   "value": {
    //     "path": "/api/demos/find_user_by_name_post",
    //     "method": "POST",
    //     "errorMessage": "0 : 123"
    //   }
    // }


**实现调用缓存**

.. code-block:: java
    :linenos:

    public class ApiCacheSpi implements PreExecuteChainSpi, ResultProcessChainSpi {
        private Map<String,Object> cacheMap = ... // for example

        public void preExecute(ApiInfo apiInfo, BasicFuture<Object> future) {
            String cacheKey = ...
            if (this.cacheMap.containsKey(cacheKey)) {
                Object cacheValue = cacheMap.get(cacheKey);
                future.completed(cacheValue);
                return;
            }
        }

        public Object callAfter(boolean formPre, ApiInfo apiInfo, Object result) {
            // formPre 为 true，表示 preExecute 已经处理过。
            // apiInfo.isPerform() 为 true 表示，API 调用是从 UI 界面发起的。
            if (formPre || apiInfo.isPerform()) {
                return result;
            }
            //
            String cacheKey = ...
            this.cacheMap.put(cacheKey, result);
            return result;
        }
    }


Compiler拦截器
------------------------------------
``CompilerSpiListener`` 也叫做编译拦截器，DataQL 在真正执行查询之前调用。

如果当 ``PreExecuteChainSpi`` 中已经通过 ``future.completed`` 或者 ``future.failed`` 处理了请求，那么就不会引发 ``CompilerSpiListener``。

编译拦截器的应用场景主要有两个

- 实现对 QIL 缓存（QIL 是 DataQL 查询编译之后的指令序列，它类似 Java 的 class 文件）
- 改写或替换 DataQL 查询脚本


**QIL 缓存**

.. code-block:: java
    :linenos:

    public class QilCacheSpi implements CompilerSpiListener {
        private Map<String, QIL> menCache = new ConcurrentHashMap<>();

        public QIL compiler(ApiInfo apiInfo, String query, Set<String> varNames, Finder finder) throws IOException {
            String apiPath = apiInfo.getApiPath();
            if (apiPath.startsWith("/dataql/api/maps/")) {
                if (this.menCache.containsKey(apiPath)) {
                    return this.menCache.get(apiPath);
                }
                QIL compiler = CompilerSpiListener.DEFAULT.compiler(apiInfo, query, varNames, finder);
                this.menCache.put(apiPath, compiler);
                return compiler;
            }
            return CompilerSpiListener.DEFAULT.compiler(apiInfo, query, varNames, finder);
        }
    }

**改写 DataQL 查询**

在所有DataQL 查询的前面都统一追加一个 hint。

.. code-block:: java
    :linenos:

    public class QilCacheSpi implements CompilerSpiListener {
        public QIL compiler(ApiInfo apiInfo, String query, Set<String> varNames, Finder finder) throws IOException {
            query = "hint XXXXX = true; " + query; // 增加一个 XXXXX hint
            return CompilerSpiListener.DEFAULT.compiler(apiInfo, query, varNames, finder);
        }
    }
