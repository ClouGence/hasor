自定义Aop注解
------------------------------------
前面三个小节已经可以应对大部分 Aop 使用场景，那么本小节就讲解一下 Hasor Aop 的高级用法用来应对一些及特殊的场景。

我们先以一个小例子作为开始。比如我们需要实现一个自己的 Service 注解，凡事调用 Service 时都打印一行日志。

首先声明自己的注解。

.. code-block:: java
    :linenos:

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface MyAop {
    }


其次编写拦截器

.. code-block:: java
    :linenos:

    public class SimpleInterceptor implements MethodInterceptor {
        public static boolean called = false;
        public Object invoke(MethodInvocation invocation) throws Throwable {
            called = true;
            try {
                System.out.println("before... ");
                Object returnData = invocation.proceed();
                System.out.println("after...");
                return returnData;
            } catch (Exception e) {
                System.out.println("throw...");
                throw e;
            }
        }
    }


最后，配置拦截器的筛选器。筛选所有标记了 MyAop 注解的 Bean 都使用我们的拦截器，我们在 Module 中进行如下声明：

.. code-block:: java
    :linenos:

    public class MyAopSetup implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            //1.任意类
            Matcher<Class<?>> atClass = AopMatchers.anyClass();
            //2.有MyAop注解的方法
            Matcher<Method> atMethod = AopMatchers.annotatedWithMethod(MyAop.class);
            //3.让@MyAop注解生效
            apiBinder.bindInterceptor(atClass, atMethod, new SimpleInterceptor());
        }
    }

拦截匹配器
------------------------------------
现在引出我们的主角 Matcher 接口，接口的定位是进行筛选规则匹配。无论是筛选类还是筛选方法，都是经过该接口。

在上面例子中我们的场景是任意标记了 @MyAop 注解的类，假如我们的 aop 有效范围是，任意标记了 @MyAop 的 Controller 呢？

作为一个 Controller 肯定有它的一个特殊标记，例如在 Hasor 中 Controller 是一个接口。如果类是一个 Controller 那它一定实现了这个接口，可以理解为一定是 Controller 的子类。

这时上面的 `AopMatchers.anyClass();` 就可以改为 `AopMatchers.subClassesOf(Controller.class)`。更多功能您可以参看 `AopMatchers` 类，如果没有满足要求的方法。开发者还可以自己编写一个 Matcher 来完成自己的筛选规则。
