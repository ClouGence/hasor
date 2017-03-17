&emsp;&emsp;前面演示的大量 Hasor 的配置文件内部机制，同时也像我们展示了 Hasor 读取配置文件就是一个简单的表达式（例：`hasor.packages`）

&emsp;&emsp;现在我们设想一下这样的一组配置信息，我想读取所有的 module ：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor>
        ...
        <modules loadModule="${HASOR_LOAD_MODULE}" loadErrorShow="true">
            <module>net.hasor.web.valid.ValidWebPlugin</module>
            <module>net.hasor.web.render.RenderWebPlugin</module>
            <module>net.hasor.plugins.startup.StartupModule</module>
            <module>net.hasor.plugins.aop.AopModule</module>
        </modules>
        ...
    </hasor>
</config>
```

&emsp;&emsp;下面就介绍一下如何读取这种配置，首先读取这样的配置信息主要有两种方法：
- 方法一：通过父节点解析Xml信息，具体如下：
```java
XmlNode modules = env.getSettings().getXmlNode("hasor.modules");
List<XmlNode> allModule = modules.getChildren("module");
for (XmlNode modInfo : allModule){
    modInfo.getText();
}
```

- 方法二：考虑到我们的例子中 `module` 节点并没有定义特殊的属性，因此可以进一步从上面代码简化成如下：
```java
XmlNode[] allModule = env.getSettings().getXmlNodeArray("hasor.modules.module");
for (XmlNode modInfo : allModule){
    modInfo.getText();
}
```

&emsp;&emsp;我们在方法二的基础上还可以在进一步简化成一行代码如下：
```java
String[] allModules = env.getSettings().getStringArray("hasor.modules.module");
```

&emsp;&emsp;Hasor 的配置文件读取十分强大，更多强大的方法，请开发者自行尝试：`Settings` 接口。获取这个接口的方式很多，您可以依赖注入，也可以通过 ApiBinder 接口拿到，也可以通过 AppContxt 接口获取。这里不在详解。

---
#### 使用 Hasor 的配置文件读取方式，单独处理一个指定的 xml 文件
&emsp;&emsp;Hasor 的配置文件解析机制，好的是它可以独立 Hasor 使用。您可以单独使用 `net.hasor.core.setting.InputStreamSettings` 类去加载您的 xml 文件，然后以 Hasor 的方式去处理它。