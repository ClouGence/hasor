LifeModule
------------------------------------
一般来说当我们定义 ``Module`` 时使用的是 “net.hasor.core.Module” 接口，这个接口仅有一个 loadModule 方法。因此它是不具备生命周期特性的。如果使用生命周期，您需要更换使用 “net.hasor.core.LifeModule” 接口。LifeModule 继承了 Module 同时新增了两个生命周期方法。

.. image:: http://files.hasor.net/uploader/20170310/111859/CC2_C40A_8741_5534.jpg

LifeModule 的执行阶段一共分为三个,它们是：loadModule、onStart、onStop。

现在我们用一个小例子来想你展示 Hasor 生命周期的特征，首先我们新建一个类，这个类实现了 LifeModule 接口。我们在每一个周期到来时打印一行日志。

.. code-block:: java
    :linenos:

    public class OnLifeModule implements LifeModule {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            logger.info("初始化拉...");
        }
        public void onStart(AppContext appContext) throws Throwable {
            logger.info("启动啦...");
        }
        public void onStop(AppContext appContext) throws Throwable {
            logger.info("停止啦...");
        }
    }


接下来我们用最简单的方式启动 Hasor 并加载这个 Module，当 Hasor 启动之后我们可以看到控制台上先后打印出 “初始化拉...”、“启动啦...”，当jvm 退出时我们还会看到控制台打印“停止啦...”。

.. code-block:: java
    :linenos:

    Hasor.createAppContext(new OnLifeModule());

通过事件监听生命周期
------------------------------------
下面介绍一种方式可以在不使用 LifeModule 的前提下，借助 Event 机制监听到生命周期调用。

.. code-block:: java
    :linenos:

    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // Bean
        BindInfo<LifeBean> bindInfo = apiBinder.bindType(LifeBean.class)
                .to(LifeBeanImpl.class).toInfo();
        // 启动事件
        Hasor.addStartListener(apiBinder.getEnvironment(), bindInfo);
        // 停止事件
        Hasor.addShutdownListener(apiBinder.getEnvironment(), bindInfo);
    }

如果你的 Bean 是 new 出来的，不需要经过 Hasor 容器来 create 那么可以更简单如下。

.. code-block:: java
    :linenos:

    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // Bean
        LifeBean lifeBean = new LifeBeanImpl();
        apiBinder.bindType(LifeBean.class).toInstance(lifeBean);
        // 启动事件
        Hasor.addStartListener(apiBinder.getEnvironment(), lifeBean);
        // 停止事件
        Hasor.addShutdownListener(apiBinder.getEnvironment(), lifeBean);
    }


ContextListener
------------------------------------
当你开发一个 Hasor 插件时可能会关注这样一个时刻：“当所有插件都加载完毕”。

.. CAUTION::
    请注意，这相当于拦截所有 Module 的生命周期调用。

这个 Case 下我们上面两个方式都无法满足你的要求，因为当加载你的 LifeModule 时你并不能确定当前 Module 是最后一个。这时候就需要用到 ContextStartListener 接口和 ContextShutdownListener接口。

- ContextStartListener 接口中的两个方法，分别应对的是 LifeModule 接口中 `onStart` 方法调用前和调用后。
- ContextShutdownListener 接口中的两个方法，分别应对的是 LifeModule 接口中 `onStop` 方法调用前和调用后。
