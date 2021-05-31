#Hasor-Solon 部署 dataway的 例子

1. 部署 dataway，需要用到 solon.boot 相关的包（solon 需要jdk8+支持）；
2. 本例用了h2数据库，以确保此例随时可运行；
3. solon 的项目，只支持jar运行。。。本例的pom.xml配置，即是生成jar包；通过mvn package可生成。
4. 本例已将编译结果放在 bin/目录下
5. 用此命令可运行：java -jar example-solon_dataway.jar


**关于 solon.boot 的配置**

##### 方案一：undertow 
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.boot.undertow</artifactId>
</dependency>
```


##### 方案二：jetty （此方案会小2m左右；本例用的是此方案）
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.boot.jetty</artifactId>
</dependency>

<!-- solon.boot.jetty 默认只使用了 handler 接口；所以要添加 jetty-servlet 包，以支持 servlet 容器 -->
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-servlet</artifactId>
</dependency>
```

#### 补充：如果要使用完整的 solon 功能，比如：事务，缓存，验证，模板等。可以再引入
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-web</artifactId>
    <type>pom</type>
</dependency>
```

