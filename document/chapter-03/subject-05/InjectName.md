&emsp;&emsp;现在我们来假定这样一个场景，我们为了加快系统读请求准备引用 Cache 技术。接下来我们的 Cache 都会以 ICache 接口形式呈现。每引入一个 Cache 都会新增一个 ICache 接口对象。例如：
```java
ICache userCache = ...;
ICache dataCache = ...;
```

&emsp;&emsp;根据上一小节面讲过的类型注入，我们很快的发现一个类型只能表示一个Bean实例。这就导致上面虽然我们定义了两个 Cache 对象，但是因为类型相同 Hasor 在进行类型注入时无法区分的局面。

&emsp;&emsp;这时候就要使用本小节提到的名称注入方式。在使用名称注入时首先我们要把两个相同名称的 Bean 在 Hasor 上进行特别的声明：
```java
package net.test.hasor;
public class HelloModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(ICache.class).nameWith("user").to(...);
        apiBinder.bindType(ICache.class).nameWith("data").to(...);
    }
}

public class UseBean {
    @Inject("user")
    private ICache user;
    @Inject("data")
    private ICache data;
}
```

&emsp;&emsp;这样之后 UseBean 的两个 pojo 属性就会正确的注入对应的对象。

---
&emsp;&emsp;`nameWith`方法有一个最大的特点，那就是相同的一个名字可以在不同的类型上重复使用。我们在当前例子上在进一步举例。

&emsp;&emsp;假定 user、data 两个 cache 的数据是来自于两个不同的数据库分别是 DataSource1 和 DataSource2。那么这两个数据源的定义在结合前面 ICache 的定义完整应该是如下这样：
```java
apiBinder.bindType(DataSource.class).nameWith("user").toInstance(...);
apiBinder.bindType(DataSource.class).nameWith("data").toInstance(...);
apiBinder.bindType(ICache.class).nameWith("user").to(...);
apiBinder.bindType(ICache.class).nameWith("data").to(...);
```

&emsp;&emsp;现在我们的 ICache 实现类有两个，它们分别针对两个数据源做cache。那么 Cache 的实现类应该类似这样：
```java
public class UseCache implements ICache {
    @Inject("user")
    private DataSource user;
}

public class DataCache implements ICache {
    @Inject("data")
    private DataSource data;
}
```

