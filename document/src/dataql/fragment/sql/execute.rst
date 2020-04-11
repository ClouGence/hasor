执行 SQL
------------------------------------
在 DataQL 中执行一条SQL，然后将 SQL 的结果集进行处理非常简单。

.. code-block:: js
    :linenos:

    // 声明一个 SQL
    var dataSet = @@sql() <%
        select * from category limit 10;
    %>
    // 执行这个 SQL，并返回结果
    return dataSet();

SQL 参数化
------------------------------------
为了防止 SQL 注入，通常都使用带参的SQL。

.. code-block:: js
    :linenos:

    // 声明一个 SQL
    var dataSet = @@sql(itemCode) <%
        select * from category where co_code = #{itemCode} limit 10;
    %>
    // 执行这个 SQL，并返回结果
    return dataSet(${itemCode});

SQL 注入
------------------------------------
SQL 注入能力是为了一些特殊场景需要拼接 SQL 而准备的，例如：动态排序字段和排序规则。
其中参数: ``orderField`` 是排序字段，``orderType`` 是排序类型

.. code-block:: js
    :linenos:

    // 使用 DataQL 拼接字符串
    var orderBy = ${orderField} + " " + ${orderType};

    // 声明一个可以注入的 SQL
    var dataSet = @@sql(itemCode,orderString) <%
        select * from category where co_code = #{itemCode} order by ${orderString} limit 10;
    %>
    // 执行这个 SQL，并返回结果
    return dataSet(${itemCode}, orderBy);

Ognl 表达式
------------------------------------
和 Mybatis 一样，SQL 执行器可以将一个对象作为参数传入。通过 SQL 模版中的 ognl 表达式来获取对应的值，例如：一个对象插入SQL

.. code-block:: js
    :linenos:

    // 例子数据
    var testData = {
        "name"   : "马三",
        "age"    : 26,
        "status" : 0
    }
    // insert语句模版
    var insertSQL = @@sql(userInfo) <%
        insert into user_info (
            name,
            age,
            status,
            create_time
        ) values (
            #{userInfo.name},
            #{userInfo.age},
            #{userInfo.status},
            now()
        )
    %>
    // 插入数据
    return insertSQL(testData);

批量操作
------------------------------------
DataQL 的 SQL 执行器支持批量 ``Insert\Update\Delete\Select`` 操作，最常见的场景是批量插入数据。批量操作必须满足下列几点要求：

- 入参必须是 List
- 如果有多个入参。所有参数都必须是 List 并且长度必须一致。
- ``@@sql()<% ... %>`` 写法升级为批量写法 ``@@sql[]()<% ... %>``
- 如果批量操作的 SQL 中存在 SQL注入，那么批量操作会自动退化为 **循环遍历模式**

还是上面插入数据的例子，采用批量模式之后 SQL 部分不变，只是把 ``@@sql`` 改为 ``@@sql[]``。入参数转换为数组即可。

.. code-block:: js
    :linenos:

    // 例子数据
    var testData = [
        { "name" : "马一", "age" : 26, "status" : 0 },
        { "name" : "马二", "age" : 26, "status" : 0 },
        { "name" : "马三", "age" : 26, "status" : 0 }
    ]
    // insert语句模版
    var insertSQL = @@sql[](userInfo) <%
        insert into user_info (
            name,
            age,
            status,
            create_time
        ) values (
            #{userInfo.name},
            #{userInfo.age},
            #{userInfo.status},
            now()
        )
    %>
    // 批量操作
    return insertSQL(testData);

.. HINT::
    由于批量操作底层执行 SQL 使用的是 java.sql.Statement.executeBatch 方法，因此执行 insertSQL 的返回值是一组 int 数组。

执行结果拆包
------------------------------------
拆包是指，例如执行 ``select count(*) from ...`` 这种语句时 SQL 执行器自动将返回的一行一列数据拆解为 ``int`` 类型值。

拆包分为三个模式，默认为 ``column``

- ``off``：不拆包。SQL 执行的返回结果严格按照一个对象数组的模式返回。
- ``row``：最小粒度到行。当返回多条记录的时，行为和 off 一致。当返回 0 或 1 条记录时，自动解开最外层的 List，返回一个 Object。
- ``column``：最小粒度到列。当返回结果只有一行一列数据时，只返回具体值。例如： ``select count(*)`` 返回 int 类型

拆包模式可以通过 hint 改变，例如： ``hint FRAGMENT_SQL_OPEN_PACKAGE = 'row'``

.. code-block:: js
    :linenos:

    var dataSet = @@sql() <% select count(*) as cnt from category; %>
    var result =  dataSet();
    // 不指定 hint 的情况下，会返回 category 表的总记录数，例如 10条。

