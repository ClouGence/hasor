&emsp;&emsp;前面讲解了有关 Module 的一些特点，在这一小节我们将专门讲解一下作为我们开发人员都会使用到哪些 Module。为了让大家非常直观的有一个认识。先向大家展示一张 Module 家族的类图：

![Module集](http://files.hasor.net/uploader/20170310/114317/CC2_C40A_FE51_98E3.jpg "Module集")

&emsp;&emsp;这是早期的 Hasor 模块，用的频率比较高的有：Module、WebModule、RsfModule。而例如：DBModule、MyBatisModule、SpringModule 这些则是一些插件。下面我们看看下面这三兄弟怎么使用。

- Module，标准模块
```java
public class DemoModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ...
    }
}
```

- WebModule，Web模块
```java
public class MyWebModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
    }
}
```

- RsfModule，分布式RPC模块
```java
public class MyRPCModule extends RsfModule {
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        ...
    }
}
```
