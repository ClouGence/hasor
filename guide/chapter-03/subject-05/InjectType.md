&emsp;&emsp;在 Hasor 中进行依赖注入十分简单，假如我们有一个类 PojoBean 有一个 pojo 的属性，想要实现依赖注入。你只需要在 pojo 字段上加上 @Inject 注解即可。

```java
public class InfoBean {
    @Inject
    private PojoBean pojo;
}

public class PojoBean {
    ...
}
```