.. code-block:: js
    :linenos:

    hint FRAGMENT_SQL_OPEN_PACKAGE = "row" // 拆包模式设置为：行
    var dataSet = @@sql() <% select count(*) as cnt from category; %>
    var result =  dataSet();
    // 拆包模式变更为 row ，返回值为： { "cnt" : 10 }

.. code-block:: js
    :linenos:

    hint FRAGMENT_SQL_OPEN_PACKAGE = "off" // 拆包模式设置为：关闭
    var dataSet = @@sql() <% select count(*) as cnt from category; %>
    var result =  dataSet();
    // 关闭拆包，返回值为标准的 List/Map： [ { "cnt" : 10 } ]

分页查询
------------------------------------
分页查询默认是关闭的，需要通过 ``hint FRAGMENT_SQL_QUERY_BY_PAGE = true`` 将其打开。

打开分页查询之后执行 SQL 操作需要经过3个步骤。

- 创建分页查询对象
- 设置分页信息
- 执行分页查询

.. code-block:: js
    :linenos:

    // SQL 执行器切换为分页模式
    hint FRAGMENT_SQL_QUERY_BY_PAGE = true"
    // 定义查询SQL
    var dataSet = @@sql() <%
        select * from category
    %>
    // 创建分页查询对象
    var pageQuery =  dataSet();
    // 设置分页信息
    run pageQuery.setPageInfo({
        "pageSize"    : 10, // 页大小
        "currentPage" : 3   // 第3页
    });
    // 执行分页查询
    var result = pageQuery.data();

分页信息
------------------------------------
分页查询场景中会有一个更加明细的分页数据，DataQL 分页对象通过 pageInfo 方法即可拿到这个信息。

.. code-block:: js
    :linenos:

    // SQL 执行器切换为分页模式
    hint FRAGMENT_SQL_QUERY_BY_PAGE = true"
    // 定义查询SQL
    var dataSet = @@sql() <%
        select * from category
    %>
    // 创建分页查询对象
    var pageQuery =  dataSet();
    // 设置分页信息
    run pageQuery.setPageInfo({
        "pageSize"    : 10, // 页大小
        "currentPage" : 3   // 第3页
    });
    // 获得分页信息
    var result = pageQuery.pageInfo();
    // result = {
    //    "enable"         : true,  // 启用了分页，当 pageSize > 0 时为 true
    //    "pageSize"       : 4,     // 每页大小
    //    "totalCount"     : 17,    // 记录总数
    //    "totalPage"      : 5,     // 总页数
    //    "currentPage"    : 3,     // 当前页数
    //    "recordPosition" : 12     // 当前页第一条记录所处的记录第几行。
    // }

.. HINT::
    获取分页信息时需要获取总记录数，因此会产生一条 count 的查询。

数据库事务
------------------------------------
SQL 执行器本身并不支持数据库事务，事务的能力需要借助 事务函数来实现。

.. code-block:: js
    :linenos:

    import 'net.hasor.dataql.fx.db.TransactionUdfSource' as tran; //引入事务函数
    ...
    return tran.required(() -> {
        ... // 事务
        return ...
    });
    ...

DataQL 的事务函数还可以嵌套使用。

.. code-block:: js
    :linenos:

    ...
    return tran.required(() -> {
        ... // 事务
        var dat = tran.required(() -> {
            ... // 嵌套事务
            return ...
        });
        ...
        return dat
    });
    ...

支持完整的 7个传播属性。

+---------------+--------------------+-------------------------------------+
| **类型**      | **说明**           | **用法**                            |
+---------------+--------------------+-------------------------------------+
| REQUIRED      | 加入已有事务       |  tran.required(() -> { ... });      |
+---------------+--------------------+-------------------------------------+
| REQUIRES_NEW  | 独立事务           | tran.requiresNew(() -> { ... });    |
+---------------+--------------------+-------------------------------------+
| NESTED        | 嵌套事务           | tran.nested(() -> { ... });         |
+---------------+--------------------+-------------------------------------+
| SUPPORTS      | 跟随环境           | tran.supports(() -> { ... });       |
+---------------+--------------------+-------------------------------------+
| NOT_SUPPORTED | 非事务方式         | tran.notSupported(() -> { ... });   |
+---------------+--------------------+-------------------------------------+
| NEVER         | 排除事务           | tran.never(() -> { ... });          |
+---------------+--------------------+-------------------------------------+
| MANDATORY     | 要求环境中存在事务 | tran.tranMandatory(() -> { ... });  |
+---------------+--------------------+-------------------------------------+
