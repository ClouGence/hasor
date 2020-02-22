
--------------------
DataQL Maven 插件
--------------------
DataQL Maven 插件的作用是，根据 DataQL 查询文件生成对应的查询工具类。从而Java应用程序中使用 DataQL 查询能力更加方便快捷。

引入插件

.. code-block:: xml
    :linenos:

    <plugin>
        <groupId>net.hasor</groupId>
        <artifactId>dataql-maven-plugin</artifactId>
        <version>4.1.1</version>
        <executions>
            <execution>
                <goals>
                    <goal>dataql</goal>
                </goals>
            </execution>
        </executions>
    </plugin>


插件会扫描 ``sourceDirectory`` 配置的路径中所有 `.ql` 结尾的查询文件，
并在 ``outputSourceDirectory`` 配置的路径下自动生成对应的 Java 代码，
原始的 Query 查询文件会拷贝到 ``outputResourceDirectory`` 配置的目录下。


各个配置的默认值

+-----------------------------+-------------------------------------------------------+
| **满足条件**                |  **默认值**                                           |
+-----------------------------+-------------------------------------------------------+
| ``outputSourceDirectory``   | ${project.build.directory}/generated-resources/dataql |
+-----------------------------+-------------------------------------------------------+
| ``outputResourceDirectory`` | ${project.build.directory}/generated-sources/dataql   |
+-----------------------------+-------------------------------------------------------+
| ``sourceDirectory``         | ${basedir}/src/main/java                              |
+-----------------------------+-------------------------------------------------------+

插件工作在 GENERATE_SOURCES 阶段，依赖的范围是：COMPILE

下面这个接口是生成的类的接口模板

.. code-block:: xml
    :linenos:

    public class ListOptionQuery extends HintsSet implements Query {
        // 构造方法
        private ListOptionQuery(HintsSet hintsSet) { ... }
        public ListOptionQuery() throws IOException, ParseException { ... }
        public ListOptionQuery(DataQL dataQL) throws IOException, ParseException { ... }
        public ListOptionQuery(Finder finder, Map<String, VarSupplier> shareVarMap) throws IOException, ParseException { ... }
        // 方法
        public QueryResult execute(CustomizeScope customizeScope) throws InstructRuntimeException { ... }
        public ListOptionQuery clone() { ... }
    }

详细配置查看：`Maven 配置页面 <../../../maven-plugin/hasor-dataql/plugin-info.html>`_
