--------------------
编码函数库
--------------------
引入编码函数库的方式为：``import 'net.hasor.dataql.fx.encryt.CodecUdfSource' as codec;``

encodeString
------------------------------------
函数定义：``String encodeString(String)``

- **参数定义：** ``target`` 类型：String
- **返回类型：** ``String``
- **作用：** 对字符串组进行 Base64编码。

**例子**

.. code-block:: js
    :linenos:

    codec.encodeString("Hello Word.") = 'SGVsbG8gV29yZC4='
    codec.encodeString("")            = ''
    codec.encodeString(null)          = null

decodeString
------------------------------------
函数定义：``Object decodeString(jsonString)``

- **参数定义：** ``jsonString`` 类型：String
- **返回类型：** ``Object``
- **作用：** 将 Base64 解码为字符串。

**例子**

.. code-block:: js
    :linenos:

    codec.decodeString("SGVsbG8gV29yZC4=") = 'Hello Word.'
    codec.decodeString("")                 = ''
    codec.decodeString(null)               = null

encodeBytes
------------------------------------
函数定义：``String encodeBytes(target)``

- **参数定义：** ``target`` 类型：List<Byte>
- **返回类型：** ``String``
- **作用：** 对字节数组进行 Base64编码。

**例子**

.. code-block:: js
    :linenos:

    codec.encodeBytes([1,2,3,4,5,6,7]) = 'AQIDBAUGBw=='
    codec.encodeBytes([])              = ''
    codec.encodeBytes(null)            = null

decodeBytes
------------------------------------
函数定义：``String decodeBytes(target)``

- **参数定义：** ``target`` 类型：String
- **返回类型：** ``List<Byte>``
- **作用：** 将 Base64 解码为字节数组。

**例子**

.. code-block:: js
    :linenos:

    codec.decodeBytes('AQIDBAUGBw==') = [1,2,3,4,5,6,7]
    codec.decodeBytes('')             = []
    codec.decodeBytes(null)           = null

urlEncode
------------------------------------
函数定义：``String urlEncode(target)``

- **参数定义：** ``target`` 类型：String/Boolean/Number/Null
- **返回类型：** ``String``
- **作用：** 对字符串进行 URL 编码。

**例子**

.. code-block:: js
    :linenos:

    codec.urlEncode('abc')  = 'abc'
    codec.urlEncode(12345)  = '12345'
    codec.urlEncode(true)   = 'true'
    codec.urlEncode('/')    = '%2F'
    codec.urlEncode('中文') = '%E4%B8%AD%E6%96%87'
    codec.urlEncode(null)   = null

urlDecode
------------------------------------
函数定义：``String urlDecode(target)``

- **参数定义：** ``target`` 类型：String/Boolean/Number/Null
- **返回类型：** ``String``
- **作用：** 对字符串进行 URL 解码。

**例子**

.. code-block:: js
    :linenos:

    codec.urlDecode('abc')  = 'abc'
    codec.urlDecode(12345)  = '12345'
    codec.urlDecode(true)   = 'true'
    codec.urlDecode('%2F')  = '/'
    codec.urlDecode('%E4%B8%AD%E6%96%87') = '中文'
    codec.urlDecode(null)   = null
