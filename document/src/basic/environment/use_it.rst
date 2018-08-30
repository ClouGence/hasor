配置环境变量
------------------------------------
配置 Hasor 的环境变量有很多渠道，最简单的就是像配置 `JAVA_HOME` 一样配置一个系统环境环境变量。Hasor 的环境变量列表中是包含系统环境变量的。为了方便大家记忆，下面列了一个清单。表示属于 Hasor 环境变量范畴的都有哪些：

1. 操作系统的环境变量，例如：`JAVA_HOME` or `System.getenv()`
2. JVM的启动参数，例如：`java xxxx -Dvar=...`
3. Java的属性，例如：`System.getProperties()`
4. Hasor的 `env.config` 属性文件
5. Hasor配置文件(含静态配置文件)中的 `hasor.environmentVar` 配置节点。

下面我们主要讲解 `hasor.environmentVar` 节点的配置方法，下面通过一个例子来介绍如何读取到环境变量：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.environmentVar>
            <MY_VAR>Hello Word</MY_VAR>
        </hasor.environmentVar>
    </config>


在项目中读取这个环境变量：

.. code-block:: java
    :linenos:

    AppContext appContext = ...
    Environment env = appContext.getEnvironment();
    System.out.println(env.evalString("%MY_VAR%"));

.. CAUTION::
    在 Hasor 中读取环境变量目前您不可以通过类似 `xx.getVar("MY_VAR")` 这种 API 来获取，您必须通过表达式获取。环境变量表达式的写法是：`%<var name>%`。


环境变量表达式
------------------------------------
例1：引用其它环境变量

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.environmentVar>
            <MY_VAR>Hello Word , this is my JAVA_HOME : %JAVA_HOME%</MY_VAR>
        </hasor.environmentVar>
    </config>


MY_VAR 的输出结果是：`Hello Word , this is my JAVA_HOME : xxxxxx`


例2：引用多个环境变量

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.environmentVar>
            <MY_NAME>zyc</MY_NAME>
            <MY_AGE>100</MY_>
            <MY_VAR>my name is : %MY_NAME% , age is %MY_AGE%</MY_VAR>
        </hasor.environmentVar>
    </config>


MY_VAR 的输出结果是：`my name is : zyc , age is 100`


启动传参
------------------------------------
现在我们在 例2 的基础上不做任何代码变化，只是在 jvm 启动时增加一个参数：`-DMY_NAME=ccc`，在看输出结果就变成了：`my name is : ccc , age is 100`

.. code-block:: java
    :linenos:

    AppContext appContext = ...
    Environment env = appContext.getEnvironment();
    System.out.println(env.evalString("i say %MY_VAR%."));


本质上 Hasor 对于环境变量的定义是不区分大小写的，您可以在 `environmentVar` 节点中定义全大写环境变量名，也可以是全小写，甚至大小写混运用。但是这样做之后 xml 是无法检测出任何错误，但是 Hasor 框架在加载您的配置文件时会把它们统一做大写化转换，进而导致您的环境变量配置失效。所以我们做出君子约定。

.. CAUTION::
    环境变量名，要求全部必须大写