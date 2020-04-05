定义拦截器
------------------------------------
首先便于说明先定义一个简单的拦截器，这个拦截器只打印几行日志

.. code-block:: java
    :linenos:

    public class SimpleInterceptor implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
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


方法级拦截器
------------------------------------
在某个类中只有某些特定的方法需要被拦截，那么就要使用方法级拦截器

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


类级拦截器
------------------------------------
在 Hasor 中为 Bean 配置拦截器只需要一个注解即可，被标注类的所有方法就都被拦截了

.. code-block:: java
    :linenos:

    @Aop(SimpleInterceptor.class)
    public class AopBean {
        public String echo(String sayMessage) {
            return "echo :" + sayMessage;
        }
    }


全局拦截器
------------------------------------
全局拦截器实际是 `匹配任意类任意方法` 的一个拦截器。这种拦截器需要在 Module 中声明

.. code-block:: java
    :linenos:

    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            //1.任意类
            Matcher<Class<?>> atClass = Matchers.anyClass();
            //2.任意方法
            Matcher<Method> atMethod = Matchers.anyMethod();
            //3.注册拦截器
            apiBinder.bindInterceptor(atClass, atMethod, new SimpleInterceptor());
        }
    }

.. CAUTION::
    请不要滥用全局拦截器。


拦截器的匹配器
------------------------------------
拦截器的匹配器 `net.hasor.core.exts.aop.Matchers`

匹配所有类
    - `Matchers.anyClass();`

匹配所有方法
    - `Matchers.anyMethod();`

匹配标记了 @MyAop 注解的类
    - `Matchers.annotatedWithClass(MyAop.class);`

匹配标记了 @MyAop 注解的方法
    - `Matchers.annotatedWithMethod(MyAop.class);`

匹配 List 类型的子类
    - `Matchers.subClassesOf(List.class);`

按照通配符匹配类
    - 格式为：<包名>.<类名>
    - 通配符符号为：?表示任意一个字符；*表示任意多个字符。
    - `Matchers.expressionClass("abc.foo.*");`

按照通配符匹配方法
    - 格式为：<返回值> <类名>.<方法名>(<参数签名列表>)
    - 通配符符号为：?表示任意一个字符；*表示任意多个字符。
    - `Matchers.expressionMethod("abc.foo.*");`


通配符匹配方法样例

.. code-block:: text
    :linenos:

     *  * *.*()                  匹配：任意无参方法
     *  * *.*(*)                 匹配：任意方法
     *  * *.add*(*)              匹配：任意add开头的方法
     *  * *.add*(*,*)            匹配：任意add开头并且具有两个参数的方法。
     *  * net.test.hasor.*(*)    匹配：包“net.test.hasor”下的任意类，任意方法。
     *  * net.test.hasor.add*(*) 匹配：包“net.test.hasor”下的任意类，任意add开头的方法。
     *  java.lang.String *.*(*)  匹配：任意返回值为String类型的方法。
