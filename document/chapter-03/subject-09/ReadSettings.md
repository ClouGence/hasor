&emsp;&emsp;前面章节，我们已经介绍了 Hasor 的配置文件结构。下面我们介绍一个 Hasor 的配置文件的基本规则。

### 随意性
&emsp;&emsp;Hasor 配置文件在编写时完全遵循 Xml 标准。但是因为没有 xml 内容上的固定限制，因此 Hasor 配置文件本质上是没有一个适合的 Sechma 可以去加以验证的。

&emsp;&emsp;这是 Hasor 配置文件灵活性的一个表现，也是区别于 Spring 等其它基于 Xml 做配置文件的框架的特点。

### 读取规则
&emsp;&emsp;如果您不遵循这个规则那么您在读取配置文件时就会陷入麻烦，因此建议您在使用 Hasor 配置文件之前一定要阅读本段。

&emsp;&emsp;Hasor 在读取配置文件时您并不是通过 Xml Dom 或者 XPath 表达式进行筛选，Hasor 为您提供了一套更为简单方便而且对属性文件也十分亲和的读取方式。我们以下面这个配置文件为例：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <!-- Demo 项目源码所处包 -->
    <hasor debug="false">
        <loadPackages>net.test.project.*</loadPackages>
    </hasor>
    <hasor-jdbc>
        <!-- 名称为 localDB 的内存数据库，数据库引擎使用 HSQL -->
        <dataSource name="localDB" dsFactory="net.test.C3p0Factory">
            <driver>org.hsqldb.jdbcDriver</driver>
            <url>jdbc:hsqldb:mem:aname</url>
        </dataSource>
    </hasor-jdbc>
</config>
```

- 如果想读取：`debug="false"` 对应的表达式为：`hasor.debug`
- 如果想读取：`net.test.project.*` 对应的表达式为：`hasor.loadPackages`
- 如果想读取：`org.hsqldb.jdbcDriver` 对应的表达式为：`hasor-jdbc.dataSource.driver`

&emsp;&emsp;这种读取规则我们可能感觉有点熟悉。它有点像Xpath，但又不同。下面我们就来正式介绍这种规则。
- 1.除根节点之外，任何元素、属性都被成为节点。
- 2.要想读取到某个节点，必须通过节点表达式。
- 3.一个节点的表达式是，其最根的节点到它之间所有节点的路径。路径使用`.`分割。

&emsp;&emsp;我们在回头去看上面三个读取的例子：
- `debug="false"` 要读取到 false，它所处的节点是一个属性节点 `debug` 这个属性节点位于 `hasor` 节点之下，`hasor` 节点在往上就是跟节点了，规则上说过 `根节点除外`。因此最根节点就是 `hasor`。因此它的路径是：`hasor -> debug`，接着路径转化为表达式：`hasor.debug`。
- `net.test.project.*` 要读取到这个值，它所处的节点是一个元素节点 `loadPackages` 这个元素节点位于 `hasor` 节点之下。因此它的路径是：`hasor -> loadPackages`，接着路径转化为表达式：`hasor.loadPackages`。
- `org.hsqldb.jdbcDriver` 要读取到这个值，它所处的节点是一个元素节点 `driver` 这个属性节点位于 `dataSource` 节点之下，在往上是`hasor-jdbc`元素节点。因此它的路径是：`hasor-jdbc -> dataSource -> driver`，接着路径转化为表达式：`hasor-jdbc.dataSource.driver`。

&emsp;&emsp;最后在得到表达式之后，我们通过 `Settings` 接口就可以读取配置信息了：
```java
AppContext appContext = Hasor.createAppContext("simple-config.xml");
Settings settings = appContext.getInstance(Settings.class);
String driver = settings.getString("hasor-jdbc.dataSource.driver");
```

&emsp;&emsp;以上就是 Hasor 配置文件的读取规则。下面我们在讨论一下特殊情况，这种情况我们要尽量回避，以避免给自己带来麻烦。举例Xml，我们还是读取`debug`配置信息：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor debug="false">
        <debug>true</debug>
    </hasor>
</config>
```

&emsp;&emsp;上面的配置文件中我们发现 `hasor` 节点下面有两个节点，一个是元素节点叫 `debug`，一个是属性节点也叫 `debug`。根据我们上面的路径规则，这两个节点的最终表达式均为：`hasor.debug`，这种情况我们称之为配置冲突。

&emsp;&emsp;配置冲突要尽量避免。如果不小心遇到了冲突，Hasor 在加载配置文件时进行数据合并。相信无论合并之后选用哪个值，对您来说都是不想看到的。不过有时候冲突也会被我们所利用。

&emsp;&emsp;还是这个例子如果配置文件的内容无法改变，但是我还想读取 `debug` 办法还是有的。既然直接读取 debug 节点读取不到，那我们可以读它的父节点。通过父亲节点以Xml的形式去解决它。具体解决办法如下：
```java
Settings settings = ...;
XmlNode node = settings.getXmlNode("hasor");
String attrDebug = node.getAttribute("debug");
String attrDebug = node.getOneChildren("debug").getText();
```
