&emsp;&emsp;Module 的执行阶段一共分为三个分别位于三个方法中。它们是：loadModule、onStart、onStop。其中 loadModule 方法是我们经常打交道的地方，它极为重要的。

&emsp;&emsp;底层 Module 可以分为两个接口，它们是：“net.hasor.core.Module”、“net.hasor.core.LifeModule”，其中 LifeModule 继承了 Module。LifeModule 接口和另一个最大的不同是它封装了 Hasor 容器的两个生命周期阶段。

![Module生命周期](http://files.hasor.net/uploader/20170310/111859/CC2_C40A_8741_5534.jpg "Module生命周期")

&emsp;&emsp;现在我们用一个小例子来想你展示 Hasor 生命周期的特征，首先我们新建一个类，这个类实现了 LifeModule 接口。我们在每一个周期到来时打印一行日志。
```java
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
```

&emsp;&emsp;接下来我们用最简单的方式启动 Hasor 并加载这个 Module，当 Hasor 启动之后我们可以看到控制台上先后打印出 “初始化拉...”、“启动啦...”，当jvm 推出时我们还会看到控制台打印“停止啦...”。
```java
Hasor.createAppContext(new OnLifeModule());
```
