# DataQL 数据查询引擎

&emsp;&emsp;DataQL（Data Query Language）DataQL 是一种查询语言。旨在通过提供直观、灵活的语法来描述客户端应用程序的数据需求和交互。

&emsp;&emsp;数据的存储根据其业务形式通常是较为简单的，并不适合直接在页面上进行展示。因此开发页面的前端工程师需要为此做大量的工作，这就是 DataQL 极力解决的问题。

&emsp;&emsp;例如：下面这个 DataQL 从 user 函数中查询 id 为 4 的用户相关信息并返回给应用。

```js
return userByID({'id': 4}) => {
    'name',
    'sex' : (sex == 'F') ? '男' : '女' ,
    'age' : age + '岁'
}
```

返回结果：
```json
{
  'name' : '马三',
  'sex'  : '男',
  'age'  : '25岁'
}
```

----------
## 特性
01. **层次结构**：多数产品都涉及数据的层次结构，为了保证结构的一致性 DataQL 结果也是分层的。
02. **数据为中心**：前端工程是一个比较典型的场景，但是 DataQL 不局限于此（后端友好性）。
03. **弱类型定义**：语言中不会要求声明任何形式的类型结构。
04. **简单逻辑**：具备简单逻辑处理能力：表达式计算、对象取值、条件分支、lambda和函数。
05. **编译运行**：查询的执行是基于编译结果的。
06. **混合语言**：允许查询中混合任意的其它语言代码，典型的场景是查询中混合 SQL 查询语句。
07. **类 JS 语法**：类JS语法设计，学习成本极低。

## 样例

```java
public class UserByIdUdf implements Udf {
    public UserInfo call(Hints readOnly, Object[] params) {
        ...
    }
}

public class ConsoleDemo {
    public static void main(String[] args) {
        AppContext appContext = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.addShareVarInstance("userByID", new UserByIdUdf());

        });
        DataQL dataQL = appContext.getInstance(DataQL.class);
        QueryResult queryResult = dataQL.createQuery("return userByID({'id': 4}) => {" +
                                                     "    'name'," +
                                                     "    'sex' : (sex == 'F') ? '男' : '女' ," +
                                                     "    'age' : age + '岁'" +
                                                     "}").execute();
        DataModel dataModel = queryResult.getData();
    }
}
```

----------------
# 子项目：Dataway 数据接口配置服务

&emsp;&emsp;依托 DataQL 服务聚合能力，为应用提供一个 UI 界面。并以 jar 包的方式集成到应用中。
通过 Dataway 可以直接在界面上配置和发布接口。

&emsp;&emsp;这种模式的革新使得开发一个接口不必在编写任何形式的代码，只需要配置一条 DataQL 查询即可完成满足前端对接口的需求。
从而避免了从数据库到前端之间一系列的开发配置任务，例如：Mapper、DO、DAO、Service、Controller 统统不在需要。

&emsp;&emsp;Dataway特意采用了 jar包集成的方式发布，这使得任意的老项目都可以无侵入的集成 Dataway。
直接改进老项目的迭代效率，大大减少企业项目研发成本。

