# Dataway 数据接口服务工具

&emsp;&emsp;依托 DataQL 服务聚合能力 提供一个 jar 包的方式集成到应用中。并提供一个 UI 界面让开发可以直接通过 UI 配置数据接口。集成到应用里面，提供一个 ui 页面直接配置 服务接口。免去一些常见场景的开发任务。
为应用提供一个UI页面，可以直接将 DataQL/SQL 查询配置成一个接口
----------
## 特性
01. 提供一个支持IoC、Aop的Bean容器
03. 基于 Module + ApiBinder 机制提供统一的插件入口
04. 特色的 Xml 解析器。让你无需二次开发无需配置，直接读取自定义xml配置文件
04. 支持 JSR-330

## 样例

```java
public class ConsoleDemo {
    public static void main(String[] args) {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            ...
        });
    }
}
```
