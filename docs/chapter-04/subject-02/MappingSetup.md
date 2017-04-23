&emsp;&emsp;MappingSetup 接口的作用是用来发现所有 Hasor Web 框架中注册的 请求控制器。具体用法如下：
```java
public class DemoPlugin implements MappingSetup {
    @Override
    public void setup(MappingData mappingData) {
        ...
    }
}
```

&emsp;&emsp;编写好 MappingSetup 之后您还需要通过 WebModule 注册到框架中。
```java
public class DemoModule extends WebModule{
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.addSetup(DemoPlugin.class);
    }
}
```