ApiBinder
------------------------------------
ApiBinder 和 Module 一样，都是 Hasor 的基础。也是您接触 Hasor 必然接触到的东西。它是你通往 Hasor 的一个重要入口。

- Apibinder 是您模块在 init 阶段唯一可以接触到的接口。
- 它仅在 init 阶段有效，您不可以使用任何方式在除 init 阶段之外任何地方使用这个接口。
- ApiBinder 可以提供给你 Settings 接口用来读取配置文件。
- 它可以让您使用代码形式进行依赖注入。
- 您可以通过 ApiBinder 获取 Environment 接口来操作环境变量。


在 Hasor 2.3 之后，由于 ApiBinder 扩展机制的引入，ApiBinder 变可以随时进行扩展。下面我们展现一下相关特性。例：扩展 Apibinder 并在自己的 TestBinder 上实现一个 Hello Word 方法。

.. code-block:: java
    :linenos:

    package net.test.binder;
    public interface TestBinder extends ApiBinder {
        public void hello();
    }
    public class TestBinderImpl extends ApiBinderWrap implements TestBinder {
        public TestBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        public void hello() {
            System.out.println("Hello Binder");
        }
    }
    public class TestBinderCreater implements ApiBinderCreater {
        public TestBinder createBinder(ApiBinder apiBinder) {
            return new TestBinderImpl(apiBinder);
        }
    }


接着我们需要在配置文件中增加如下配置：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.apiBinderSet>
            <binder type="net.test.binder.TestBinder">net.test.binder.TestBinderCreater</binder>
        </hasor.apiBinderSet>
    </config>


最后我们在 Hasor 的启动Module中使用这个自定义的 ApiBinder

.. code-block:: java
    :linenos:

    package net.test.hasor;
    public class HelloModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            TestBinder myBinder = (TestBinder)apiBinder;
            or
            TestBinder myBinder = apiBinder.tryCast(TestBinder.class);

            myBinder.hello();
        }
    }

在上面例子中建议您在使用 ``(TestBinder)apiBinder`` 强制类型转换时先进行一下类型判断，以避免 TestBinder 没有生效而造成的类型转换异常。如果您使用的是 tryCast 方式那么就不需要关注这个细节，如果正常加载 tryCast 就会正确返回给您一个 TestBinder。如果没有正确配置，它会返回一个 null。