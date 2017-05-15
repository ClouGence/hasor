&emsp;&emsp;`static-config.xml`是 Hasor 的一个特殊配置文件，它位于 classpath 的根目录上。每个 jar 包都可以拥有一个，它的用途是保存默认配置。

&emsp;&emsp;前面提到的配置文件是 `hasor-config.xml` 它是主配置文件，而 `static-config.xml`文件的格式和主配置文件相同。但是一般情况下我们会把静态配置文件中的 `xmlns` 修改成另外一个，用以隔离各个配置文件的冲突。在下一节我们将会讲解 xml 的命名空间在 Hasor 配置文件中的作用。

&emsp;&emsp;例如下面这个就是 RSF 框架的 `static-config.xml` 配置骨架：
```java
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/hasor-rsf">
    <hasor.rsfConfig ...>
        ...
    </hasor.rsfConfig>
</config>
```
&emsp;&emsp;在看一下 RSF 注册中心的配置骨架：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/hasor-registry">
    <hasor.registry ... >
        ...
    </hasor.registry>
</config>
```

&emsp;&emsp;首先这些项目都是 Hasor 的子项目，因此它们的配置都被要求放到 `hasor` 节点下面，同时每个子项目都要求 `xmlns` 重新自定义一个。

&emsp;&emsp;如果您也在开发 Hasor 插件，或者您的项目在使用 Hasor 时需要用到 `static-config.xml` 配置文件时。建议您也依照该这种方式实践。

&emsp;&emsp;避免冲突：为了避免可能存在的配置冲突，每个项目最好能定义下面两个信息：
- 1.命名空间
- 2.项目配置前缀

&emsp;&emsp;通过这两个点的差异化，是可以保证您的配置可以不和其他人冲突。下面就列出 Hasor 目前已经在使用的命名空间：
- http://project.hasor.net/hasor/schema/main
- http://project.hasor.net/hasor/schema/hasor-core
- http://project.hasor.net/hasor/schema/hasor-rsf
- http://project.hasor.net/hasor/schema/hasor-registry
- http://project.hasor.net/hasor/schema/hasor-land