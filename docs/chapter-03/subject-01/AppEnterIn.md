### A.加载Module

&emsp;&emsp;当您使用 Hasor 时让框架跑起来只是第一步，您还需要一个启动入口用于配置您项目。在这个启动入口里您可以为您的项目做一些实际的设定。

&emsp;&emsp;在 Hasor 里一切的开始都是 `net.hasor.core.Module` 接口，您需要编写一个类实现这个接口，接口中只有一个方法需要您实现。下面这段代码就是我们实现的第一个 Module：
```java
public class FirstModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
    }
}
```

&emsp;&emsp;接下来我们迫切需要的是让 Hasor 可以加载这个 Module。对于一般情况下而言我们可以在 `Hasor.create` 时进行加载，例如：
```java
Hasor.createAppContext(new FirstModule());
```

&emsp;&emsp;有时候为了减少代码行数我们会把 FirstModule 简写成一个匿名类，例如：
```java
Hasor.createAppContext(new Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
    }
});
```

&emsp;&emsp;如果您打算同时指定 Hasor 配置文件那么会变成如下写法：
```java
Hasor.createAppContext("my-config.xml", new FirstModule());
or
Hasor.createAppContext("my-config.xml", new Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
    }
});
```

&emsp;&emsp;如果我有多个启动入口怎么办？没关系 Hasor 支持多个启动入口，你只需要按照你想要的启动顺序排好，然后在 `Hasor.createAppContext` 启动容器时候传递进去就可以了，Hasor 会自动照顾好它们。

### B.加载多个Module

&emsp;&emsp;前面我们一直说的是如何在启动时候通过参数形式加载启动入口，现在我们就来想象一下如下情景。一个项目，按照业务纬度划分了若干个模块。每个模块都可以通过 Hasor 的 Module 来管理，接下来那么为了方便开发，每个模块负责人会负责开发自己的 Module，最后在统一的地方进行加载。

&emsp;&emsp;刚刚这个例子来说，Hasor 提供了多种方式来加载这些 Module，现在我们就来看看不同的加载方式和它们的优缺点。

&emsp;&emsp;**方式一：** `Hasor.createAppContext` 在初始化 Hasor 容器时指定所有模块。这种方式简单粗暴，无需过多语言进行描述。它的缺点比较明显，那就是随着项目复杂度的提升 Module 可能会越来越多。到时候创建 Hasor 会看起来很复杂。
```java
Hasor.createAppContext("my-config.xml", new UserModule(),new ClassModule() ...);
```

&emsp;&emsp;**方式二：** 为了解决第一种方式中带来的不足，我们可以预先创建一个总入口，然后在总入口中逐个加载所有模块。不光如此总入口我们也可以设置多个，甚至总入口中加载的模块可以是项目中其它模块的入口。这种方式解决了 Module 的规划和加载问题。
```java
public class RootModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.installModule(new UserModule());
        apiBinder.installModule(new ClassModule());
        ...
    }
}
```

&emsp;&emsp;**方式三：** 前面提到的两种方式最大的弊病就是，需要把要加载的模块写死在代码中。倘若我有一个需求是根据实际情况来决定是否要加载，最好的办法就是让我们自己可以在某个地方自己配一下。而这就要用到 Hasor 配置文件中模块管理功能。这种方式最大的好处就是模块的加载可以和代码解耦，在 Hasor 自身内部就大量使用了这种方式。例如配置文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <!-- 默认要装载的模块 -->
    <hasor.modules>
        <module>net.hasor.web.valid.ValidWebPlugin</module>
        <module>net.hasor.web.render.RenderWebPlugin</module>
        <module>net.hasor.plugins.startup.StartupModule</module>
        <module>net.hasor.plugins.aop.AopModule</module>
    </hasor.modules>
</config>
```