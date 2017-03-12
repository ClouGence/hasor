&emsp;&emsp;Hasor 支持单例，声明 Bean 的单例一般通过下面这种注解方式即可。
```java
@Singleton()
public class AopBean {
    ...
}
```

&emsp;&emsp;如果您使用的 Apibinder 方式进行代码形式声明单例，那么需要这样：
```java
public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) {
        apiBinder.bindType(PojoInfo.class).asEagerSingleton();
    }
}
```

&emsp;&emsp;如果说您的项目中要大量应用到单例模式，在每个类上都标记 `@Singleton` 注解也是一件不小的工作量。Hasor 允许让你增加一个配置，通过配置让 Hasor 框架默认讲所有类在创建时都进行单例化配置。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.default>
        <!-- 改为 true，让 Hasor 框架默认工作在单例模式下 -->
        <asEagerSingleton>true</asEagerSingleton>
    </hasor.default>
</config>
```