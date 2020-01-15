--------------------
函数
--------------------

外部函数
------------------------------------
并非通过 DataQL 语句定义的函数都是外部函数。使用外部函数通常需要编写 Java 代码，然后在 DataQL 查询中将其导入之后才可以使用。

例如：下面这个函数就是返回一个 ``DataBean`` 对象：

.. code-block:: java
    :linenos:

    public class DemoUdf implements Udf {
        @Override
        public Object call(Hints readOnly, Object[] params) {
            return new DataBean();
        }
    }

然后通过 ``import`` 语句导入，导入之后就可以正常使用这个函数了。

.. code-block:: js
    :linenos:

    import 'net.demo.packages.DemoUdf' as demo;
    var data = demo() => { ... };
    ...

.. HINT::
    使用外部函数需要用到的语句是 import 语句，更多外部函数的技巧需要在开发手册中查阅。

定义函数
------------------------------------
在 DataQL 查询中可以定义一个函数，然后在后续查询中使用它。一个典型的场景就是对性别字段的转换。定义函数的语法如下：

.. code-block:: js
    :linenos:

    var foo = (param1,param2,...) -> {
        <语句1>;
        <语句2>
        <语句3>
    }

其中 `foo` 是函数名，`param1`、`param2`是函数的参数。``{ ... }`` 花括号中间是函数体。定义的函数和外部函数的用法完全一样，只是函数的逻辑部分是通过 DataQL 来编写的。这有点类似 ``存储过程``。

.. code-block:: js
    :linenos:

    var foo = () -> {
        return { ... } // 返回一个数据体
    }
    var data = foo() => { ... }; // 对 foo 数据体进行结构变换
    ...

Lambda
------------------------------------
例如在使用 ``net.hasor.dataql.sdk.CollectionUdfSource`` 函数包进行数据过滤时，需要指定一个过滤函数。在不使用 Lambda 表达式之前需要定义一个函数。

.. code-block:: js
    :linenos:

    import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
    var dataSet = ...
    var foo = (dat) -> {
        return test.parent_id == null; // 返回一个数据体
    }
    return collect.filter(dataSet, foo);


Lambda 可以帮助减少对函数的声明，例如简写为：

.. code-block:: js
    :linenos:

    import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
    var dataSet = ...
    return collect.filter(dataSet, (dat) -> { return test.parent_id == null; });

外部代码片段
------------------------------------
DataQL 允许在查询中混合其它查询语言，一个典型的场景是把 SQL 语句混合在 DataQL 查询中。例如：

.. code-block:: js
    :linenos:

    var dataSet = @@sql(item_code) <%
        select * from category where co_code = :item_code
    %>
    return dataSet() => [
        { "id","name","code","body" }
    ]

定义一个外部执行片段需要使用 ``@@xxxx<% ..... %>`` 语法。其中 xxxx 为片段执行器名称。在 ``<%`` 和 ``%>`` 之间编写外部代码片段。

DataQL 查询在遇到定义外部片段时，会将其转转换成为 Udf 形态。因此执行外部代码片段和执行函数调用是相同的。

上面执行 SQL 的例子中，``@@sql`` 表示这个外部片段执行器的名字是 sql。在创建 DataQL 查询环境时需要注册这个执行器：

.. code-block:: java
    :linenos:

    DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
    dataQL.addFragmentProcess("sql", ...); //注册外部代码片段执行器

.. HINT::
    一个外部代码片段执行器需要实现 ``net.hasor.dataql.FragmentProcess`` 接口。更多信息请参考开发手册。
