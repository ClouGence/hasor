复合拦截器
------------------------------------
复合拦截器，有两层含义
1. 可以写多个拦截器，同时拦截同一个切点。
2. 可以有多个拦截器同时生效在不同的位置。

第一点，多个拦截器同时拦截一个切点。它们的生效顺序是先 A 后 B。代码如下：

.. code-block:: java
    :linenos:

    @Aop({ SimpleInterceptorA.class, SimpleInterceptorB.class })
    public class AopBean {
        ...
    }


第二点，多个拦截器生效在不同位置：

.. code-block:: java
    :linenos:

    @Aop(ClassInterceptor.class)
    public class AopBean {
        public String print() {
            ...
        }
        @Aop(MethodInterceptor.class)
        public String echo(String sayMessage) {
            return "echo :" + sayMessage;
        }
    }


在这个 case 中 print 方法调用时 `ClassInterceptor` 拦截器生效，当调用 `echo` 方法时 `ClassInterceptor` 和 `MethodInterceptor` 会同时生效，生效顺序为，先 Class 后 Method。

如果还有一个全局 Aop 也在配置中，那么拦截器的生效顺序是：**全局级 -> 类级 -> 方法级**
