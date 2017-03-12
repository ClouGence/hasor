&emsp;&emsp;前面我们介绍了：类型注入、接口注入、名称注入。三种注入方式，下面我们在介绍一下 ID注入，这种方式和名称注入很像但有所不同。我们先看一下代码：
```java
package net.test.hasor;
public class HelloModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(InfoBean.class).idWith("beanA");
        apiBinder.bindType(InfoBean.class).idWith("beanB");
    }
}

public class UseBean {
    @Inject(value = "beanA" , byType = Type.ByID)
    private InfoBean pojoA;
    @Inject(value = "beanB" , byType = Type.ByID)
    private InfoBean pojoB;
}
```

#### ID注入和 Name注入的区别

&emsp;&emsp;ID注入，我们看到区别于名字注入的第一个特点是 nameWith 换成了 idWith，其次在使用 @Inject 注解时也变得更加复杂还需要指定 byType 属性。

&emsp;&emsp;下面我们来说一下 ID 注入和 Name 注入的本质区别，众所周知 IoC 在进行依赖注入时每一个要注入的 Bean 都是在 Hasor 容器中获取的。

&emsp;&emsp;如果把注入的代码转化为一般的 getBean，`Name方式` 和 `ID方式` 的区别应该像如下这样：
```java
appContext.findBindingBean("beanA",InfoBean.class);
appContext.findBindingBean("beanB",InfoBean.class);

appContext.getInstance("beanA");
appContext.getInstance("beanB");
```

&emsp;&emsp;ID方式还有一个最大的特征，那就是无论 ID 被用到那个类型中。一个 ID 不能被同时应用。这一点和 Name 方式有很大的区别。

&emsp;&emsp;这也就是说我么不能使用 idWith 来套用 `名称注入` 小节中的例子。

