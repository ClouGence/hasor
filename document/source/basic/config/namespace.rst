配置冲突
------------------------------------
我们在讨论一下特殊情况，这种情况我们要尽量回避，以避免给自己带来麻烦。举例Xml：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor debug="false">
            <debug>true</debug>
        </hasor>
    </config>


上面的配置文件中我们发现 `hasor` 节点下面有两个节点，一个是元素节点叫 `debug`，一个是属性节点也叫 `debug`。根据我们上面的路径规则，这两个节点的最终表达式均为：`hasor.debug`，这种情况我们称之为配置冲突。

命名空间
------------------------------------
在 xml 领域中有一种情况和我们的配置冲突很相似。叫做 命名冲突，具体是指：当两个不同的文档使用相同的元素名时，就会发生命名冲突。

由于 Hasor 中 xml 元素和属性是等价关系，因此产生了冲突。下面我们就利用 xml 命名空间把它们隔离开例如：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns:mod1="http://mode1.myProject.net" xmlns:mod2="http://mode2.myProject.net" xmlns="http://project.hasor.net/hasor/schema/main">
      <!-- mode1.myProject.net 配置 -->
      <mod1:config>
        <mod1:appSettings>
          <mod1:serverLocal mod1:url="www.126.com" />
        </mod1:appSettings>
      </mod1:config>
      <!-- http://mode2.myProject.net 配置 -->
      <mod2:config>
        <mod2:appSettings>
          <mod2:serverLocal mod2:url="www.souhu.com" />
        </mod2:appSettings>
      </mod2:config>
    </config>


接着我们按照命名空间来读取配置：

.. code-block:: java
    :linenos:

    Settings mod1 = settings.getSettings("http://mode1.myProject.net");
    Settings mod2 = settings.getSettings("http://mode2.myProject.net");
    System.out.println(mod1.getString("appSettings.serverLocal.url"));
    System.out.println(mod2.getString("appSettings.serverLocal.url"));


你也可以拆分这个配置文件为两个独立的配置文件：

.. code-block:: xml
    :linenos:

    a-config.xml（jarA）
    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://mode1.myProject.net">
        <appSettings>
            <serverLocal url="www.126.com" />
        </appSettings>
    </config>
    a-config.xml（jarB）
    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://mode2.myProject.net">
        <appSettings>
            <serverLocal url="www.souhu.com" />
        </appSettings>
    </config>


.. HINT::

    这种在同一个配置文件中定义多个命名空间的情况很少出现。在 Hasor 的所有项目中只有 hasor-core.jar 中借助该方法像默认命名空间设置了一个默认属性。


模块化配置文件
------------------------------------
下面这个就是 RSF 框架的静态配置文件骨架：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/hasor-rsf">
        <hasor.rsfConfig>
            ...
        </hasor.rsfConfig>
    </config>


下面这个就是 tConsole 框架的静态配置文件骨架：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/hasor-tconsole">
        <hasor.tConsole>
            ...
        </hasor.tConsole>
    </config>


命名空间优先权
------------------------------------
多个命名空间配置相同的节点，在读取配置时 `http://project.hasor.net/hasor/schema/main` 命名空间下的配置享有优先权。其它命名空间的配置按照字符串排序顺序决定。

换句话说当我们尝试读取 “myconfig.driver” 这个配置时，Hasor 会优先尝试在 `http://project.hasor.net/hasor/schema/main` 中读取。
