自定义Aop注解
------------------------------------
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

