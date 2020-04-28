--------------------
比较函数库
--------------------
引入集合函数库的方式为：``import 'net.hasor.dataql.fx.basic.CompareUdfSource' as compare;``

compareString
------------------------------------
函数定义：``int compareString(str1, str2)``

- **参数定义：** ``str1`` 类型：String，``str2`` 类型：String
- **返回类型：** ``Number``
- **作用：** 比较两个字符串，使用Java原生的字符串 compareTo 比较方法来实现。

**作用**

- 比较两个字符串大小，主要用作排序场景。

**例子**

.. code-block:: js
    :linenos:

    compare.compareString("A","a")      = -32
    compare.compareString("a","A")      = 32
    compare.compareString("abd","abc")  = 1
    compare.compareString("abc","abd")  = -1

compareStringIgnoreCase
------------------------------------
函数定义：``int compareStringIgnoreCase(str1, str2)``

- **参数定义：** ``str1`` 类型：String，``str2`` 类型：String
- **返回类型：** ``Number``
- **作用：** 忽略大小写比较两个字符串，使用Java原生的字符串 compareToIgnoreCase 比较方法来实现。

**作用**

- 比较两个字符串大小，主要用作排序场景。

**例子**

.. code-block:: js
    :linenos:

    compare.compareStringIgnoreCase("a","A")      = 0
    compare.compareStringIgnoreCase("A","a")      = 0
    compare.compareStringIgnoreCase("abd","abc")  = 1
    compare.compareStringIgnoreCase("abc","abd")  = -1
