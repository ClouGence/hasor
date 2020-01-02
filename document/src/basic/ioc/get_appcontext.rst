注入容器类型
------------------------------------
用过 Spring 的同学都知道要想注入 Spring 容器本身您必须要实现 ApplicationContextAware 接口。在Hasor 中您也可以使用相同的方式：

.. code-block:: java
    :linenos:

    public class AwareBean implements AppContextAware {
        public void setAppContext(AppContext appContext) {
            ...
        }
    }

    appContext.getInstance(AwareBean.class);


但是 Hasor 中也允许你用更简单的方式，直接通过 @Inject 进行注入，如下：

.. code-block:: java
    :linenos:

    public class TestBean {
        @Inject()
        private AppContext appContext;
    }


容器可以被注入的特殊类型有：

+-------------------------------+--------------------+
| 功效                          | 接口               |
+===============================+====================+
| net.hasor.core.AppContext     | 容器               |
+-------------------------------+--------------------+
| net.hasor.core.Settings       | 配置文件接口       |
+-------------------------------+--------------------+
| net.hasor.core.Environment    | 环境变量接口       |
+-------------------------------+--------------------+
| net.hasor.core.spi.SpiTrigger | SPI 触发器         |
+-------------------------------+--------------------+
| net.hasor.core.EventContext   | 容器事件模型接口   |
+-------------------------------+--------------------+
