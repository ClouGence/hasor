# 整合 Spring

&emsp;&emsp;Hasor-Spring 是一款让 Hasor 和 Spring 互融互通的整合工具。它即支持Spring Xml 方式配置，也支持 Spring Boot 方式。

----------
## 特性
01. 在 Hasor 中使用 Spring。
02. 在 Spring 中使用 Hasor。
03. 支持 Spring Xml 方式配置。
04. 支持 Spring Boot 配置。

## 样例

Spring Xml 方式：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:h="http://www.hasor.net/schema/spring-hasor"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
       http://www.hasor.net/schema/spring-hasor spring-hasor-4.2.2.xsd">

    <h:hasor useProperties="true">
        <!-- （可选）指定Hasor 的配置文件 -->
        <h:mainConfig>classpath:net_hasor_spring/example-hconfig.xml</h:mainConfig>
        <!-- （可选）设置环境变量 -->
        <h:property name="msg_1" value="msg_1"/>
        <h:property name="msg_2" value="msg_2"/>
        <!-- （可选）设定对 @DimModule 的扫描，自动加载 Module -->
        <h:loadModule autoScan="true" scanPackages="net.hasor.test.spring.mod2.*"/>
    </h:hasor>

    <!-- 声明一个 Spring Bean，名称为：helloString。该 Bean 是通过 AppContext.getInstance('helloWord') 获取。 -->
    <h:bean id="hasorBean1" refID="helloWord"/>

    <!-- 声明一个 Spring Bean，名称为：hasorBean。该 Bean 是通过 AppContext.getInstance(HasorBean.class) 获取。 -->
    <h:bean id="hasorBean2" refType="net.hasor.test.spring.HasorBean"/>

    <!-- 常规的 Spring Bean，引用了 Hasor 的Bean -->
    <bean id="springBean" class="net.hasor.test.spring.SpringBean">
        <property name="hasorBean" ref="hasorBean2"/>
    </bean>
</beans>
```

Spring Boot 方式：
```java
// 默认
@EnableHasor

// 如果想在 Spring 中使用 hasor-web 那么使用下面这个注解
@EnableHasorWeb
```
