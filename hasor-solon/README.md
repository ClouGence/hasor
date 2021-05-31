# 整合 Solon

&emsp;&emsp;Hasor-Solon 是一款让 Hasor 和 Solon 互融互通的整合工具。使之支持 Solon Boot 方式。

----------
## 特性
01. 在 Hasor 中使用 Solon。
02. 在 Solon 中使用 Hasor。
03. 支持 Solon Boot 配置。

## 样例


Solon Boot 方式：
```java
// 默认
@EnableHasor

// 如果想在 Solon 中使用 hasor-web 那么使用下面这个注解
@EnableHasorWeb
```
