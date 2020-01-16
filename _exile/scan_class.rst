使用类扫描
------------------------------------
在一些情况下，经常会以类扫描的形式在启动时找到某些类。

Hasor是具备类扫描功能的，默认情况下 Hasor 在启动时不会进行类扫描。除非您使用了某些 Api 或插件引发了类扫描。

下面假定我们有一个接口，这个接口有若干实现类，例如 Module 接口。我们希望找到所有 Module 接口的实现类。那么可以这样：

.. code-block:: java
    :linenos:

    Set<Class<?>> aClass = apiBinder.findClass(Module.class);


再比如，我们有一个注解 @MappingTo 现在想找到标记了这个注解的所有类。那么可以这样：

.. code-block:: java
    :linenos:

    Set<Class<?>> aClass = apiBinder.findClass(MappingTo.class);


倘若我想寻找某个类的子类呢，可以这样：

.. code-block:: java
    :linenos:

    Set<Class<?>> aClass = apiBinder.findClass(AbstractUser.class);


下面要说的这些场景 Hasor 的类扫描机制是无法满足的。

1. 我想找到包含某个方法的所有类。
2. 我想找到方法上标记了某个注解的所有类。
3. 扫描类并返回要找的方法。
4. 根据某个特殊规则扫描并返回类的集合。