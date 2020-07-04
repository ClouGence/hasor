--------------------
签名/编码函数库
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
- **作用：** 对字符串进行 URL 编码，使用 UTF-8。

**例子**

.. code-block:: js
    :linenos:

    codec.urlEncode('abc')  = 'abc'
    codec.urlEncode(12345)  = '12345'
    codec.urlEncode(true)   = 'true'
    codec.urlEncode('/')    = '%2F'
    codec.urlEncode('中文') = '%E4%B8%AD%E6%96%87'
    codec.urlEncode(null)   = null

urlEncodeBy
------------------------------------
函数定义：``String urlEncodeBy(target, enc)``

- **参数定义：** ``target`` 类型：String/Boolean/Number/Null；``enc`` 类型：String，表示使用的编码；
- **返回类型：** ``String``
- **作用：** 对字符串进行 URL 编码，编码通过 enc 指定。

**例子**

.. code-block:: js
    :linenos:

    codec.urlEncodeBy('UTF-8', 'abc')  = 'abc'
    codec.urlEncodeBy('UTF-8', 12345)  = '12345'
    codec.urlEncodeBy('UTF-8', true)   = 'true'
    codec.urlEncodeBy('UTF-8', '/')    = '%2F'
    codec.urlEncodeBy('UTF-8', '中文') = '%E4%B8%AD%E6%96%87'
    codec.urlEncodeBy('UTF-8', null)   = null

urlDecode
------------------------------------
函数定义：``String urlDecode(target)``

- **参数定义：** ``target`` 类型：String/Boolean/Number/Null
- **返回类型：** ``String``
- **作用：** 对字符串进行 URL 解码，使用 UTF-8。

**例子**

.. code-block:: js
    :linenos:

    codec.urlDecode('abc')  = 'abc'
    codec.urlDecode(12345)  = '12345'
    codec.urlDecode(true)   = 'true'
    codec.urlDecode('%2F')  = '/'
    codec.urlDecode('%E4%B8%AD%E6%96%87') = '中文'
    codec.urlDecode(null)   = null

urlDecodeBy
------------------------------------
函数定义：``String urlDecodeBy(target, enc)``

- **参数定义：** ``target`` 类型：String/Boolean/Number/Null；``enc`` 类型：String，表示使用的编码；
- **返回类型：** ``String``
- **作用：** 对字符串进行 URL 解码，编码通过 enc 指定。

**例子**

.. code-block:: js
    :linenos:

    codec.urlDecodeBy('UTF-8', 'abc')  = 'abc'
    codec.urlDecodeBy('UTF-8', 12345)  = '12345'
    codec.urlDecodeBy('UTF-8', true)   = 'true'
    codec.urlDecodeBy('UTF-8', '%2F')  = '/'
    codec.urlDecodeBy('UTF-8', '%E4%B8%AD%E6%96%87') = '中文'
    codec.urlDecodeBy('UTF-8', null)   = null

digestBytes
------------------------------------
函数定义：``String digestBytes(digestType, content)``

- **参数定义：** ``digestType`` 类型：String，摘要算法；``content`` 类型：List<Byte>，内容；
- **返回类型：** ``String``
- **作用：** 指定摘要算法，对字节数组进行摘要计算。

可用的摘要算法有

+-----------------------------+
| 算法                        |
+---------+---------+---------+
| MD5     | SHA     | SHA1    |
+---------+---------+---------+
| SHA256  | SHA512  |         |
+---------+---------+---------+

**例子**

.. code-block:: js
    :linenos:

    codec.digestBytes("sha1",[123]) = [96,-70,75,45,-86,78,-44,-48,112,-2,-64,102,-121,-30,73,-32,-26,-7,-18,69]
    codec.digestBytes("sha1",[])    = [-38,57,-93,-18,94,107,75,13,50,85,-65,-17,-107,96,24,-112,-81,-40,7,9]
    codec.digestBytes("sha1",null)  = null

digestString
------------------------------------
函数定义：``String digestString(digestType, content)``

- **参数定义：** ``digestType`` 类型：String，摘要算法；``content`` 类型：String，内容；
- **返回类型：** ``String``
- **作用：** 指定摘要算法，对字节数组进行摘要计算。

可用的摘要算法有

+-----------------------------+
| 算法                        |
+---------+---------+---------+
| MD5     | SHA     | SHA1    |
+---------+---------+---------+
| SHA256  | SHA512  |         |
+---------+---------+---------+

**例子**

.. code-block:: js
    :linenos:

    codec.digestString("sha1",123)   = [64,-67,0,21,99,8,95,-61,81,101,50,-98,-95,-1,92,94,-53,-37,-66,-17]
    codec.digestString("sha1","123") = [64,-67,0,21,99,8,95,-61,81,101,50,-98,-95,-1,92,94,-53,-37,-66,-17]
    codec.digestString("sha1","")    = [-38,57,-93,-18,94,107,75,13,50,85,-65,-17,-107,96,24,-112,-81,-40,7,9]
    codec.digestString("sha1",null)  = null

hmacBytes
------------------------------------
函数定义：``String hmacBytes(hmacType, signKey, content)``

- **参数定义：** ``hmacType`` 类型：String，摘要算法；``signKey`` 类型：String，签名Key；``content`` 类型：List<Byte>，内容；
- **返回类型：** ``String``
- **作用：** 指定摘要算法，对字节数组进行Hmac签名计算。

可用的摘要算法有

+-------------------------+
| 算法                    |
+------------+------------+
| HmacMD5    | HmacSHA1   |
+------------+------------+
| HmacSHA256 | HmacSHA512 |
+------------+------------+

**例子**

.. code-block:: js
    :linenos:

    codec.hmacBytes("HmacMD5","123456",[])           = yrE4DqhtisyapiOQpYQGqg==
    codec.hmacBytes("HmacMD5","123456",null)         = null
    codec.hmacBytes("HmacSHA1","123456",[123])       = AjmR1rly06LFH2bQSZXHXK489tQ=
    codec.hmacBytes("HmacSHA256","123456",[123,123]) = kF6shCdT9Spi1cXRbVvcBThqZC89/ULBRQxrTrjfAOU=

hmacString
------------------------------------
函数定义：``String hmacString(hmacType, signKey, content)``

- **参数定义：** ``hmacType`` 类型：String，摘要算法；``signKey`` 类型：String，签名Key；``content`` 类型：List<Byte>，内容；
- **返回类型：** ``String``
- **作用：** 指定摘要算法，对字符串进行Hmac签名计算。

可用的摘要算法有

+-------------------------+
| 算法                    |
+------------+------------+
| HmacMD5    | HmacSHA1   |
+------------+------------+
| HmacSHA256 | HmacSHA512 |
+------------+------------+

**例子**

.. code-block:: js
    :linenos:

    codec.hmacString("HmacMD5","123456",[""])    = yrE4DqhtisyapiOQpYQGqg== （如果是数组那么会取第一个元素，空数组视为 null）
    codec.hmacString("HmacMD5","123456","")      = yrE4DqhtisyapiOQpYQGqg==
    codec.hmacString("HmacMD5","123456",null)    = null
    codec.hmacString("HmacSHA1","123456","abc")  = 8a5qSNRnNFqmPnKoy9i6upJBfOU=
    codec.hmacString("HmacSHA256","123456","abc")= 9s7W9Ig//AmBprmUWBn2gBArQwl62O96Dfm94G+z0uQ=
