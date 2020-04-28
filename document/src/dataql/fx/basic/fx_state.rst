--------------------
状态函数库
--------------------
引入状态函数库的方式为：``import 'net.hasor.dataql.fx.basic.StateUdfSource' as state;``

decNumber
------------------------------------
函数定义：``Udf decNumber(initValue)``

- **参数定义：** ``initValue`` 类型：Number，起始数字
- **返回类型：** ``Udf``
- **作用：** 返回一个Udf，每次调用这个UDF，都会返回一个 Number。Number值较上一次会自增 1。


**例子**

.. code-block:: js
    :linenos:

    var decNum = state.decNumber(0);
    var next = decNum() // next = 1
    var next = decNum() // next = 2
    var next = decNum() // next = 3

incNumber
------------------------------------
函数定义：``Udf incNumber(initValue)``

- **参数定义：** ``initValue`` 类型：Number，起始数字
- **返回类型：** ``Udf``
- **作用：** 返回一个Udf，每次调用这个UDF，都会返回一个 Number。Number值较上一次会自减 1。


**例子**

.. code-block:: js
    :linenos:

    var decNum = state.incNumber(0);
    var next = decNum() // next = -1
    var next = decNum() // next = -2
    var next = decNum() // next = -3

