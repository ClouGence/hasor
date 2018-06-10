类级拦截器
------------------------------------
前面章节我们看到了使用 Hasor 进行 Aop 开发的一个简单例子。Hasor 提供的 @Aop 注解不光可以标记到类上，它还可以标记在方法上。

如果 @Aop 标记到类上，那么 Aop 拦截器将会对整个类的 public、protected 方法生效，这叫做类级拦截器。

类级拦截器的例子这里展示一下它的特征代码：

.. code-block:: java
    :linenos:

    @Aop(SimpleInterceptor.class)
    public class AopBean {
        public String echo(String sayMessage) {
            return "echo :" + sayMessage;
        }
    }


方法级拦截器
------------------------------------
有时候我们不想拦截所有方法，只想对有限的几个方法做 Aop 拦截。这个时候就会用到 Hasor 的方法级拦截器，
还是以上面的 AopBean 为例，假设我们有两个方法。我们只想拦截 echo 方法调用，那么可以这样：

.. code-block:: java
    :linenos:

    public class AopBean {
        public String print() {
            ...
        }
        @Aop(SimpleInterceptor.class)
        public String echo(String sayMessage) {
            return "echo :" + sayMessage;
        }
    }


我们可以看出，方法级拦截器和类级拦截器的唯一区别就是 @Aop 注解放到方法上而不是类上。没错就这么简单！

全局拦截器
------------------------------------
假定我们希望在产品开发完毕之后，想检测一下方法的覆盖率。最简单的一种方式就是开发一些全局 Aop 每当方法调用时将方法名记录下来。
这样经过一段时间运行之后只要没有调用过的方法都可能是无用的方法。或者还没有触发相关业务场景。
接下来我们来讲解一下如何在 Hasor 中声明一个全局拦截器。

首先全局拦截器是一种比较重量级的拦截器，它相当于为所有类都做了一层 Aop。如果没有控制好性能，很容易因为全局拦截器拖垮整个应用的性能。

因此为了安全起见，Hasor 并没有提供任何注解方式声明全局 Aop 的途径。如果您想使用全局拦截器就要在 Module 中自己进行特别声明，例如如下代码就是将 SimpleInterceptor 作为全局拦截器进行声明。

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            //1.任意类
            Matcher<Class<?>> atClass = AopMatchers.anyClass();
            //2.任意方法
            Matcher<Method> atMethod = AopMatchers.anyMethod();
            //3.注册拦截器让@MyAop注解生效
            apiBinder.bindInterceptor(atClass, atMethod, new SimpleInterceptor());
        }
    }

