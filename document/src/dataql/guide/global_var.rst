全局变量
------------------------------------
添加全局变量有两种方式，两种方式没有分别。

**方式1**
    - 在 QueryModule 中初始化环节添加

.. code-block:: java
    :linenos:

    AppContext appContext = Hasor.create().build((QueryModule) apiBinder -> {
        apiBinder.addShareVarInstance("global_var", "g1");
    });

**方式2**
    - 通过 DataQL 接口添加

.. code-block:: java
    :linenos:

    DataQL dataQL = appContext.getInstance(DataQL.class);
    dataQL.addShareVarInstance("global_var", "g2");

下面执行 DataQL 查询获取这个全局变量

.. code-block:: js
    :linenos:

    return global_var;
