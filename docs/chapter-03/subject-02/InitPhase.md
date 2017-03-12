- `findModules` ：查找所有可以加载的 Module。
- `doInitialize` ：执行 init 阶段的起始标志。
- `newApiBinder` ：创建 Module 在执行 loadModule 方法时用到的 ApiBinder 参数。
- `installModule` ：加载每一个 Module。
- `doBind` ：容器级的初始化操作
- `doInitializeCompleted` ：执行 init 阶段的终止标志。
---

&emsp;&emsp;findModules，这个阶段的目的是找出所有配置文件中的 Module 并创建它们，这些 Module 将会在 `installModule` 阶段中加载它们。

&emsp;&emsp;现在我们编写一个 `HelloWord` Module 然后放入配置文件。然后我们启动 Hasor 让 `findModules` 来加载我们的 Module。首先我们创建 Module。
```java
package net.test.hasor;
public class HelloModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        System.out.println("Hello Module");
    }
}
```

&emsp;&emsp;接下来我们准备一个 Hasor 配置文件，并且填入如下内容：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.modules>
        <module>net.test.hasor.HelloModule</module>
    </hasor.modules>
</config>
```

&emsp;&emsp;最后启动 Hasor 就可以看到 `Hello Module` 打印在控制台上。
```java
Hasor.createAppContext("my-config.xml");
```

&emsp;&emsp;Hasor 的很多套件都是通过这种方式进入启动流程，即便是您没有做过丝毫配置。例如：RSF。

&emsp;&emsp;如果您想要一个最纯粹的 Hasor，不希望它在初始化时加载任何插件，您可以通过 `HASOR_LOAD_MODULE` 环境变量禁用 `findModules`。例如：下面这个配置中 `HelloModule` 就不会被加载，同时任何一个内置的 Module 都不会被加载。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor>
        <environmentVar>
            <!-- 是否加载模块 -->
            <HASOR_LOAD_MODULE>false</HASOR_LOAD_MODULE>
        </environmentVar>
        <modules>
            <module>net.test.hasor.HelloModule</module>
        </modules>
    </hasor>
</config>
```

&emsp;&emsp;为了达到上面这个效果您还可以这样设置配置文件。有关配置文件特性的细节请访问配置文件相关章节。 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.modules loadModule="false">
        <module>net.test.hasor.HelloModule</module>
    </hasor.modules>
</config>
```

&emsp;&emsp;`doInitialize`和 `doInitializeCompleted` 两个阶段。除非您是在扩展 `TemplateAppContext` 否则您是无法截获到这两个阶段调用通知的。在 Hasor 内部 doInitializeCompleted 方法会负责通知 `BeanContainer` 完成对标记了 @Init 方法的单例对象，以执行启动创建工作。

&emsp;&emsp;`newApiBinder` 本质上是创建 `ApiBinder` 接口对象的过程。在 Hasor3 之后，由于 ApiBinder 扩展机制的引入，newApiBinder 变得与 findModules 同样重要。有关 ApiBinder 的内容在后面的 ApiBinder 章节会深入讲解。

&emsp;&emsp;`installModule` 阶段是循环所有已知 Module 并执行它们的 `loadModule` 方法去初始化它们。