## springboot-dal-on-dataql
- 一个标准的 SpringBoot 工程。
- 使用 DataQL 替代 Mybatis 等框架作为，数据访问组建。

## springboot-dataway-db
- 一个标准的 SpringBoot + Dataway 工程。
- Dataway 上配置的接口信息保存在 H2 数据库上（每次重启会丢失）

## springboot-dataway-nacos
- 一个标准的 SpringBoot + Dataway 工程。
- Nacos 需要预先准备，建议版本：1.4+
- Dataway 上配置的接口信息保存在 Nacos 上。

## springcloud-dataway-nacos
- 一个标准的 SpringCloud + Dataway 工程
- Spring Cloud 服务发现基于 Nacos
- Hasor Dataway 连接了另外两个 MySQL 数据源
- Hasor Dataway 接口配置信息保存在 Nacos
- Dataway 服务在 provider 应用中注册了一个全新的服务名

## springboot-tconsole
- 基于 SpringBoot + tConsole 的 CLI 示例。
