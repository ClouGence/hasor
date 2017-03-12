&emsp;&emsp;根据项目类型的不同 Hasor 的启动方式可能会有不同。我们以一般情况下，您可以通过下面这行代码启动 Hasor 容器。Hasor 是支持 ‘零’ 配置文件的，因此您大可不必为 Hasor 准备任何配置文件。
```java
import net.hasor.core.Hasor;
AppContext appContext = Hasor.createAppContext();
```

&emsp;&emsp;当然您可以在项目中创建一个 “simple-config.xml” 配置文件作为 Hasor 配置文件，然后在创建 Hasor 容器时使用这个配置文件。如下所示：
```java
import net.hasor.core.Hasor;
AppContext appContext = Hasor.createAppContext("simple-config.xml");
```

simple-config.xml 配置文件的格式如下:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    ...
</config>
```