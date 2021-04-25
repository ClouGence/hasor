# Core 容器框架

&emsp;&emsp;Core 具备 Aop 并兼容 JSR-330 的Bean容器框架。

----------
## 特性
01. 提供一个支持IoC、Aop的Bean容器
02. 基于 Module + ApiBinder 机制提供统一的插件入口
03. 统一的读取接口同时兼容properties、yaml、xml 三种数据配置格式
04. 支持 JSR-330
05. SPI 注册兼容 Java 标准 SPI

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
