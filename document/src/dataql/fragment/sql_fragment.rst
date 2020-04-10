关于执行器
------------------------------------
SQL 执行器是 DataQL 的一个 FragmentProcess 扩展，其作用是让 DataQL 可以执行 SQL。
执行器的实现是 FunctionX 扩展包提供的。使用执行器需要引入扩展包。

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-dataql-fx</artifactId>
        <version>4.1.3</version>
    </dependency>

功能和特性
------------------------------------

SQL 执行器有两种工作模式：1-常规模式、2-分页模式