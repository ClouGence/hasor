--------------------
Json函数库
--------------------
引入Json函数库的方式为：``import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;``

toJson
------------------------------------
函数定义：``String toJson(target)``

- **参数定义：** ``target`` 类型：任意
- **返回类型：** ``String``
- **作用：** 把对象 JSON 序列化。

**例子**

.. code-block:: js
    :linenos:

    json.toJson([])          = "[]"
    json.toJson({})          = "{}"
    json.toJson([0,1,2])     = "[0,1,2]"
    json.toJson({'key':123}) = "{\"key\":123}"
    json.toJson(null)        = "null"

toFmtJson
------------------------------------
函数定义：``String toFmtJson(target)``

- **参数定义：** ``target`` 类型：任意
- **返回类型：** ``String``
- **作用：** 把对象 JSON 序列化（带格式）。

**例子**

.. code-block:: js
    :linenos:

    json.toFmtJson([])          = "[]"
    json.toFmtJson({})          = "{}"
    json.toFmtJson([0,1,2])     = "[\n\t0,\n\t1,\n\t2\n]"
    json.toFmtJson({'key':123}) = "{\n\t\"key\":123\n}"
    json.toFmtJson(null)        = "null"


fromJson
------------------------------------
函数定义：``Object fromJson(jsonString)``

- **参数定义：** ``jsonString`` 类型：String
- **返回类型：** ``Object``
- **作用：** 把 Json 格式的字符串解析成对象。

**例子**

.. code-block:: js
    :linenos:

    json.fromJson("[]")                     = []
    json.fromJson("{}")                     = {}
    json.fromJson("[\n\t0,\n\t1,\n\t2\n]")  = [0,1,2]
    json.fromJson("{\n\t\"key\":123\n}")    = {'key':123}
    json.fromJson("null")                   = null
