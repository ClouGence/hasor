关于
------------------------------------
SQL 执行器是 DataQL 的一个 FragmentProcess 扩展，其作用是让 DataQL 可以执行 SQL。
执行器的实现是 FunctionX 扩展包提供的。使用执行器只需要引入扩展包。

.. code-block:: xml
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-dataql-fx</artifactId>
        <version>4.1.7</version>
    </dependency>

功能与特性
  - 支持两种模式：**简单模式**、**分页模式**
  - 简单模式下，使用原生SQL。100% 兼容所有数据库。
  - 分页模式下，自动改写分页SQL。并兼容多种数据库。
  - 支持参数化 SQL，更安全
  - 支持 SQL 注入，更灵活
  - 支持批量 CURD

配置数据源
------------------------------------
由于 SQL 执行器属于 DataQL 扩展的一部分，而 DataQL 是属于 Hasor 生态中的一员。因此只要在 Hasor 中初始化数据源就可以让其工作起来。

下例是初始化 Hasor 数据源的模块代码，如果你是基于 Spring 生态。那么请参考 ``集成到 Spring`` 相关内容以配置模块。

.. HINT::
    如果 Hasor 环境中已经初始化了数据源那么无需二次初始化。

.. code-block:: java
    :linenos:

    public class ExampleModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            // .创建数据源
            DataSource dataSource = null;
            // .初始化Hasor Jdbc 模块，并配置数据源
            apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        }
    }

SQL方言
------------------------------------
方言是可选项，但如果使用分页查询那么就会用到方言。

- 配置方言环境变量，例如： ``HASOR_DATAQL_FX_PAGE_DIALECT`` 等于 ``Mysql`` (不区分大小写)

.. HINT::
    环境变量的配置请参考章节： ``容器框架(IoC/Aop)`` -> ``环境变量``
    如果是 Spring 整合那么参考章节： ``整合及工具`` -> ``集成到 Spring``

SQL分页在方言的SQL语句改写上，参考了成熟的开源框架 ``MyBatis 分页插件 PageHelper`` 因此可以兼容下列数据库。

+---------------+----------------------------------------+
| **方言版本**  | **兼容的数据库**                       |
+---------------+----------------------------------------+
| Mysql         | Mysql、Mariadb、SQLite、HerdDB         |
+---------------+----------------------------------------+
| Oracle        | Oracle                                 |
+---------------+----------------------------------------+
| SqlServer2012 | SqlServer2012、Derby                   |
+---------------+----------------------------------------+
| PostgreSQL    | H2、HsqlDB、HsqlDB、Phoenix            |
+---------------+----------------------------------------+
| DB2           | DB2                                    |
+---------------+----------------------------------------+
| Informix      | Informix                               |
+---------------+----------------------------------------+
