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

uuid
------------------------------------
函数定义：``String uuid()``

- **参数定义：** 无
- **返回类型：** ``String``
- **作用：** 返回一个完整格式的 UUID 字符串。

**例子**

.. code-block:: js
    :linenos:

    state.uuid() = "bc4b0433-0427-4d0f-9f0b-5e9b7a0a281e"
    state.uuid() = "f573a2dd-5dd3-41c8-8bf6-f5e794b7d3f4"
    state.uuid() = "dcae141d-2e1e-4079-8cd2-ded2cfa7d9d8"

uuidToShort
------------------------------------
函数定义：``String uuidToShort()``

- **参数定义：** 无
- **返回类型：** ``String``
- **作用：** 返回一个不含"-" 符号的 UUID 字符串。

**例子**

.. code-block:: js
    :linenos:

    state.uuidToShort() = "bc4b043304274d0f9f0b5e9b7a0a281e"
    state.uuidToShort() = "f573a2dd5dd341c88bf6f5e794b7d3f4"
    state.uuidToShort() = "dcae141d2e1e40798cd2ded2cfa7d9d8"
