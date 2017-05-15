&emsp;&emsp;本小节重点介绍 `原型模式` 原型模式和单例模式是正反的一对关系。一般情况下 Hasor 在创建 Bean 时候，都是原型模式下的Bean。因此开发者不需要做任何配置。

&emsp;&emsp;如果您使用了前一个小节上提到的 `default` 配置修改了 Hasor 的默认配置。那么就相当于每个类都加上了 @Singleton 注解，如果此时创建某个 Bean 不想要它是一个单例 Bean，那么就需要明确指定原型模式。例如下面：
```java
@Prototype()
public class AopBean {
    ...
}
```

&emsp;&emsp;或者您可以通过 Apibinder 方式进行代码形式声明：
```java
public class MyModule implements Module {
    public void loadModule(ApiBinder apiBinder) {
        apiBinder.bindType(PojoInfo.class).asEagerPrototype();
    }
}
```