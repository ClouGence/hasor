&emsp;&emsp;Hasor 的配置文件同时支持 `属性` 和 `Xml` 两种格式是支持属性文件，在使用中 Hasor 建议您使用 Xml 形式的配置文件。

&emsp;&emsp;下面我们就来看一看，一个合法的 Hasor 配置文件的样子：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">

</config>
```
&emsp;&emsp;`config` 标签，这是一个根标签。所有 Hasor 的配置都放到这个标签下。其实，根标签的名字对于 Hasor 来说没有任何意义。您甚至可以取别的名字，为了方便可读我们还是统一使用“config”作为根标签。

提示：这样的配置文件也是Hasor所支持的：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root xmlns="http://project.hasor.net/hasor/schema/main">

</root>
```

&emsp;&emsp;`xmlns` 属性。这个属性的值只要不为空，其它都不重要。因此一个合法的 Hasor 配置文件甚至可以是这样的：
```xml
<a xmlns="a">

</a>
```

&emsp;&emsp;现在我们来总结一下 Hasor 配置文件的特点：
- 1.要求有一个根标签
- 2.根标签上要有 `xmlns` 属性，属性不能为空

---
&emsp;&emsp;为了规范化，Hasor 建议您使用下面这个片段作为您的配置文件骨架：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">

</config>
```