![avatar](https://www.hasor.net/web/_images/CC2_A633_6D5C_MK4L.png)

&emsp;&emsp;如上图所示 Dataway 在开发模式上提供了巨大的便捷。
虽然工作流程中标识了由后端开发来配置 DataQL 接口，但这主要是出于考虑接口责任人。
但在实际工作中根据实际情况需要，配置接口的人员可以是产品研发生命周期中任意一名角色。

----------
## 主打场景

&emsp;&emsp;主打场景并不是说 Dataway 适用范围仅限于此，而是经过多次项目实践。我们认为下面这些场景会有非常好的预期效果。
比如说 ``取数据`` 在一些报表、看板项目中即便是取数据逻辑在复杂。我们依然做到了真正的 零 开发，所有取数逻辑全部通过 DataQL + SQL 的方式满足。
对比往期项目对于后端技术人员的需求从 3～5 人的苦逼通宵加班，直接缩减为 1人配置化搞定。

&emsp;&emsp;再比如，某个内部类 ERP 项目，20多个表单页面，后端部分仅有 1000 行左右的核心代码。其它数据存取逻辑全部配置化完成。


01. 取数据
    - 如果你只想从数据库或者服务中获取某类数据，不需要： VO、BO、Convert、DO、Mapper 这类东西。
02. 存数据
    - 如果是从页面表单递交数据到数据库或者服务，免去 BO、FormBean、DO、Mapper 这类东西。
03. 数据聚合
    - 基于服务调用结果经过结构转换并响应给前端。
    - 将数据库和服务等多个结果进行汇聚然后返回给前端。

----------
## 技术架构

![avatar](https://www.hasor.net/web/_images/CC2_B633_6D5C_MK4L.png)

&emsp;&emsp;刚一接触 DataQL 可能会有一种错觉认为 DataQL 是一个高级别的 ORM 工具。
这一点需要澄清。DataQL 的竞品应是 GraphQL，而非 ORM 框架。

&emsp;&emsp;ORM 类框架有一个最大的特点是具有 Mapping 过程，然后通过框架在进行 CURD 操作。
例如：Mybatis、Hibernate。其中有一些甚至做到了更高级的界面化例如： apijson，但其本质依然是 ORM。

&emsp;&emsp;而 DataQL 有很大不同。虽然 DataQL 提供了非常出色的基于 SQL 数据存取能力。但从技术架构上来审视，可以看出它并不是 ORM 框架。
它没有 ORM 中最关键的 Mapping 过程。DataQL 专注的是：结果转换、数据和服务的聚合查询。

&emsp;&emsp;造成 ORM 错觉的是由于 DataQL 充分利用 Udf 和 Fragment 奇妙的组合，提供了更便捷的数据库存储逻辑配置化而已。

----------
## Spring 中使用 Dataway

&emsp;&emsp;Dataway 是 Hasor 生态中的一员，使用 Dataway 第一步需要通过 [hasor-spring](https://www.hasor.net/web/spring/index.html) 打通两个生态。

```xml
<!-- 引入依赖 -->
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-spring</artifactId>
    <version>4.1.3</version>
</dependency>
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-dataway</artifactId>
    <version>4.1.3</version>
</dependency>
```

```java
@EnableHasor()      // 在Spring 中启用 Hasor
@EnableHasorWeb()   // 将 hasor-web 配置到 Spring 环境中，Dataway 的 UI 是通过 hasor-web 提供服务。
```

&emsp;&emsp;然后第二步，在应用的 `application.properties` 配置文件中启用 Dataway

```properties
# 启用 Dataway 功能（默认不启用）
HASOR_DATAQL_DATAWAY=true
# 开启 ui 管理功能（注意生产环境必须要设置为 false，否则会造成严重的生产安全事故）
HASOR_DATAQL_DATAWAY_ADMIN=true

# （可选）API工作路径
HASOR_DATAQL_DATAWAY_API_URL=/api/
# （可选）ui 的工作路径，只有开启 ui 管理功能后才有效
HASOR_DATAQL_DATAWAY_UI_URL=/interface-ui/
```

&emsp;&emsp;第三步，初始化 dataway 的必要数据库表。

```sql
CREATE TABLE `interface_info` (
    `api_id`          int(11)      NOT NULL AUTO_INCREMENT   COMMENT 'ID',
    `api_method`      varchar(12)  NOT NULL                  COMMENT 'HttpMethod：GET、PUT、POST',
    `api_path`        varchar(512) NOT NULL                  COMMENT '拦截路径',
    `api_status`      int(2)       NOT NULL                  COMMENT '状态：0草稿，1发布，2有变更，3禁用',
    `api_comment`     varchar(255)     NULL                  COMMENT '注释',
    `api_type`        varchar(24)  NOT NULL                  COMMENT '脚本类型：SQL、DataQL',
    `api_script`      mediumtext   NOT NULL                  COMMENT '查询脚本：xxxxxxx',
    `api_schema`      mediumtext       NULL                  COMMENT '接口的请求/响应数据结构',
    `api_sample`      mediumtext       NULL                  COMMENT '请求/响应/请求头样本数据',
    `api_create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `api_gmt_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='Dataway 中的API';

CREATE TABLE `interface_release` (
    `pub_id`          int(11)      NOT NULL AUTO_INCREMENT   COMMENT 'Publish ID',
    `pub_api_id`      int(11)      NOT NULL                  COMMENT '所属API ID',
    `pub_method`      varchar(12)  NOT NULL                  COMMENT 'HttpMethod：GET、PUT、POST',
    `pub_path`        varchar(512) NOT NULL                  COMMENT '拦截路径',
    `pub_status`      int(2)       NOT NULL                  COMMENT '状态：0有效，1无效（可能被下线）',
    `pub_type`        varchar(24)  NOT NULL                  COMMENT '脚本类型：SQL、DataQL',
    `pub_script`      mediumtext   NOT NULL                  COMMENT '查询脚本：xxxxxxx',
    `pub_script_ori`  mediumtext   NOT NULL                  COMMENT '原始查询脚本，仅当类型为SQL时不同',
    `pub_schema`      mediumtext       NULL                  COMMENT '接口的请求/响应数据结构',
    `pub_sample`      mediumtext       NULL                  COMMENT '请求/响应/请求头样本数据',
    `pub_release_time`datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间（下线不更新）',
    PRIMARY KEY (`pub_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='Dataway API 发布历史。';

create index idx_interface_release on interface_release (pub_api_id);
```

&emsp;&emsp;最后一步，将 Spring 使用的数据源导入到 Hasor 环境共 Dataway 使用。

```java
@DimModule
@Component
public class ExampleModule implements SpringModule {
    @Autowired
    private DataSource dataSource = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .DataSource form Spring boot into Hasor
        apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        // .custom DataQL
        //apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
        //apiBinder.tryCast(QueryApiBinder.class).bindFragment("sql", SqlFragment.class);
    }
}
```

----------
## 使用UI配置接口

&emsp;&emsp;启动工程，访问 http://127.0.0.1:8080/interface-ui/ 就可以看到下面页面了。

![avatar](https://www.hasor.net/web/_images/CC2_C633_6D5C_MK4L.png)

![avatar](https://www.hasor.net/web/_images/CC2_D633_6D5C_MK4L.png)

发布接口需要先进行冒烟测试，冒烟通过之后就可以点亮发布按钮。接口只有在发布之后才能在 Api List 页面中调用，前端才可以正常访问。


## FAQ：

&emsp;&emsp;拿到源码直接倒入工程后发现有一些类缺失是什么问题？ 答：请先执行一下 "mvn compile"。原因是：dataway的 dao 层采用的是 DataQL 方案。