&emsp;&emsp;在一些特定情况下，我们希望程序在启动时找到某些类。虽然我们可以通过 xml 配置的方式获取到这些类的名字，但是类扫描还是方便很多。

&emsp;&emsp;Hasor是具备类扫描功能的，默认情况下 Hasor 在启动时不会进行类扫描。除非您使用了某些 Api 或插件引发了类扫描。

&emsp;&emsp;下面假定我们有一个接口，这个接口有若干实现类，例如 Module 接口。我们希望找到所有 Module 接口的实现类。那么可以这样：
```java
Set<Class<?>> aClass = apiBinder.findClass(Module.class);
```

&emsp;&emsp;再比如，我们有一个注解 @MappingTo 现在想找到标记了这个注解的所有类。那么可以这样：
```java
Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);
```

&emsp;&emsp;倘若我想寻找某个类的子类呢，可以这样：
```java
Set<Class<?>> aClass = apiBinder.findClass(AbstractUser.class);
```

&emsp;&emsp;或许你会问，这不都是一样吗！是的 Hasor 的类扫描就是这么简单，一个 Api 完成你的所需。下面要说的这些场景 Hasor 的类扫描机制是无法满足的。
- 1.我想找到包含某个方法的所有类。
- 2.我想找到方法上标记了某个注解的所有类。
- 3.扫描类并返回要找的方法。
- 4.根据某个特殊规则扫描并返回类的集合。