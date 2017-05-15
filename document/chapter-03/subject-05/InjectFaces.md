&emsp;&emsp;除了上面这种方式直接注入一个对象之外，Hasor 还支持注入一个接口。让我们改进一下上面的例子加以解释说明。首先 PojoBean 是一个接口，那么 InfoBean 在注入的时就是要注入一个接口，例如下面：
```java
public class InfoBean {
    @Inject
    private PojoBean pojo;
}

public interface PojoBean {
    ...
}
```

&emsp;&emsp;这种情况下如果使用 @Inject 注入一个接口，框架会因为找不到具体的实现类而导致注入失败，因此在这种情况下我们要在接口上标记上 @ImplBy 注解，这个注解会告诉框架这个接口的实现类是哪个，例如下面这样：
```java
public class InfoBean {
    @Inject
    private PojoBean pojo;
}

@ImplBy(PojoBeanImpl.class)
public interface PojoBean {
    ...
}

public class PojoBeanImpl implements PojoBean {
    ...
}
```

&emsp;&emsp;如果您不想使用 @ImplBy Hasor 还允许你在 install 阶段自己手动设置 PojoBean 接口的实现类，为了实现目标我们必须要在 Module 的 loadModule 方法中使用下面这样的代码来指定接口和实现类的关系：
```java
apiBinder.bindType(PojoBean.class).to(PojoBeanImpl.class);
```
