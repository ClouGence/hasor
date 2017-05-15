&emsp;&emsp;使用 Freemarker 作为渲染引擎。

&emsp;&emsp; Hasor 框架内置了 Freemarker 渲染引擎插件，您只需要引用 freemarker 的 jar 包，然后配置一下渲染引擎即可。

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.23</version>
</dependency>
```

&emsp;&emsp;配置渲染器。
```
apiBinder.suffix("htm").bind(FreemarkerRender.class);
```