# Core 容器框架

&emsp;&emsp;Core 具备 Aop 并兼容 JSR-330 的Bean容器框架。

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
