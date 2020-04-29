外部代码片段
------------------------------------

DataQL 允许在查询中混合其它查询语言，一个典型的场景是把 SQL 语句混合在 DataQL 查询中。例如：

.. code-block:: js
    :linenos:

    var dataSet = @@sql(item_code) <%
        select * from category where co_code = #{item_code}
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
