&emsp;&emsp;有时候我们希望有 Bean 可以在被创建时自动调用一个 init 方法，本小节就来向大家展示一下 Hasor 这方面的能力。

&emsp;&emsp;方式一：通过 @Init 注解，例如下面这样。
```java
public class PojoBean {
    @Init
    public void init(){
        ...
    }
}
```

&emsp;&emsp;方式二：通过编码方式在 Module 初始化时指定，例如下面这样。
```java
public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(PojoBean.class).initMethod("init");
    }
}
```

&emsp;&emsp;小贴士：如果您组合使用 @Singleton 注解和 @Init 注解，同时这个类在 Hasor 启动时通过 Module 预先注册到了 Hasor 容器中。那么 Hasor 会在启动时自动创建这个类并调用 init 方法。例如：
```java
@Singleton
public class PojoBean {
    @Init
    public void init(){
        ...
    }
}

public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(PojoBean.class);
    }
}
```

&emsp;&emsp;或者下面这样的方式也可以达到同样的效果：
```java
public class PojoBean {
    public void init(){
        ...
    }
}

public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(PojoBean.class)
            .initMethod("init")    // 初始化方法，相当于 @Init 注解
            .asEagerSingleton();   // 单例，相当于 @Singleton 注解
    }
}
```


