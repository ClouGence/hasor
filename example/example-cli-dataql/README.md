#Hasor 例子

实现的一个简单 CURD 数据库的命令行工具。

整合
 - Spring Boot（提供 Boot 能力）
 - DataQL Maven 插件（生成 .ql 文件的 Java 调用代码）
 - tConsole（CLI框架）
  
用法：

- java -jar ./target/example-hasor-1.0.jar list
- java -jar ./target/example-hasor-1.0.jar get xxx
- java -jar ./target/example-hasor-1.0.jar set xxx newValue
- java -jar ./target/example-hasor-1.0.jar remove xxx

jdk 要求 1.8+
