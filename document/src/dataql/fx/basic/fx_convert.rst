--------------------
转换函数库
--------------------
引入集合函数库的方式为：``import 'net.hasor.dataql.fx.basic.ConvertUdfSource' as convert;``

toInt
------------------------------------
函数定义：``Number toInt(target)``

- **参数定义：** ``target`` 类型：Object
- **返回类型：** ``Number``
- **作用：** 将参数转换为 Number。

**例子**

.. code-block:: js
    :linenos:

    convert.toInt("12")     = 12
    convert.toInt(12)       = 12
    convert.toInt("0x12")   = 18
    convert.toInt("1.2e10") = 12000000000
    convert.toInt("")       = 0
    convert.toInt(null)     = 0
    convert.toInt("abc")    = throw Error

toString
------------------------------------
函数定义：``String toString(target)``

- **参数定义：** ``target`` 类型：Object
- **返回类型：** ``String``
- **作用：** 将参数转换为 String。

内部实现逻辑为： ``String.valueOf(target)``

**例子**

.. code-block:: js
    :linenos:

    convert.toString("12")          = "12"
    convert.toString(12)            = "12"
    convert.toString("0x12")        = "0x12"
    convert.toString("1.2e10")      = "1.2e10"
    convert.toString("")            = ""
    convert.toString(null)          = "null"
    convert.toString("abc")         = "abc"
    convert.toString([1,2,3,4])     = "[1, 2, 3, 4]"
    convert.toString({"tet":123})   = "{tet=123}"

toBoolean
------------------------------------
函数定义：``Boolean toBoolean(target)``

- **参数定义：** ``target`` 类型：Object
- **返回类型：** ``Boolean``
- **作用：** 将参数转换为 Boolean。

**例子**

.. code-block:: js
    :linenos:

    convert.toBoolean(null)    = null
    convert.toBoolean("true")  = Boolean.TRUE
    convert.toBoolean("false") = Boolean.FALSE
    convert.toBoolean("on")    = Boolean.TRUE
    convert.toBoolean("ON")    = Boolean.TRUE
    convert.toBoolean("off")   = Boolean.FALSE
    convert.toBoolean("oFf")   = Boolean.FALSE
    convert.toBoolean("blue")  = null
