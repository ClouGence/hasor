# Dataway 数据接口配置服务

&emsp;&emsp;依托 DataQL 服务聚合能力，为应用提供一个 UI 界面。并以 jar 包的方式集成到应用中。
通过 Dataway 可以直接在界面上配置和发布接口。

&emsp;&emsp;这种模式的革新使得开发一个接口不必在编写任何形式的代码，只需要配置一条 DataQL 查询即可完成满足前端对接口的需求。
从而避免了从数据库到前端之间一系列的开发配置任务，例如：Mapper、DO、DAO、Service、Controller 统统不在需要。

&emsp;&emsp;Dataway特意采用了 jar包集成的方式发布，这使得任意的老项目都可以无侵入的集成 Dataway。
直接改进老项目的迭代效率，大大减少企业项目研发成本。

![avatar](https://www.hasor.net/web/_images/CC2_A633_6D5C_MK4L.png)

&emsp;&emsp;如上图所示 Dataway 在开发模式上提供了巨大的便捷。
虽然工作流程中标识了由后端开发来配置 DataQL 接口，但这主要是出于考虑接口责任主体。
但在实际工作中根据实际情况需要，配置接口的人员可以是产品研发生命周期中任意一名角色。

----------
## 主打场景

01. 取数据
    - 如果你只想从数据库或者服务中获取某类数据，不需要： VO、BO、Convert、DO、Mapper 这类东西。
02. 存数据
    - 如果是从页面表单递交数据到数据库或者服务，免去 BO、FormBean、DO、Mapper 这类东西。

----------
## 技术架构

![avatar](https://www.hasor.net/web/_images/CC2_B633_6D5C_MK4L.png)


&emsp;&emsp;ORM 类框架有一个最大的特点是具有 Mapping 过程，然后通过框架在进行 CURD 操作。
例如：Mybatis、Hibernate。其中有一些甚至做到了更高级的界面化例如： apijson，但其本质依然是 ORM。

&emsp;&emsp;这与 DataQL 有很大不同。虽然 DataQL 提供了非常出色的基于 SQL 数据存取能力。但从技术架构上来审视，可以看出它并不是 ORM 框架。
它没有 ORM 中最关键的 Mapping 过程。DataQL 专注的是：结果转换、数据和服务的聚合查询。

&emsp;&emsp;DataQL 的数据库的存取能力也只是充分利用 Udf 和 Fragment 奇妙的组合而已。

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
