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

byteToHex
------------------------------------
函数定义：``String byteToHex(target)``

- **参数定义：** ``target`` 类型：List<Byte>
- **返回类型：** ``String``
- **作用：** 将二进制数据转换为16进制字符串。

**例子**

.. code-block:: js
    :linenos:

    convert.byteToHex([123]) = '7B'
    convert.byteToHex([])    = ''
    convert.byteToHex(null)  = null

hexToByte
------------------------------------
函数定义：``List<Byte> hexToByte(target)``

- **参数定义：** ``target`` 类型：String
- **返回类型：** ``List<Byte>``
- **作用：** 将16进制字符串转换为二进制数据。

**例子**

.. code-block:: js
    :linenos:

    convert.hexToByte('7B7B') = [123,123]
    convert.hexToByte('')     = []
    convert.hexToByte(null)   = null

stringToByte
------------------------------------
函数定义：``List<Byte> stringToByte(target, charset)``

- **参数定义：** ``target`` 类型：String；``charset`` 类型：String，字符集
- **返回类型：** ``List<Byte>``
- **作用：** 字符串转换为二进制数据。

**例子**

.. code-block:: js
    :linenos:

    convert.stringToByte('1234','utf-8')   [49,50,51,52]
    convert.stringToByte('1234','utf-16')  [-2,-1,0,49,0,50,0,51,0,52]

byteToString
------------------------------------
函数定义：``String byteToString(target, charset)``

- **参数定义：** ``target`` 类型：List<Byte>；``charset`` 类型：String，字符集
- **返回类型：** ``String``
- **作用：** 二进制数据转换为字符串。

**例子**

.. code-block:: js
    :linenos:

    convert.byteToString([49,50,51,52], 'UTF-8')                = '1234'
    convert.byteToString([-2,-1,0,49,0,50,0,51,0,52], 'UTF-16') = '1234'
