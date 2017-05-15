&emsp;&emsp;在 Hasor3 之后，由于 ApiBinder 扩展机制的引入，ApiBinder 变可以随时进行扩展。下面我们展现一下相关特性。例：扩展 Apibinder 并在自己的 TestBinder 上实现一个 Hello Word 方法。
```java
package net.test.binder;
public interface TestBinder extends ApiBinder {
    public void hello();
}
public class TestBinderImpl extends ApiBinderWrap implements TestBinder {
    public TestBinderImpl(ApiBinder apiBinder) {
        super(apiBinder);
    }
    public void hello() {
        System.out.println("Hello Binder");
    }
}
public class TestBinderCreater implements ApiBinderCreater {
    public TestBinder createBinder(ApiBinder apiBinder) {
        return new TestBinderImpl(apiBinder);
    }
}
```

&emsp;&emsp;接着我们需要在配置文件中增加如下配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.apiBinderSet>
        <binder type="net.test.binder.TestBinder">net.test.binder.TestBinderCreater</binder>
    </hasor.apiBinderSet>
</config>
```

&emsp;&emsp;最后我们在 Hasor 的启动Module中使用这个自定义的 ApiBinder
```java
package net.test.hasor;
public class HelloModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        TestBinder myBinder = (TestBinder)apiBinder;
        myBinder.hello();
    }
}
```
