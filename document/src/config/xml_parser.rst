简单Xml解析
------------------------------------
读取所有 module 节点：

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

- 方法二：考虑到 `module` 节点并没有定义特殊的属性，因此可以进一步从上面代码简化成如下：

.. code-block:: java
    :linenos:

    String[] allModules = env.getSettings().getStringArray("hasor.modules.module");
