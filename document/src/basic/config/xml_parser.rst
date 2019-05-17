解析Xml
------------------------------------
现在我们设想一下这样的一组配置信息，我想读取所有的 module ：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor>
            <modules loadModule="${HASOR_LOAD_MODULE}" loadErrorShow="true">
                <module>net.hasor.web.render.RenderWebPlugin</module>
                <module>net.hasor.plugins.startup.StartupModule</module>
                <module>net.hasor.plugins.aop.AopModule</module>
            </modules>
        </hasor>
    </config>


- 方法一：通过父节点解析Xml信息，具体如下：

.. code-block:: java
    :linenos:

    XmlNode modules = env.getSettings().getXmlNode("hasor.modules");
    List<XmlNode> allModule = modules.getChildren("module");
    for (XmlNode modInfo : allModule){
        ...
    }

- 方法二：考虑到我们的例子中 `module` 节点并没有定义特殊的属性，因此可以进一步从上面代码简化成如下：

.. code-block:: java
    :linenos:

    String[] allModules = env.getSettings().getStringArray("hasor.modules.module");


Hasor 的配置文件读取十分强大，更多强大的方法，请开发者自行尝试：`Settings` 接口。获取这个接口的方式很多，您可以依赖注入，也可以通过 ApiBinder 接口拿到，也可以通过 AppContxt 接口获取。这里不在详解。

