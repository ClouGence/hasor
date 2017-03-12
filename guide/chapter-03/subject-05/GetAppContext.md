&emsp;&emsp;用过 Spring 的同学都知道要想注入 Spring 容器本身您必须要实现一个接口。在Hasor 中您也可以使用相同的方式，但是 Hasor 为您提供了一种更简单的方式，如下：
```java
public class TestBean {
    @Inject()
    private AppContext appContext;
}
```

&emsp;&emsp;您的眼睛没有看错，就是这么简单。一个 @Inject 就可以搞定。

---
&emsp;&emsp;另外 Hasor 还提供了一些其它方式方便您在一些特殊场景下拿到 AppContext。
```java
public class AwareBean implements AppContextAware {
    public void setAppContext(AppContext appContext) {
       ...
    }
}

appContext.getInstance(AwareBean.class);
```

&emsp;&emsp;您还可以利用 Hasor 的事件机制来拿到 AppContext，例如：
```java
public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        Hasor.autoAware(apiBinder.getEnvironment(),new AwareBean());
    }
}
```

&emsp;&emsp;`Hasor.autoAware`方法使用时，要注意，一定要在 Hasor onStart 阶段之前调用，否则您即便是调用了这个方法也不会得到 AppContext 对象。这是因为 aware 是通过 ContextEvent_Started 事件完成 AppContext 对象获取的。